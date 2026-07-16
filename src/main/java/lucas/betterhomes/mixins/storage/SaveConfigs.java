package lucas.betterhomes.mixins.storage;

import lucas.betterhomes.Betterhomes;
import lucas.betterhomes.storage.ConfigManager;
import lucas.betterhomes.storage.TeleportManager;
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
      TeleportManager.save();
      ConfigManager.save();
      Betterhomes.LOGGER.info("Saved configs");
    } catch (Exception e) {
      Betterhomes.error(e);
    }
  }

  @Inject(method = "stopServer", at = @At("HEAD"))
  private void onWorldClose(CallbackInfo ci) {
    try {
      TeleportManager.save();
      ConfigManager.save();
      Betterhomes.LOGGER.info("SERVER CLOSING: Saved configs");
    } catch (Exception e) {
      Betterhomes.error(e);
    }
  }
}
