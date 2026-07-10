package lucas.betterhomes.config.mixins;

import lucas.betterhomes.Betterhomes;
import lucas.betterhomes.config.ConfigManager;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(MinecraftServer.class)
public class SaveConfigs {
  @Inject(method = "saveEverything", at = @At("HEAD"))
  private void onWorldSave(boolean silent, boolean flush, boolean force, CallbackInfoReturnable<Boolean> cir) {
    try {
      ConfigManager.save();
      Betterhomes.LOGGER.info("Saved configs");
    } catch (Exception e) {
      Betterhomes.handleException(e, "saving configs");
    }
  }

  @Inject(method = "stopServer", at = @At("HEAD"))
  private void onWorldClose(CallbackInfo ci) {
    try {
      ConfigManager.save();
      Betterhomes.LOGGER.info("SERVER CLOSING: Saved configs");
    } catch (Exception e) {
      Betterhomes.handleException(e, "saving configs for server shutdown");
    }
  }
}
