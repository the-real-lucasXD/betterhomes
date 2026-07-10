package lucas.betterhomes;

import lucas.betterhomes.config.ConfigManager;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Betterhomes implements ModInitializer {
	public static final String MOD_ID = "betterhomes";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
    try {
      ConfigManager.load();
      LOGGER.info("Successfully loaded betterhomes mod");
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
	}

  public static void handleException(Exception e, String source) {
    var exception = e.getClass().cast(e);
    LOGGER.error("Error when {}", source, exception);
  }

  public static void handleExceptionThrow(Exception e, String source) throws Exception {
    var exception = e.getClass().cast(e);
    LOGGER.error("Error when {}", source, exception);
    throw exception;
  }
}
