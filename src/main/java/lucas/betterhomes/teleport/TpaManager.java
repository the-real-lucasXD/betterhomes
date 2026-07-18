package lucas.betterhomes.teleport;

import com.mojang.datafixers.util.Pair;
import lucas.betterhomes.Betterhomes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class TpaManager {
  public static ConcurrentHashMap<String, Pair<String, Boolean>> tpaRequests = new ConcurrentHashMap<>();

  public static void teleport(String acceptor, Pair<String, Boolean> sender) {
    ServerPlayer receiver = Betterhomes.getPlayer(acceptor), requester = Betterhomes.getPlayer(sender.getFirst());
    if (sender.getSecond()) {
      LocationData location = new LocationData(receiver);
      if (Betterhomes.configs().teleportCountdownTicks.get() == 0 ||
        requester.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER)
      ) location.teleport(requester);
      else new TeleportCountdown(location, requester);
    } else {
      LocationData location = new LocationData(requester);
      if (Betterhomes.configs().teleportCountdownTicks.get() == 0 ||
        receiver.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER)
      ) location.teleport(requester);
      else new TeleportCountdown(location, receiver);
    }
  }

  public static ArrayList<Pair<String, Boolean>> getSender(String player) {
    ArrayList<Pair<String, Boolean>> list = new ArrayList<>();
    for (String i : tpaRequests.keySet()) {
      if (tpaRequests.get(i).getFirst().equals(player)) list.add(new Pair<>(i, tpaRequests.get(i).getSecond()));
    } return list;
  }

  public static ArrayList<Pair<String, String>> getVars(Object rawRequest) {
    if (rawRequest instanceof Pair<?, ?> requestHandler) {
      if (requestHandler.getFirst() instanceof String requestFirst &&
        requestHandler.getSecond() instanceof Boolean requestSecond
      ) {
        Pair<String, Boolean> request = Pair.of(requestFirst, requestSecond);
        ArrayList<Pair<String, String>> vars = new ArrayList<>();
        if (request.getSecond()) {
          vars.add(new Pair<>("prompt1", ""));
          vars.add(new Pair<>("prompt2", " to you"));
        } else {
          vars.add(new Pair<>("prompt1", "You to "));
          vars.add(new Pair<>("prompt2", ""));
        } vars.add(new Pair<>("player", Betterhomes.getPlayer(request.getFirst()).getScoreboardName()));
        return vars;
      } else return new ArrayList<>();
    } else return new ArrayList<>();
  }
}
