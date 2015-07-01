package wa.xare.core.integration.java;

import static org.vertx.testtools.VertxAssert.fail;
import static org.vertx.testtools.VertxAssert.testComplete;

import org.junit.Test;
import org.vertx.testtools.TestVerticle;
import org.vertx.testtools.VertxAssert;

import wa.xare.core.DefaultRoute;
import wa.xare.core.RouteConfiguration;
import wa.xare.core.node.LoggerNode;
import wa.xare.core.node.NodeConfiguration;
import wa.xare.core.node.NodeType;
import wa.xare.core.node.endpoint.EndpointConfiguration;
import wa.xare.core.node.endpoint.EndpointDirection;
import wa.xare.core.node.endpoint.EndpointTypeNames;

public class BasicRouteIntegrationTest extends TestVerticle {

  @Test
  public void testRoute() {
    String msgBody = "hello";
    vertx.eventBus().registerHandler("output", message -> {
      container.logger().info("recieved output message: " + message.body());
      VertxAssert.assertEquals(message.body(), msgBody);
      testComplete();
    });

    vertx.eventBus().sendWithTimeout("address-0", msgBody, 5_000, r -> {
      if (r.failed()) {
        fail(r.cause().getMessage());
      }
      container.logger().info(r.result());
    });
  }

  @Override
  public void start() {
    initialize();
    configureAndDeployRoute();
  }

  private void configureAndDeployRoute() {
    NodeConfiguration nConfig = new NodeConfiguration();
    nConfig.setType(NodeType.LOGGER);
    nConfig.putString(LoggerNode.LOG_LEVEL_FIELD, "info");

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

    container.deployWorkerVerticle(DefaultRoute.class.getName(), rConfig, 1,
        false, r -> {
          if (r.succeeded()) {
            startTests();
          }
        });
  }

}
