package wa.xare.core.web;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import java.util.ArrayList;
import java.util.List;

import static wa.xare.core.XareCoreVerticle.Commands;
import static wa.xare.core.XareCoreVerticle.DEFAULT_ADDRESS;

public class XareWebVerticle extends AbstractVerticle {

  private static final Logger LOGGER = LoggerFactory.getLogger(XareWebVerticle.class);

  private static final String ROUTES_PATH = "routes";
  private static final String NODE_PATH = "nodes";
  private static final String DEPLOYED_PATH = "deployed";
  private static final String COMPONENTS_PATH = "components";


  private Router router;
  private HttpServer server;


  @Override
  public void start() throws Exception {
    super.start();

//    HttpServerOptions serverOptions = new HttpServerOptions().setPort(122122);
    server = vertx.createHttpServer();

    router = Router.router(getVertx());
    router.route().handler(BodyHandler.create());

    buildRoute(HttpMethod.GET, Commands.LIST_ROUTES, ROUTES_PATH);
    buildRoute(HttpMethod.POST, Commands.ADD, ROUTES_PATH);
    buildRoute(HttpMethod.GET, Commands.SHOW_ROUTE_CONFIGURATION, ROUTES_PATH, ":routeName");
    buildRoute(HttpMethod.GET, Commands.DEPLOY, ROUTES_PATH, ":routeName", "deploy");
    buildRoute(HttpMethod.GET, Commands.LIST_DEPLOYMENTS, DEPLOYED_PATH);
    // TODO: route for deployment info
    buildRoute(HttpMethod.GET, Commands.UNDEPLOY, DEPLOYED_PATH, ":deploymentId", "undeploy");
    buildRoute(HttpMethod.DELETE, Commands.REMOVE_ROUTE, ROUTES_PATH, ":routeName");

    buildRoute(HttpMethod.GET, Commands.LIST_COMPONENTS, COMPONENTS_PATH);


    buildRoute(HttpMethod.GET, Commands.LIST_NODES, NODE_PATH);
    buildRoute(HttpMethod.GET, Commands.DESCRIBE_NODE, NODE_PATH, ":node");


    server.requestHandler(router::accept).listen(8080);
  }


  /**
   * Builds route
   *
   * @param httpMethod http method
   * @param command xare command
   * @param path path segments, prefix parameters with :
   */
  private void buildRoute(HttpMethod httpMethod, String command, String... path) {
    StringBuilder pathBuilder = new StringBuilder();
    List<String> parameters = new ArrayList<>();

    for (String segment : path) {
      if (segment.startsWith(":")) {
        parameters.add(segment.substring(1));
      }
      pathBuilder.append("/").append(segment);
    }

    String pathWithParams = pathBuilder.toString();

    LOGGER.info("path: " + pathWithParams);

    DeliveryOptions options = new DeliveryOptions();
    options.addHeader("command", command);

    router.route(httpMethod, pathWithParams).handler(
        rc -> {
          final JsonObject messageBody = new JsonObject();

          for (String param : parameters) {
            messageBody.put(param, rc.request().getParam(param));
          }

          if (httpMethod == HttpMethod.POST) {
            messageBody.put("body", rc.getBodyAsJson());
          }

          getVertx().eventBus().send(DEFAULT_ADDRESS, messageBody, options, res -> handleReply(rc, res));
        }
    );
  }

  private void handleReply(RoutingContext ctx, AsyncResult<Message<Object>> asyncResult) {

      if (asyncResult.succeeded()) {
        Object resultBody = asyncResult.result().body();
        if (resultBody instanceof JsonObject) {
          String prettyJson = ((JsonObject) resultBody).encodePrettily();
          ctx.response().end(prettyJson);
        }
      } else {
        createFailedCommand();
      }


  }
  private void createFailedCommand() {
  }


}
