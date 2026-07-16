package lucas.betterhomes.gui;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.JsonOps;
import lucas.betterhomes.Betterhomes;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.server.dialog.Dialog;
import net.minecraft.server.level.ServerPlayer;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class GuiManager {
  @SafeVarargs
  public static void display(String identifier, ServerPlayer player, Pair<String, String>... variables) {
    JsonObject json = JsonParser.parseReader(new InputStreamReader(
      Objects.requireNonNull(Betterhomes.class.getResourceAsStream("/screens/" + identifier + ".json")),
      StandardCharsets.UTF_8
    )).getAsJsonObject();
    
    String _final;
    if (Betterhomes.configs().useDialogs.get()) _final = json.get("dialog").getAsString();
    else _final = json.get("no-dialog").getAsString();
    
    for (Pair<String, String> variable : variables)
      _final = _final.replaceAll("\\$\\{"+variable.getFirst()+ "}", variable.getSecond());
    
    if (_final.startsWith("chat_")) {
      _final = _final.replace("chat_", "");
      player.sendSystemMessage(ComponentSerialization.CODEC
        .decode(JsonOps.INSTANCE, JsonParser.parseString(_final))
        .getPartialOrThrow()
        .getFirst(), false
      );
    } else if (_final.startsWith("dialog_")) {
      _final = _final.replace("dialog_", "");
      player.openDialog(
        Dialog.CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString(_final)).getPartialOrThrow()
      );
    }
  }
}
