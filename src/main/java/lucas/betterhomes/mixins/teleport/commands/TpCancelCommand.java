package lucas.betterhomes.mixins.teleport.commands;

import com.mojang.brigadier.CommandDispatcher;
import lucas.betterhomes.teleport.TeleportCountdown;
import lucas.betterhomes.teleport.TpaManager;
import net.minecraft.commands.*;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

import static net.minecraft.commands.Commands.literal;

@Mixin(Commands.class)
public class TpCancelCommand {
  @Shadow @Final
  private CommandDispatcher<CommandSourceStack> dispatcher;
  
  @Inject(method = "<init>", at = @At("RETURN"))
  private void init(Commands.CommandSelection commandSelection, CommandBuildContext context, CallbackInfo ci) {
    this.dispatcher.register(literal("tpcancel")
      .executes(ctx -> {
        ServerPlayer player = ctx.getSource().getPlayer();
        TeleportCountdown countdown = TeleportCountdown.getCountdown(Objects.requireNonNull(player));
        if (countdown != null) countdown.cancel();
        TpaManager.tpaRequests.remove(player.getStringUUID());
        TpaManager.ticks.remove(player.getStringUUID());
        return 0;
      })
    );
  }
}