package lucas.betterhomes.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigManager {
  private static final Path path = FabricLoader.getInstance().getConfigDir().resolve("betterhomes/config.json");
  private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
  public static ConfigManager INSTANCE = new ConfigManager();

  public static void load() throws Exception {
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
  }

  public static void save() throws IOException {
    Files.createDirectories(path.getParent());
    if (Files.notExists(path)) Files.createFile(path);
    Files.writeString(path, GSON.toJson(INSTANCE));
  }

  public Config<Integer> teleportCountdownTicks = new Config<>(60, "teleportCountdownTicks");
  public Config<Boolean> cancelTeleportOnMove = new Config<>(false, "cancelTeleportOnMove");
  public Config<Boolean> cancelTeleportOnHurt = new Config<>(true, "cancelTeleportOnHurt");
  public Config<Boolean> cancelTeleportOnPlayerHurt = new Config<>(true, "cancelTeleportOnPlayerHurt");
  public Config<Boolean> cancelTeleportOnChatMessage = new Config<>(true, "cancelTeleportOnChatMessage");
  public Config<Boolean> enablePhomes = new Config<>(true, "enablePhomes");
  public Config<Integer> phomeLimit = new Config<>(0, "phomeLimit");
  public Config<Boolean> modifyOtherPhomes = new Config<>(false, "modifyOtherPhomes");
  public Config<Boolean> enableHomes = new Config<>(true, "enableHomes");
  public Config<Integer> homeLimit = new Config<>(5, "homeLimit");
  public Config<Boolean> teleportFacing = new Config<>(true, "teleportFacing");
  public Config<Boolean> takeEnderPearlDamage = new Config<>(false, "takeEnderPearlDamage");
  public Config<Boolean> useDialogs = new Config<>(true, "useDialogs");
  public Config<Boolean> enableTpa = new Config<>(true, "enableTpa");
  public Config<Integer> tpaExpiryTicks = new Config<>(600, "tpaExpiryTicks");
}