package wa.xare.core;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import wa.xare.core.builder.*;
import wa.xare.core.exception.InvalidRouteConfigurationException;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class XareCoreVerticle extends AbstractVerticle {

  private static final Logger LOGGER = LoggerFactory.getLogger(XareCoreVerticle.class);
  public static final String DEFAULT_ADDRESS = "io.xare.core";

  private RouteBuilder routeBuilder;
  private Map<String, JsonObject> routeConfigMap;
  private Map<String, List<String>> deploymentsMap;

  @Override
  public void start() throws Exception {

    routeBuilder = new RouteBuilder();
    routeConfigMap = new HashMap<>();
    deploymentsMap = new HashMap<>();

    this.getVertx().eventBus().consumer(DEFAULT_ADDRESS, this::handleCommand);
    System.out.println("clusterd: " + this.getVertx().isClustered());
    LOGGER.info(String.format("starting core verticle. Listening at '%s'", DEFAULT_ADDRESS));
  }

  @Override
  public void stop() throws Exception {
    super.stop();
  }

  private void handleCommand(Message<JsonObject> message) {
    String command = message.headers().get("command");
    LOGGER.info("received command: " + command);

    switch (command) {
      case "removeRoute": // removes a non-deployed route config
        removeRoute(message);
        break;
      case "listDeployments": // lists all deployed routes by name and deployment ID
        listDeployments(message);
        break;
      case "listComponents": // list all known components. Same consideration as above.
        listComponents(message);
        break;
      case "undeploy": // undeploys a route by deployment ID
        undeployRoute(message);
        break;
      case "showRouteConfiguration": // show route json description
        showRouteConfig(message);
        break;
      case "describeNode": // show node Json template
        describeNode(message);
        break;
      case "listNodes": // list all known nodes, that can be used in a route. Consider also list node packets or groups.
        listNodes(message);
        break;
      case "deploy": // deploy a route by route name, or by new route config. That way the route is added and deployed.
        deployRoute(message);
        break;
      case "add": // adds a new route but does not deploy it.
        addRoute(message);
        break;
      case "listRoutes": // lists all available routes by name
        listRoutes(message);
        break;
      default:
        message.reply(createFailedResponse("Unknown command: " + command));
    }

  }

  protected void removeRoute(Message<JsonObject> message) {
    try {
      String routeName = getField(message, "routeName");
      if (deploymentsMap.containsKey(routeName)) {
        throw new CommandExecutionException("cannot remove route configuration with active deployments.");
      }

      if (routeConfigMap.containsKey(routeName)) {
        JsonObject routeConfig = routeConfigMap.remove(routeName);
        message.reply(createSuccessfulResponse("removed-config", routeConfig));
      } else {
        throw new CommandExecutionException("unknown route: " + routeName);
      }
    } catch (CommandExecutionException e) {
      message.reply(createFailedResponse(e.getMessage()));
    }
  }


  protected void listDeployments(Message<JsonObject> message) {
    JsonObject deployments = new JsonObject();

    String routeName = message.body().getString("routeName");
    if (routeName != null && !routeName.isEmpty()){
      if (deploymentsMap.containsKey("routeName")) {
        deployments.put("routeName", new JsonArray(deployments.getString(routeName)));
      } else {
        message.reply(createFailedResponse("unknown route: " + routeName));
      }
    } else {
      for (Entry<String, List<String>> entry : deploymentsMap.entrySet()) {
        deployments.put(entry.getKey(), new JsonArray(entry.getValue()));
      }
    }

    message.reply(createSuccessfulResponse("deployments", deployments));
  }


  protected void listComponents(Message<JsonObject> message) {
    //TODO: Check package.
    Map<String, ComponentContainer> conatinerMap = NodeDefinitionBuilder.getInstance().getComponentContainerMap();
    JsonObject components = new JsonObject();
    for (Entry<String, ComponentContainer> entry : conatinerMap.entrySet()) {
      String groupName = entry.getKey();
      JsonArray componentNames = new JsonArray(entry.getValue().getComponentNames());
      components.put(groupName, componentNames);
    }

    message.reply(createSuccessfulResponse("components", components));

  }

  protected void undeployRoute(Message<JsonObject> message) {

    String deploymentId = message.body().getString("deploymentId");
    JsonObject response;

    if (deploymentId == null) {
      response = createFailedResponse("deploymentId not defined");
    } else if (!deploymentsMap.containsKey(deploymentId)) {
      response = createFailedResponse("unknown deployment with ID: " + deploymentId);
    } else {

      String routeName = getDeployedRouteName(deploymentId);
      if (routeName == null){
        response = createFailedResponse("deploymentId not defined");
      } else {
        vertx.undeploy(deploymentId);
        List<String> routeDeployments = deploymentsMap.get(routeName);
        routeDeployments.remove(deploymentId);
        if (routeDeployments.isEmpty()){
          deploymentsMap.remove(routeName);
        }
        JsonObject undeployedRoute = new JsonObject();
        undeployedRoute.put("routeName", routeName);
        undeployedRoute.put("deploymentId", deploymentId);
        response = createSuccessfulResponse("undeployed-route", undeployedRoute);
      }
    }
    message.reply(response);
  }

  private String getDeployedRouteName(String deploymentId) {
    for (Entry<String, List<String>> entry : deploymentsMap.entrySet()) {
      if (entry.getValue().contains(deploymentId)){
        return entry.getKey();
      }
    }
    return null;
  }

  protected void showRouteConfig(Message<JsonObject> message) {
    try {
      String routeName = getField(message, "routeName");
      JsonObject routeConfig = getRouteConfig(routeName);
      JsonObject response = createSuccessfulResponse("routeConfig", routeConfig);
      message.reply(response);
    } catch (CommandExecutionException cee) {
      JsonObject response = createFailedResponse(cee.getMessage());
      message.reply(response);
    }
  }

  protected void describeNode(Message<JsonObject> message) {
    try {
      String nodeName = getField(message, "node");
      NodeDefinition nodeDefinition = NodeDefinitionBuilder.getInstance().getNodeDefinition(nodeName);
      JsonObject template = nodeDefinition.getTemplate();
      JsonObject response = createSuccessfulResponse("nodeTemplate", template);
      message.reply(response);
    } catch (CommandExecutionException cee) {
      message.reply(createFailedResponse(cee.getMessage()));
    }
  }

  protected void listNodes(Message<JsonObject> message) {

    // TODO: check node package

    NodeDefinitionBuilder nodeBuilder = NodeDefinitionBuilder.getInstance();
    Map<String, NodeDefinition> nodeDefinitionMap = nodeBuilder.getNodeDefinitionMap();
    List<String> nodeNameList = nodeDefinitionMap.keySet().stream().collect(Collectors.toList());
    JsonArray nodeNames = new JsonArray(nodeNameList);
    JsonObject response = createSuccessfulResponse("nodes", nodeNames);
    message.reply(response);
  }

  protected void listRoutes(Message<JsonObject> message) {
    if (routeConfigMap.isEmpty()){
      message.reply("no routes available");
    } else {
      List<String> routeNameList = routeConfigMap.keySet().stream().collect(Collectors.toList());
      JsonArray routeNames = new JsonArray(routeNameList);
      JsonObject response = createSuccessfulResponse("routes", routeNames);
      message.reply(response);
    }
  }

  protected void addRoute(Message<JsonObject> message) {
    JsonObject routeConfig = message.body();

    try {
      validateRouteConfig(routeConfig);
    } catch (InvalidRouteConfigurationException ex) {
      LOGGER.debug("route configuration invalid", ex);
      message.reply(ex.getMessage());
    }

    String routeName = routeConfig.getString("name");
    routeConfigMap.put(routeName, routeConfig);

    message.reply("route configuration added");
  }

  protected void deployRoute(Message<JsonObject> message) {

    try {
      String routeName = getField(message, "name");
      JsonObject routeConfig = getRouteConfig(routeName);
      Route route = routeBuilder.buildRoute(routeConfig);
      getVertx().deployVerticle(route, new DeploymentOptions().setWorker(true), event -> {

        if (!deploymentsMap.containsKey(routeName)) {
          deploymentsMap.put(routeName, new ArrayList<>());
        }
        deploymentsMap.get(routeName).add(route.getDeploymentId());

        message.reply(createSuccessfulResponse("routeId", route.getDeploymentId()));
      });

    } catch (CommandExecutionException | RouteConfigurationException e) {
      message.reply(createFailedResponse(e.getMessage()));
    }
  }

  /**
   * Validates route config by actually building the route.
   *
   * @param routeConfig the route configuration to build
   * @throws InvalidRouteConfigurationException if config is invalid
   */
  protected void validateRouteConfig(JsonObject routeConfig) throws InvalidRouteConfigurationException {
    if (!routeConfig.containsKey("name") || routeConfig.getString("name").isEmpty()) {
      throw new InvalidRouteConfigurationException(InvalidRouteConfigurationException.MISSING_REQUIRED_FIELD, "name");
    }

    String routeName = routeConfig.getString("name");
    if (routeConfigMap.containsKey(routeName)) {
      throw new InvalidRouteConfigurationException("Route name '%s' already used.", routeName);
    }

    try {
      routeBuilder.buildRoute(routeConfig);
    } catch (RouteConfigurationException e) {
      throw new InvalidRouteConfigurationException(e.getMessage(), e);
    }
  }

  private JsonObject createSuccessfulResponse(String bodyTitle, Object body) {
    Objects.requireNonNull(bodyTitle);
    Objects.requireNonNull(body);

    JsonObject response;
    response = new JsonObject();
    response.put("status", "OK");

    response.put(bodyTitle, body);

    return response;
  }

  private JsonObject createFailedResponse(String errorMessage) {
    JsonObject response;
    response = new JsonObject();
    response.put("status", "FAILED");
    if (errorMessage != null) {
      response.put("error", errorMessage);
    }

    return response;
  }

  private JsonObject getRouteConfig(String routeName) throws CommandExecutionException {
    if (!routeConfigMap.containsKey(routeName)) {
      throw new CommandExecutionException("unknown route name: " + routeName);
    }
    return routeConfigMap.get(routeName);
  }

  private String getField(Message<JsonObject> message, String fieldName) throws CommandExecutionException {
    if (!message.body().containsKey(fieldName)) {
      throw new CommandExecutionException("field not defined: " + fieldName);
    }

    return message.body().getString(fieldName);
  }
}
