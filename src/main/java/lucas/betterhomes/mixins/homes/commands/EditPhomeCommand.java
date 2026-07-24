package lucas.betterhomes.mixins.homes.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import lucas.betterhomes.Betterhomes;
import lucas.betterhomes.homes.Home;
import lucas.betterhomes.storage.TeleportManager;
import lucas.betterhomes.homes.Phome;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.*;
import net.minecraft.network.chat.Component;
import net.minecraft.server.permissions.Permissions;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

@Mixin(Commands.class)
public class EditPhomeCommand {
  @Shadow @Final
  private CommandDispatcher<CommandSourceStack> dispatcher;
  
  @Inject(method = "<init>", at = @At("RETURN"))
  private void init(Commands.CommandSelection commandSelection, CommandBuildContext context, CallbackInfo ci) {
    this.dispatcher.register(literal("editphome")
      .requires(source -> Betterhomes.configs().enablePhomes.get()
        || source.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
      .then(argument("name", StringArgumentType.word())
        .suggests((ctx, builder) ->
          SharedSuggestionProvider.suggest(
            TeleportManager.INSTANCE.phomes.values().stream().map(Home::getName).filter(phome -> {
              if (!Betterhomes.configs().modifyOtherPhomes.get())
                return TeleportManager.INSTANCE.phomes.get(phome).creator.equals(
                  Objects.requireNonNull(ctx.getSource().getPlayer()).getStringUUID()
                );
              else return true;
            }),
            builder
          )
        ).then(literal("sethere")
          .executes(ctx -> {
            Phome home = TeleportManager.INSTANCE.phomes.get(StringArgumentType.getString(ctx, "name"));
            if (home == null) {
              ctx.getSource().sendFailure(Component.literal("Cannot perform modification: Unrecognised name."));
              return 0;
            } if (!Betterhomes.configs().modifyOtherPhomes.get() &&
              !home.creator.equals(Objects.requireNonNull(ctx.getSource().getPlayer()).getStringUUID())
            ) {
              ctx.getSource().sendFailure(Component.literal("You cannot modify phomes not created by you!"));
              return 0;
            } home.setLocation(ctx.getSource().getPlayer());
            ctx.getSource().sendSuccess(
              () -> Component.literal("Successfully moved phome ")
                .append(Component.literal(home.getName()).withStyle(ChatFormatting.BOLD))
                .append(" to your current position."),
              false
            ); return 0;
          })
        ).then(literal("setdescription")
          .then(argument("description", StringArgumentType.greedyString())
            .executes(ctx -> {
              Phome home = TeleportManager.INSTANCE.phomes.get(StringArgumentType.getString(ctx, "name"));
              if (home == null) {
                ctx.getSource().sendFailure(Component.literal("Cannot perform modification: Unrecognised name."));
                return 0;
              } if (!Betterhomes.configs().modifyOtherPhomes.get() &&
                !home.creator.equals(Objects.requireNonNull(ctx.getSource().getPlayer()).getStringUUID())
              ) {
                ctx.getSource().sendFailure(Component.literal("You cannot modify phomes not created by you!"));
                return 0;
              } home.setDescription(StringArgumentType.getString(ctx, "description"), ctx.getSource().getPlayer());
              ctx.getSource().sendSuccess(
                () -> Component.literal("Successfully changed the description of phome ")
                  .append(Component.literal(home.getName()).withStyle(ChatFormatting.BOLD))
                  .append("."),
                false
              ); return 0;
            })
          )
        ).then(literal("rename")
          .then(argument("newname", StringArgumentType.word())
            .executes(ctx -> {
              ConcurrentHashMap<String, Phome> homes = TeleportManager.INSTANCE.phomes;
              Phome home = homes.get(StringArgumentType.getString(ctx, "name"));
              if (home == null) {
                ctx.getSource().sendFailure(Component.literal("Cannot perform modification: Unrecognised name."));
                return 0;
              } if (!Betterhomes.configs().modifyOtherPhomes.get() &&
                !home.creator.equals(Objects.requireNonNull(ctx.getSource().getPlayer()).getStringUUID())
              ) {
                ctx.getSource().sendFailure(Component.literal("You cannot modify phomes not created by you!"));
                return 0;
              } if (homes.containsKey(StringArgumentType.getString(ctx, "newname"))) {
                ctx.getSource().sendFailure(
                  Component.literal("Cannot perform modification: A phome exists with the entered name.")
                ); return 0;
              } home.setName(StringArgumentType.getString(ctx, "newname"), ctx.getSource().getPlayer());
              homes.put(StringArgumentType.getString(ctx, "newname"), home);
              homes.remove(StringArgumentType.getString(ctx, "name"));
              ctx.getSource().sendSuccess(
                () -> Component.literal("Successfully renamed phome ").append(
                  Component.literal(StringArgumentType.getString(ctx, "name")).withStyle(ChatFormatting.BOLD)
                ).append(" to ").append(Component.literal(home.getName()).withStyle(ChatFormatting.BOLD).append(".")),
                false
              );
              return 0;
            })
          )
        )
      )
    );
  }
}