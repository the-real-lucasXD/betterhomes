package lucas.betterhomes.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lucas.betterhomes.Betterhomes;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigManager {
  private static final Path path = FabricLoader.getInstance().getConfigDir().resolve("betterhomes/config.json");
  private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
  private static ConfigManager INSTANCE = new ConfigManager();

  public static void load() throws Exception {
    try {
      if (Files.exists(path)) {
        INSTANCE = GSON.fromJson(Files.readString(path), ConfigManager.class);
        if (INSTANCE == null) {
          INSTANCE = new ConfigManager();
          save();
        }
      } else {
        INSTANCE = new ConfigManager();
        save();
      }
    } catch (Exception e) {
      Betterhomes.handleExceptionThrow(e, "loading configs");
    }
  }

  public static void save() throws Exception {
    try {
      Files.createDirectories(path.getParent());
      if (Files.notExists(path)) Files.createFile(path);
      Files.writeString(path, GSON.toJson(INSTANCE));
    } catch (Exception e) {
      Betterhomes.handleExceptionThrow(e, "saving configs");
    }
  }

  public Config<Integer> teleportCountdownTicks = new Config<>(60, "teleportCountdownTicks");
  public Config<Integer> teleportCooldownTicks = new Config<>(400, "teleportCooldownTicks");
  public Config<Boolean> moveOnTeleport = new Config<>(false, "moveOnTeleport");
  public Config<Boolean> cancelTeleportOnHurt = new Config<>(true, "cancelTeleportOnHurt");
  public Config<Boolean> cancelTeleportOnPlayerHurt = new Config<>(true, "cancelTeleportOnPlayerHurt");
  public Config<Boolean> cancelTeleportOnChatMessage = new Config<>(true, "cancelTeleportOnChatMessage");
  public Config<Boolean> startTeleportWhileMoving = new Config<>(false, "startTeleportWhileMoving");
  public Config<Boolean> enablePhomes = new Config<>(true, "enablePhomes");
  public Config<Integer> phomeLimit = new Config<>(0, "phomeLimit");
  public Config<Integer> phomeLimitPerPlayer = new Config<>(0, "phomeLimitPerPlayer");
  public Config<Boolean> modifyOtherPhomes = new Config<>(false, "modifyOtherPhomes");
  public Config<Boolean> enableHomes = new Config<>(true, "enableHomes");
  public Config<Integer> homeLimit = new Config<>(5, "homeLimit");
}