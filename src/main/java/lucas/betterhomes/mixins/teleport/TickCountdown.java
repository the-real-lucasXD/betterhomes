package lucas.betterhomes.mixins.teleport;

import lucas.betterhomes.Betterhomes;
import lucas.betterhomes.teleport.TeleportCountdown;
import lucas.betterhomes.teleport.TpaManager;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
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
    for (String player : TpaManager.ticks.keySet()) {
      TpaManager.ticks.replace(player, TpaManager.ticks.get(player) - 1);
      if (TpaManager.ticks.get(player) == 0) {
        Betterhomes.getPlayer(player).sendSystemMessage(
          Component.literal("Your TPA request to ").withColor(TextColor.RED)
          .append(Component.literal(
            Betterhomes.getPlayer(TpaManager.tpaRequests.get(player).getFirst()).getScoreboardName()
          ).withStyle(ChatFormatting.BOLD).withColor(TextColor.RED))
          .append(Component.literal(" has expired!").withStyle(ChatFormatting.RED)),
          false
        ); TpaManager.tpaRequests.remove(player);
        TpaManager.ticks.remove(player);
      }
    }
  }
}
