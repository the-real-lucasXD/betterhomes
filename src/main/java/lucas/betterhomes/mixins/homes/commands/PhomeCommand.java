package lucas.betterhomes.mixins.homes.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import lucas.betterhomes.Betterhomes;
import lucas.betterhomes.homes.Home;
import lucas.betterhomes.storage.TeleportManager;
import lucas.betterhomes.homes.Phome;
import lucas.betterhomes.teleport.TeleportCountdown;
import net.minecraft.commands.*;
import net.minecraft.network.chat.Component;
import net.minecraft.server.permissions.Permissions;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

@Mixin(Commands.class)
public class PhomeCommand {
  @Shadow @Final
  private CommandDispatcher<CommandSourceStack> dispatcher;
  
  @Inject(method = "<init>", at = @At("RETURN"))
  private void init(Commands.CommandSelection commandSelection, CommandBuildContext context, CallbackInfo ci) {
    this.dispatcher.register(literal("phome")
      .requires(source -> Betterhomes.configs().enablePhomes.get()
        || source.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
      .then(argument("name", StringArgumentType.word())
        .suggests((_, builder) ->
          SharedSuggestionProvider.suggest(TeleportManager.INSTANCE.phomes.values().stream().map(Home::getName), builder)
        ).executes(ctx -> {
          Phome home = TeleportManager.INSTANCE.phomes.get(StringArgumentType.getString(ctx, "name"));
          if (home == null) {
            ctx.getSource().sendFailure(Component.literal("Unrecognised home name."));
            return 0;
          } if (TeleportCountdown.inCountdown(ctx.getSource().getPlayer())) {
            ctx.getSource().sendFailure(Component.literal("You are already in a teleport!"));
            return 0;
          } home.teleport(ctx.getSource().getPlayer());
          return 0;
        })
      )
    );
  }
}