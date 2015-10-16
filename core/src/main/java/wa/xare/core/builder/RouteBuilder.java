package wa.xare.core.builder;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import wa.xare.core.DefaultRoute;
import wa.xare.core.Route;
import wa.xare.core.node.Node;
import wa.xare.core.node.endpoint.Endpoint;
import wa.xare.core.node.endpoint.EndpointDirection;

public class RouteBuilder {


  public RouteBuilder() {
  }

  public Route buildRoute(JsonObject routeConfiguration)
      throws RouteConfigurationException {

    Route route = new DefaultRoute();
    NodeBuilder nodeBuilder = new NodeBuilder(route);
    

    // validate routeConfig
    // TODO: Build Json Configuration validation system.

    // build incoming endpoint
    JsonObject fromConfig = routeConfiguration.getJsonObject("from");
    if (fromConfig == null) {
      throw new RouteConfigurationException("no incoming endpoint defined.");
    }
    // Add implicit values for the incoming endpoint
    fromConfig.put("type", Endpoint.TYPE_NAME);
    fromConfig
        .put("direction", EndpointDirection.INCOMING.name().toLowerCase());
    
    // Build main pipeline
    JsonObject mainPipeline = new JsonObject();
    mainPipeline.put("nodes", routeConfiguration.getJsonArray("nodes"));
    
    Endpoint fromEndpoint = nodeBuilder.getEndpointInstance(route, fromConfig);
    Node piplineNode = nodeBuilder.getNodeInstance(route, mainPipeline);
    
    route.setIncomingEndpoint(fromEndpoint);
    
    nodeBuilder.getNodeInstance(route, configuration)
    
    
    // build main node pipeline

    return route;
  }

}
