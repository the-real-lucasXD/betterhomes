package lucas.betterhomes.homes;

import java.time.LocalDateTime;

public record EditLog(String player, String action, String time) {
  public EditLog(String player, String action) {
    this(player, action, LocalDateTime.now().toString());
  }
}
