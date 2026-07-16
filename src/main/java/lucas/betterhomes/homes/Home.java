package lucas.betterhomes.homes;

import lucas.betterhomes.Betterhomes;
import lucas.betterhomes.teleport.LocationData;
import lucas.betterhomes.teleport.TeleportCountdown;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;

public class Home {
  LocationData location;
  String name, description = "";
  
  public Home(ServerPlayer player, String name) {
    this.location = new LocationData(player);
    this.name = name;
  }
  
  public void teleport(ServerPlayer player) {
    if (
      Betterhomes.configs().teleportCountdownTicks.get() == 0 ||
      player.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER)
    ) location.teleport(player);
    else new TeleportCountdown(location, player);
  }
  
  public void setLocation(ServerPlayer source) {
    this.location = new LocationData(source);
  }
  
  public void setName(String name, ServerPlayer source) {
    this.name = name;
  }
  
  public void setDescription(String description, ServerPlayer source) {
    this.description = description;
  }
  
  public String getName() {
    return name;
  }
}
