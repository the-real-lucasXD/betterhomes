package lucas.betterhomes.mixins.teleport.cancellers;

import lucas.betterhomes.Betterhomes;
import lucas.betterhomes.teleport.TeleportCountdown;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
public class ChatMessage {
  @Inject(
    method = "broadcastChatMessage(Lnet/minecraft/network/chat/PlayerChatMessage;Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/network/chat/ChatType$Bound;)V",
    at = @At("HEAD"), cancellable = true
  ) private void broadcastChatMessage(
    PlayerChatMessage message, ServerPlayer sender, ChatType.Bound chatType, CallbackInfo ci
  ) {
    if (!Betterhomes.configs().cancelTeleportOnChatMessage.get()) return;
    TeleportCountdown countdown = TeleportCountdown.getCountdown(sender);
    if (countdown != null) {
      countdown.cancel();
      sender.sendSystemMessage(
        Component.literal("Teleport cancelled! (You sent a chat message)").withColor(TextColor.RED),
      true);
      countdown.cancelled = true;
      ci.cancel();
    }
  }
}
