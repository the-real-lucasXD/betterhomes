package lucas.betterhomes;

import lucas.betterhomes.storage.ConfigManager;
import lucas.betterhomes.storage.TeleportManager;
import net.fabricmc.api.ModInitializer;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class Betterhomes implements ModInitializer {
	public static final String MOD_ID = "betterhomes";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
  public static MinecraftServer server;

	@Override
	public void onInitialize() {
    try {
      TeleportManager.load();
      ConfigManager.load();
      LOGGER.info("Successfully loaded betterhomes mod");
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
  
  public static void error(Exception e) {
    Betterhomes.LOGGER.error(e.getMessage());
  }
  
  public static ConfigManager configs() {
    return ConfigManager.INSTANCE;
  }
  
  public static ServerPlayer getPlayer(String uuid) {
    return server.getPlayerList().getPlayer(UUID.fromString(uuid));
  }
}
