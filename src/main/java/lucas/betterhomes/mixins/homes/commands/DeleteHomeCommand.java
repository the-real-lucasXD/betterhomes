package lucas.betterhomes.mixins.homes.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import lucas.betterhomes.Betterhomes;
import lucas.betterhomes.homes.Home;
import lucas.betterhomes.storage.TeleportManager;
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

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

@Mixin(Commands.class)
public class DeleteHomeCommand {
  @Shadow @Final
  private CommandDispatcher<CommandSourceStack> dispatcher;
  
  @Inject(method = "<init>", at = @At("RETURN"))
  private void init(Commands.CommandSelection commandSelection, CommandBuildContext context, CallbackInfo ci) {
    this.dispatcher.register(literal("deletehome")
      .requires(source -> Betterhomes.configs().enableHomes.get()
        || source.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
      .then(argument("name", StringArgumentType.word())
        .suggests((ctx, builder) ->
          SharedSuggestionProvider.suggest(TeleportManager.INSTANCE.homes.get(
            Objects.requireNonNull(ctx.getSource().getPlayer()).getStringUUID()
          ).values().stream().map(Home::getName), builder)
        ).executes(ctx -> {
          Home home = TeleportManager.INSTANCE.homes.get(
            Objects.requireNonNull(ctx.getSource().getPlayer()).getStringUUID()
          ).remove(StringArgumentType.getString(ctx, "name"));
          if (home == null) ctx.getSource().sendFailure(Component.literal("Unrecognised home name."));
          else ctx.getSource().sendSuccess(() -> Component.literal("Successfully deleted home."), false);
          return 0;
        })
      )
    );
  }
}