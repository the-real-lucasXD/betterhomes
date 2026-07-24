package lucas.betterhomes.homes;

import lucas.betterhomes.teleport.LocationData;
import net.minecraft.server.level.ServerPlayer;

public class Home {
  LocationData location;
  String name, description = "";
  
  public Home(ServerPlayer player, String name) {
    this.location = new LocationData(player);
    this.name = name;
  }
  
  public void teleport(ServerPlayer player) {
    location.teleport(player);
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
