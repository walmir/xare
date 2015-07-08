package wa.xare.core.node;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import wa.xare.core.api.Packet;
import wa.xare.core.api.Route;
import wa.xare.core.api.annotation.NodeType;
import wa.xare.core.api.configuration.NodeConfiguration;
import wa.xare.core.api.processing.ProcessingResult;
import wa.xare.core.selector.Selector;

@NodeType("logger")
public class LoggerNode extends AbstractNode {

  public static final String TYPE_NAME = "logger";

  public static final String LOG_LEVEL_FIELD = "level";
  private static final String DEFAULT_LOG_LEVEL = "info";

	public static final String INFO = "info";
	public static final String WARN = "warn";
	public static final String DEBUG = "debug";
	public static final String TRACE = "trace";
	public static final String ERROR = "error";

  private Logger logger = LoggerFactory
      .getLogger(LoggerNode.class);
	private String level = INFO;


	@Override
	public void startProcessing(Packet packet) {
		Object message;

		Selector selector = getSelector();
		if (selector != null) {
			message = selector.getSelection(packet);
		} else {
			message = packet.getBody();
		}

		switch (level) {
		case INFO:
      logger.info("LOGGER-NODE: " + message);
			break;
		case DEBUG:
      logger.debug(message);
			break;
		case WARN:
      logger.warn(message);
			break;
		case TRACE:
      logger.trace(message);
			break;
		case ERROR:
      logger.error(message);
			break;

		default:
			throw new IllegalArgumentException("unkown log level: " + level);
		}

		ProcessingResult result = ProcessingResult
				.successfulProcessingResult(packet);
		notifyProcessingListeners(result);
	}

  public String getLevel() {
    return level;
  }

	public void setLevel(String level) {
    this.level = level;
	}

	@Override
	public void setRoute(Route route) {
		super.setRoute(route);
	}

  @Override
  protected void doConfigure(NodeConfiguration configuration) {
    if (configuration.containsKey(LOG_LEVEL_FIELD)) {
      setLevel(configuration.getString(LoggerNode.LOG_LEVEL_FIELD));
    } else {
      setLevel(DEFAULT_LOG_LEVEL);
    }
  }

  // protected Logger getLogger() {
  // return logger;
  // }
  //
  // protected void setLogger(Logger logger) {
  // this.logger = logger;
  // }

}
