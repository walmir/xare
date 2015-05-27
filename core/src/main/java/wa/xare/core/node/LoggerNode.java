package wa.xare.core.node;

import org.vertx.java.core.logging.Logger;

import wa.xare.core.Route;
import wa.xare.core.packet.Packet;
import wa.xare.core.selector.Selector;

public class LoggerNode extends DefaultRouteNode {

	public static final String LOG_LEVEL_FIELD = "loglevel";

	public static final String INFO = "info";
	public static final String WARN = "warn";
	public static final String DEBUG = "debug";
	public static final String TRACE = "trace";
	public static final String ERROR = "error";

  private Logger logger;

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

	public void setLevel(String level) {
		this.level = level;
	}

	@Override
	public void setRoute(Route route) {
		super.setRoute(route);
		logger = route.getContainer().logger();
	}

}
