package lucas.betterhomes.mixins.homes.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import lucas.betterhomes.Betterhomes;
import lucas.betterhomes.homes.Home;
import lucas.betterhomes.storage.TeleportManager;
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
public class EditHomeCommand {
  @Shadow @Final
  private CommandDispatcher<CommandSourceStack> dispatcher;
  
  @Inject(method = "<init>", at = @At("RETURN"))
  private void init(Commands.CommandSelection commandSelection, CommandBuildContext context, CallbackInfo ci) {
    this.dispatcher.register(literal("edithome")
      .requires(source -> Betterhomes.configs().enableHomes.get()
        || source.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
      .then(argument("name", StringArgumentType.word())
        .suggests((ctx, builder) ->
          SharedSuggestionProvider.suggest(TeleportManager.INSTANCE.homes.get(
            Objects.requireNonNull(ctx.getSource().getPlayer()).getStringUUID()
          ).values().stream().map(Home::getName), builder)
        ).then(literal("sethere")
          .executes(ctx -> {
            Home home = TeleportManager.INSTANCE.homes.get(
              Objects.requireNonNull(ctx.getSource().getPlayer()).getStringUUID()
            ).get(StringArgumentType.getString(ctx, "name"));
            if (home == null) {
              ctx.getSource().sendFailure(Component.literal("Cannot perform modification: Unrecognised name."));
              return 0;
            } home.setLocation(ctx.getSource().getPlayer());
            ctx.getSource().sendSuccess(
              () -> Component.literal("Successfully moved home ")
                .append(Component.literal(home.getName()).withStyle(ChatFormatting.BOLD))
                .append(" to your current position."),
              false
            ); return 0;
          })
        ).then(literal("setdescription")
          .then(argument("description", StringArgumentType.greedyString())
            .executes(ctx -> {
              Home home = TeleportManager.INSTANCE.homes.get(
                Objects.requireNonNull(ctx.getSource().getPlayer()).getStringUUID()
              ).get(StringArgumentType.getString(ctx, "name"));
              if (home == null) {
                ctx.getSource().sendFailure(Component.literal("Cannot perform modification: Unrecognised name."));
                return 0;
              } home.setDescription(StringArgumentType.getString(ctx, "description"), ctx.getSource().getPlayer());
              ctx.getSource().sendSuccess(
                () -> Component.literal("Successfully changed the description of home ")
                  .append(Component.literal(home.getName()).withStyle(ChatFormatting.BOLD))
                  .append("."),
                false
              ); return 0;
            })
          )
        ).then(literal("rename")
          .then(argument("newname", StringArgumentType.word())
            .executes(ctx -> {
              ConcurrentHashMap<String, Home> homes = TeleportManager.INSTANCE.homes.get(
                Objects.requireNonNull(ctx.getSource().getPlayer()).getStringUUID()
              );
              Home home = homes.get(StringArgumentType.getString(ctx, "name"));
              if (home == null) {
                ctx.getSource().sendFailure(Component.literal("Cannot perform modification: Unrecognised name."));
                return 0;
              } if (homes.containsKey(StringArgumentType.getString(ctx, "newname"))) {
                ctx.getSource().sendFailure(
                  Component.literal("Cannot perform modification: A home exists with the entered name.")
                ); return 0;
              } home.setName(StringArgumentType.getString(ctx, "newname"), ctx.getSource().getPlayer());
              homes.put(StringArgumentType.getString(ctx, "newname"), home);
              homes.remove(StringArgumentType.getString(ctx, "name"));
              ctx.getSource().sendSuccess(
                () -> Component.literal("Successfully renamed home ").append(
                  Component.literal(StringArgumentType.getString(ctx, "name")).withStyle(ChatFormatting.BOLD)
                ).append(" to ").append(Component.literal(home.getName()).withStyle(ChatFormatting.BOLD).append(".")),
                false
              ); return 0;
            })
          )
        )
      )
    );
  }
}