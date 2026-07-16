package lucas.betterhomes.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lucas.betterhomes.homes.Home;
import lucas.betterhomes.homes.Phome;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ConcurrentHashMap;

public class TeleportManager {
  public ConcurrentHashMap<String, ConcurrentHashMap<String, Home>> homes = new ConcurrentHashMap<>();
  public ConcurrentHashMap<String, Phome> phomes = new ConcurrentHashMap<>();
  
  private static final Path path = FabricLoader.getInstance().getConfigDir().resolve("betterhomes/homes.json");
  private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
  public static TeleportManager INSTANCE = new TeleportManager();
  
  public static void load() throws Exception {
    if (Files.exists(path)) {
      INSTANCE = GSON.fromJson(Files.readString(path), TeleportManager.class);
      if (INSTANCE == null) {
        INSTANCE = new TeleportManager();
        save();
      }
    } else {
      INSTANCE = new TeleportManager();
      save();
    }
  }
  
  public static void save() throws IOException {
    Files.createDirectories(path.getParent());
    if (Files.notExists(path)) Files.createFile(path);
    Files.writeString(path, GSON.toJson(INSTANCE));
  }
}
