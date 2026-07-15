package lucas.betterhomes.config;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lucas.betterhomes.Betterhomes;

import java.io.IOException;
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
    catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public Config(T initial, String key) {
    value = initial;
    desc = json.get(key).getAsString();
  }

  public T get() {
    return value;
  }

  public void set(T value) {
    this.value = value;
  }
}
