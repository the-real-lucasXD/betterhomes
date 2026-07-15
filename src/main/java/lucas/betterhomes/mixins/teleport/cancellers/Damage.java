package lucas.betterhomes.mixins.teleport.cancellers;

import lucas.betterhomes.Betterhomes;
import lucas.betterhomes.teleport.TeleportCountdown;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayer.class)
public class Damage {
  @Inject(method = "hurtServer", at = @At("HEAD"))
  private void hurtServer(ServerLevel level, DamageSource source, float damage, CallbackInfoReturnable<Boolean> cir) {
    ServerPlayer player = (ServerPlayer) (Object) this;
    TeleportCountdown countdown = TeleportCountdown.getCountdown(player);
    if (countdown != null) {
      if (Betterhomes.configs().cancelTeleportOnHurt.get()) {
        countdown.cancelled = true;
        countdown.cancel();
        player.sendSystemMessage(
          Component.literal("Teleport cancelled! (You took damage)").withColor(TextColor.RED),
          true
        );
      } else if (Betterhomes.configs().cancelTeleportOnPlayerHurt.get()) {
        Entity indirectSource = source.getEntity();
        Entity directSource = source.getDirectEntity();
        
        if (indirectSource instanceof ServerPlayer || directSource instanceof ServerPlayer) {
          countdown.cancelled = true;
          countdown.cancel();
          player.sendSystemMessage(
            Component.literal("Teleport cancelled! (You were attacked by a player)").withColor(TextColor.RED),
            true
          );
        }
      }
    }
  }
}
