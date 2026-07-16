package lucas.betterhomes.homes;

import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;

public class Phome extends Home {
  public String creator;
  public ArrayList<EditLog> logs;
  
  public Phome(ServerPlayer player, String name) {
    super(player, name);
    this.creator = player.getStringUUID();
    this.logs = new ArrayList<>();
  }
  
  @Override
  public void setLocation(ServerPlayer source) {
    super.setLocation(source);
    logs.add(new EditLog(source.getStringUUID(), "changed location"));
  }
  
  @Override
  public void setName(String name, ServerPlayer source) {
    super.setName(name, source);
    logs.add(new EditLog(source.getStringUUID(), "changed name"));
  }
  
  @Override
  public void setDescription(String description, ServerPlayer source) {
    super.setDescription(description, source);
    logs.add(new EditLog(source.getStringUUID(), "changed description"));
  }
}
