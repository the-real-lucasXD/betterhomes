package lucas.betterhomes.teleport;

import lucas.betterhomes.Betterhomes;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

import java.util.Collections;
import java.util.Objects;

public class LocationData {
  public String dimension;
  
  public double x;
  public double y;
  public double z;
  
  public float yawX;
  public float yawY;
  
  
  public void teleport(ServerPlayer player, boolean check) {
    Betterhomes.LOGGER.info(String.valueOf(player.getDeltaMovement().lengthSqr()));
    if (!check ||
      Betterhomes.configs().teleportCountdownTicks.get() == 0 ||
      player.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER)
    ) {
      ServerLevel level = Betterhomes.server.getLevel(
        ResourceKey.create(Registries.DIMENSION, Identifier.parse(dimension))
      );
      if (Betterhomes.configs().teleportFacing.get()) player.teleportTo(
        Objects.requireNonNull(level),
        x, y, z, Collections.emptySet(),
        yawY, yawX, true
      );
      else player.teleportTo(
        Objects.requireNonNull(level),
        x, y, z, Collections.emptySet(),
        player.getYRot(), player.getXRot(), true
      );
      Identifier rawSoundPath = Identifier.withDefaultNamespace("entity.player.teleport");
      Holder<SoundEvent> soundHolder = Holder.direct(SoundEvent.createVariableRangeEvent(rawSoundPath));
      
      player.connection.send(new ClientboundSoundPacket(
        soundHolder,
        SoundSource.PLAYERS,
        player.getX(), player.getY(), player.getZ(),
        1.0f, 1.0f,
        player.getRandom().nextLong()
      ));
      if (Betterhomes.configs().takeEnderPearlDamage.get())
        player.hurtServer(level, player.damageSources().enderPearl(), 5.0f);
    } else new TeleportCountdown(this, player);
  }
  
  public void teleport(ServerPlayer player) {
    teleport(player, true);
  }
  
  @SuppressWarnings("resource")
  public LocationData(ServerPlayer player) {
    ServerLevel level = player.level();
    this.dimension = level.dimension().identifier().toString();
    this.x = player.getX();
    this.y = player.getY();
    this.z = player.getZ();
    this.yawX = player.getXRot();
    this.yawY = player.getYRot();
  }
}
