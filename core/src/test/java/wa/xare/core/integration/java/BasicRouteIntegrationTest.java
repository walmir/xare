package wa.xare.core.integration.java;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import wa.xare.core.DefaultRoute;
import wa.xare.core.Route;
import wa.xare.core.builder.RouteBuilder;
import wa.xare.core.builder.RouteConfigurationException;
import wa.xare.core.configuration.EndpointConfiguration;
import wa.xare.core.configuration.NodeConfiguration;
import wa.xare.core.configuration.RouteConfiguration;
import wa.xare.core.node.LoggerNode;
import wa.xare.core.node.endpoint.EndpointDirection;
import wa.xare.core.node.endpoint.EndpointTypeNames;

@RunWith(VertxUnitRunner.class)
public class BasicRouteIntegrationTest {

  private static final Logger LOGGER = LoggerFactory
      .getLogger(BasicRouteIntegrationTest.class);

  Vertx vertx;

  @Before
  public void before(TestContext context) throws RouteConfigurationException {
    vertx = Vertx.vertx();

    JsonObject routeConfig = configureRoute();

    RouteBuilder routeBuilder = new RouteBuilder();
    Route route = routeBuilder.buildRoute(routeConfig);

    vertx.deployVerticle(route, new DeploymentOptions()
        .setConfig(routeConfig).setWorker(true), context.asyncAssertSuccess());
  }

  @After
  public void after(TestContext context) {
    vertx.close(context.asyncAssertSuccess());
  }

  @Test
  public void testRoute(TestContext context) {
    String msgBody = "hello";

    Async async = context.async();

    vertx.eventBus().consumer("output", message -> {
      LOGGER.info("recieved output message: " + message.body());
      context.assertEquals(message.body(), msgBody);
      async.complete();
    });

    vertx.eventBus().send("address-0", msgBody);
  }

  private JsonObject configureRoute() {
    NodeConfiguration nConfig = new NodeConfiguration();
    nConfig.setType("logger");
    nConfig.put(LoggerNode.LOG_LEVEL_FIELD, "info");

    EndpointConfiguration finalNode = new EndpointConfiguration();
    finalNode.setEndpointAddress("output");
    finalNode.setEndpointDirection(EndpointDirection.OUTGOING);
    finalNode.setEndpointType(EndpointTypeNames.DEFAULT_DIRECT_ENDPOINT);

    EndpointConfiguration eConfig = new EndpointConfiguration();
    eConfig.setEndpointAddress("address-0");
    eConfig.setEndpointDirection(EndpointDirection.INCOMING);
    eConfig.setEndpointType(EndpointTypeNames.DEFAULT_DIRECT_ENDPOINT);

    RouteConfiguration rConfig = new RouteConfiguration();
    rConfig.setName("the-route");
    rConfig.setIncomingEndpointConfiguration(eConfig);
    rConfig.addNodeConfiguration(nConfig);
    rConfig.addNodeConfiguration(finalNode);

    return rConfig;
  }

}
