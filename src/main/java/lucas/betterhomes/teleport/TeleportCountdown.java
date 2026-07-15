package lucas.betterhomes.teleport;

import lucas.betterhomes.Betterhomes;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.BossEvent;

import java.util.HashMap;
import java.util.UUID;

public class TeleportCountdown {
  public boolean cancelled = false;
  public int ticks;
  public LocationData destination;
  public String player;
  public ServerBossEvent bossBar = new ServerBossEvent(
    UUID.randomUUID(),
    Component.literal("Teleporting"),
    BossEvent.BossBarColor.GREEN,
    BossEvent.BossBarOverlay.PROGRESS
  );
  
  public static HashMap<String, TeleportCountdown> countdowns = new HashMap<>();
  
  public TeleportCountdown(LocationData destination, ServerPlayer player) {
    ticks = Betterhomes.configs().teleportCountdownTicks.get()+1;
    this.destination = destination;
    this.player = player.getStringUUID();
    countdowns.put(this.player, this);
    bossBar.addPlayer(player);
    tick();
  }
  
  public void cancel() {
    countdowns.remove(this.player);
    bossBar.removeAllPlayers();
  }
  
  public void tick() {
    if (cancelled) return;
    ticks --;
    bossBar.setName(Component.literal(String.format("Teleporting: %.2fs", (double) ticks/20)));
    bossBar.setProgress((float) ticks /Betterhomes.configs().teleportCountdownTicks.get());
    
    if (Betterhomes.configs().cancelTeleportOnChatMessage.get())
      Betterhomes.getPlayer(this.player).sendSystemMessage(
        Component.literal(
          "Send any chat message to cancel teleport. (message will not be sent)"
        ).withColor(TextColor.GREEN), true
      );
    
    if (ticks == 0) {
      cancel();
      destination.teleport(Betterhomes.getPlayer(this.player));
    } else if (ticks%20 == 0) {
      ServerPlayer player = Betterhomes.getPlayer(this.player);
      
      Identifier rawSoundPath = Identifier.withDefaultNamespace("entity.experience_orb.pickup");
      Holder<SoundEvent> soundHolder = Holder.direct(SoundEvent.createVariableRangeEvent(rawSoundPath));
      
      player.connection.send(new ClientboundSoundPacket(
        soundHolder,
        SoundSource.PLAYERS,
        player.getX(), player.getY(), player.getZ(),
        1.0f, 0.5f,
        player.getRandom().nextLong()
      ));
    }
  }
  
  public static TeleportCountdown getCountdown(ServerPlayer player) {
    return countdowns.get(player.getStringUUID());
  }
}
