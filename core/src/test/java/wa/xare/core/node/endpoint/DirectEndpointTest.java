package wa.xare.core.node.endpoint;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.logging.Logger;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import wa.xare.core.DefaultRoute;
import wa.xare.core.api.Endpoint;
import wa.xare.core.api.EndpointDirection;
import wa.xare.core.api.configuration.EndpointConfiguration;
import wa.xare.core.node.NodeBuilder;

@RunWith(MockitoJUnitRunner.class)
public class DirectEndpointTest {

  private final String address = "address";

  @Mock
  DefaultRoute defaultRoute;

  @Mock
  Vertx vertx;

  @Mock
  EventBus eventBus;

  // @Mock
  // Container container;

  @Mock
  Logger logger;

  DirectEndpoint endpoint;

  @Before
  public void prepareTest() {
    when(defaultRoute.getVertx()).thenReturn(vertx);
    when(vertx.eventBus()).thenReturn(eventBus);

    endpoint = spy(new DirectEndpoint(EndpointDirection.INCOMING, address));
    endpoint.setRoute(defaultRoute);
  }

  @Test
  public void testDeliverOutgoingMessage() throws Exception {
    endpoint.direction = EndpointDirection.OUTGOING;
    Object msg = "test";
    endpoint.deliverOutgoingMessage(msg);

    verify(eventBus).send(eq("address"), eq(msg));

    reset(eventBus);
    endpoint.direction = EndpointDirection.INCOMING;
    endpoint.deliverOutgoingMessage(msg);
    verify(eventBus, never()).send(anyString(), any(Handler.class));
  }

  @Test
  public void testDeployAsIncomingEndpoint() throws Exception {
    endpoint.deploy();
    verify(endpoint).deployAsIncomingEndpoint();
    verify(eventBus).consumer(anyString(), any());
  }

  @Test
  public void testAnnotationBasedConfiguration() {
    EndpointConfiguration finalNode = new EndpointConfiguration();
    finalNode.setEndpointAddress("output");
    finalNode.setEndpointDirection(EndpointDirection.OUTGOING);
    finalNode.setEndpointType(EndpointTypeNames.DEFAULT_DIRECT_ENDPOINT);

    Endpoint endpoint = NodeBuilder.getInstance().getEndpointInstance(
        defaultRoute, finalNode);

    assertThat(endpoint).isInstanceOf(DirectEndpoint.class);
    DirectEndpoint de = (DirectEndpoint) endpoint;
    assertThat(de.direction).isEqualTo(EndpointDirection.OUTGOING);
  }
}
