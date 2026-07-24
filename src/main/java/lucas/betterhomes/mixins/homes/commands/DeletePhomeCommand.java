package lucas.betterhomes.mixins.homes.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import lucas.betterhomes.Betterhomes;
import lucas.betterhomes.homes.Home;
import lucas.betterhomes.storage.TeleportManager;
import lucas.betterhomes.homes.Phome;
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
public class DeletePhomeCommand {
  @Shadow @Final
  private CommandDispatcher<CommandSourceStack> dispatcher;
  
  @Inject(method = "<init>", at = @At("RETURN"))
  private void init(Commands.CommandSelection commandSelection, CommandBuildContext context, CallbackInfo ci) {
    this.dispatcher.register(literal("deletephome")
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
        ).executes(ctx -> {
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
          } homes.remove(StringArgumentType.getString(ctx, "name"));
          ctx.getSource().sendSuccess(() -> Component.literal("Successfully deleted phome."), false);
          return 0;
        })
      )
    );
  }
}