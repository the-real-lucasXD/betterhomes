package lucas.betterhomes.mixins.teleport;

import lucas.betterhomes.teleport.TeleportCountdown;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public class TickCountdown {
  @Inject(method = "tickServer", at = @At("HEAD"))
  private void tickServer(CallbackInfo ci) {
    for (TeleportCountdown countdown : TeleportCountdown.countdowns.values()) countdown.tick();
  }
}
