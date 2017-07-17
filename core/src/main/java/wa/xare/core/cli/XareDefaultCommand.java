package wa.xare.core.cli;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.cli.CLIException;
import io.vertx.core.cli.annotations.DefaultValue;
import io.vertx.core.cli.annotations.Name;
import io.vertx.core.cli.annotations.Option;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.spi.launcher.DefaultCommand;
import io.vertx.ext.sync.Sync;
import wa.xare.core.XareCoreVerticle;

import java.util.Collections;
import java.util.Map;

/**
 * Base class for all Xare CLI commands.
 */
public abstract class XareDefaultCommand extends DefaultCommand {

  private Logger LOGGER = LoggerFactory.getLogger(XareDefaultCommand.class);

//  private Vertx vertx;

  private String address;

  private String commandName;

  @Option(longName = "address", shortName = "a")
  @DefaultValue(XareCoreVerticle.DEFAULT_ADDRESS)
  public void setAddress(String address) {
    this.address = address;
  }

  public XareDefaultCommand() {
    initializeVertx();
  }

  private void initializeVertx() {
    VertxOptions options = new VertxOptions();

    Sync.awaitEvent(asyncWaitHandler -> Vertx.clusteredVertx(options, h -> {
      if (h.succeeded()) {

        Vertx vertx = h.result();
        EventBus eventBus = vertx.eventBus();

//
//        eventBus.send(address, command, opts, resp -> {
//          if (resp.succeeded()) {
//            System.out.println("results:" + resp.result().body());
//          } else {
//            System.out.println(resp.cause().getMessage());
//          }
//          vertx.close();
//        });

      } else {
        LOGGER.error("failed clusterd vertx init: " + h.cause().getMessage(), h.cause().getCause());
      }
      asyncWaitHandler.handle(this);
    }));

  }

//  /**
//   * Set vertx. Used for testing purposes.
//   * @param vertx vertx instance
//   */
//  public void setVertx(Vertx vertx) {
//    this.vertx = vertx;
//  }

  /**
   * Override if delivery options headers have to be added.
   * @return delivery options headers.
   */
  protected Map<String, String> createHeaders() {
    return Collections.emptyMap();
  }

  /**
   * Override if command body is required.
   * @return command body
   */
  protected JsonObject commandBody() {
    return new JsonObject();
  }

  /**
   * Gets command name from the {@code @Name} annotation of the command class.
   * Override this method if the core vertical command has a different name.
   * @return command name
   */
  protected String commandName() {
    Name annotation = this.getClass().getAnnotation(Name.class);
    if (annotation != null) {
      return annotation.value();
    } else {
      throw new IllegalStateException("command has no name");
    }
  }

  @Override
  public final void run() throws CLIException {
    DeliveryOptions opts = new DeliveryOptions();
    opts.addHeader("command", commandName());
//    Object message = buildMessage();
//
//    Runner
//
//    if (vertx == null) {
//      LOGGER.error("Vertx initialization failed");
//      return;
//    }

//    vertx.eventBus().send(address, message, opts, res -> {
//      if (res.succeeded()) {
//        handleCommandResponse(res.result());
//      } else {
//        handleFailedCommand(res.cause());
//      }
//    });

  }

  protected abstract void handleCommandResponse(Message<Object> result);

  protected abstract void handleFailedCommand(Throwable cause);

}
