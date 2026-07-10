package lucas.betterhomes.config;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lucas.betterhomes.Betterhomes;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class Config<T> {
  public String desc;
  public T value;
  public static JsonObject json;

  static {
    try (
      InputStreamReader reader = new InputStreamReader(
        Objects.requireNonNull(Betterhomes.class.getResourceAsStream("/descriptions.json")),
        StandardCharsets.UTF_8
      )
    ) { json = JsonParser.parseReader(reader).getAsJsonObject(); }
    catch (Exception e) { Betterhomes.handleException(e, "obtaining descriptions resource"); }
  }

  public Config(T initial, String key) {
    value = initial;
    desc = json.get(key).getAsString();
  }

  public String description() {
    return desc;
  }

  public T get() {
    return value;
  }

  public void set(T value) {
    this.value = value;
  }
}
