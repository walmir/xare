package wa.xare.core;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import wa.xare.core.builder.RouteBuilder;
import wa.xare.core.builder.RouteConfigurationException;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wajdi on 18/10/15.
 */
public class XareCoreVerticle extends AbstractVerticle {

  private static final Logger LOGGER = LoggerFactory.getLogger(XareCoreVerticle.class);

  public static final String DEFAULT_ADDRESS = "io.xare.core";
  private RouteBuilder routeBuilder;

  private Map<String, Route> routeMap;

  @Override
  public void start() throws Exception {
    LOGGER.info(String.format("starting core verticle. Listening at '%s'", DEFAULT_ADDRESS));

    routeBuilder = new RouteBuilder();
    routeMap = new HashMap<>();

    this.getVertx().eventBus().consumer(DEFAULT_ADDRESS, this::handleCommand);
  }

  private void handleCommand(Message<JsonObject> message) {
    String command = message.headers().get("command");

    switch (command) {
      case "listRoutes": // lists all available routes by name
      case "listDeployments": // lists all deployed routes by name and deployment ID
      case "listNodes": // list all known nodes, that can be used in a route. Consider also list node packets or groups.
      case "listComponents": // list all known components. Same consideration as above.
      case "deploy": // deploy a route by route name, or by new route config. That way the route is added and deployed.
      case "undeploy": // undeployes a route by deployment ID
      case "remove":
        LOGGER.warn("unimplemented command");
      case "add": // adds a new route but does not deploy it.
        addRoute(message.body());
    }


  }

  private void addRoute(JsonObject routeConfig) {

    try {
      Route route = routeBuilder.buildRoute(routeConfig);
      getVertx().deployVerticle(route, new DeploymentOptions().setWorker(true), event -> {
        routeMap.put(route.getDeploymentId(), route);
      });
      getVertx().undeploy("");
    } catch (RouteConfigurationException e) {
      e.printStackTrace();
    }

  }

  @Override
  public void stop() throws Exception {
    super.stop();
  }
}
