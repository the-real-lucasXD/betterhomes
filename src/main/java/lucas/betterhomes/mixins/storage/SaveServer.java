package lucas.betterhomes.mixins.storage;

import lucas.betterhomes.Betterhomes;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public class SaveServer {
  @Inject(method = "runServer", at = @At("HEAD"))
  private void runServer(CallbackInfo ci) {
    Betterhomes.server = (MinecraftServer) (Object) this;
  }
}