package lucas.betterhomes.teleport;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class DynamicLocationData extends LocationData {
  ServerPlayer reference;

  public DynamicLocationData(ServerPlayer player) {
    super(player);
  }

  @SuppressWarnings("resource") @Override
  public void teleport(ServerPlayer player) {
    ServerLevel level = reference.level();
    this.dimension = level.dimension().identifier().toString();
    this.x = reference.getX();
    this.y = reference.getY();
    this.z = reference.getZ();
    this.yawX = reference.getXRot();
    this.yawY = reference.getYRot();
    super.teleport(player);
  }
}
