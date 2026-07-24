package lucas.betterhomes.teleport;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class DynamicLocationData extends LocationData {
  ServerPlayer reference;

  public DynamicLocationData(ServerPlayer player) {
    reference = player;
    super(player);
  }

  @SuppressWarnings("resource") @Override
  public void teleport(ServerPlayer player, boolean check) {
    if (check) new TeleportCountdown(this, player);
    else {
      ServerLevel level = reference.level();
      this.dimension = level.dimension().identifier().toString();
      this.x = reference.getX();
      this.y = reference.getY();
      this.z = reference.getZ();
      this.yawX = reference.getXRot();
      this.yawY = reference.getYRot();
      super.teleport(player, false);
    }
  }
}
