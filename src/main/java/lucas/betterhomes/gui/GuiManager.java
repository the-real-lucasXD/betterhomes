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
import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Matcher;

public class GuiManager {
  @SafeVarargs
  public static void display(String identifier, ServerPlayer player, Pair<String, String>... variables) {
    String _final = format(identifier, variables);
    
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

  @SafeVarargs
  public static String format(String identifier, Pair<String, String>... variables) {
    JsonObject json = JsonParser.parseReader(new InputStreamReader(
      Objects.requireNonNull(Betterhomes.class.getResourceAsStream("/screens/" + identifier + ".json")),
      StandardCharsets.UTF_8
    )).getAsJsonObject();
    
    String _final, _previous;
    if (Betterhomes.configs().useDialogs.get()) _final = json.get("dialog").getAsString();
    else _final = json.get("no-dialog").getAsString();

    do {
      _previous = _final.intern();
      for (Pair<String, String> variable : variables)
        _final = _final.replaceAll("\\$\\{" + variable.getFirst() + "}", Matcher.quoteReplacement(variable.getSecond()));
    } while (!_previous.equals(_final));

    return _final;
  }

  @SuppressWarnings("unchecked")
  public static String loop(
    String identifier, Function<Object, ArrayList<Pair<String, String>>> handler, ArrayList<?> variables
  ) {
    StringBuilder _final = new StringBuilder("[");
    
    for (int i=0; i<variables.size(); i++) {
      _final.append(format(identifier,handler.apply(variables.get(i)).toArray(new Pair[0])));
      if (i < variables.size()-1) _final.append(",");
    }
    
    return _final.append("]").toString();
  }
}
