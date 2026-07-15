package lucas.betterhomes.mixins.teleport.cancellers;

import lucas.betterhomes.Betterhomes;
import lucas.betterhomes.teleport.TeleportCountdown;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public class Movement {
  @Unique
  private double lastX = Integer.MIN_VALUE, lastY = Integer.MIN_VALUE, lastZ = Integer.MIN_VALUE;
  @Unique
  private static final double MOVEMENT_TRESHOLD = 0.0001;
  
  @Inject(method = "tick", at = @At("HEAD"))
  private void tick(CallbackInfo ci) {
    if (!Betterhomes.configs().cancelTeleportOnMove.get()) return;
    ServerPlayer player = (ServerPlayer) (Object) this;
    TeleportCountdown countdown = TeleportCountdown.getCountdown(player);
    if (countdown != null) {
      if (Math.abs(player.getX() - lastX) > MOVEMENT_TRESHOLD ||
        Math.abs(player.getY() - lastY) > MOVEMENT_TRESHOLD ||
        Math.abs(player.getZ() - lastZ) > MOVEMENT_TRESHOLD
      ) {
        countdown.cancelled = true;
        countdown.cancel();
        player.sendSystemMessage(
          Component.literal("Teleport cancelled! (You moved)").withColor(TextColor.RED),
          true
        );
      }
    } lastX = player.getX();
    lastY = player.getY();
    lastZ = player.getZ();
  }
}
