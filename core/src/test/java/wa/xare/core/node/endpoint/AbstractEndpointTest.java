package wa.xare.core.node.endpoint;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import io.vertx.core.Vertx;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import wa.xare.core.api.EndpointDirection;
import wa.xare.core.api.Route;
import wa.xare.core.api.configuration.NodeConfiguration;

@RunWith(MockitoJUnitRunner.class)
public class AbstractEndpointTest {

  AbstractEndpoint endpoint;

  @Mock
  Route defaultRoute;

  @Mock
  Vertx vertx;

  @Before
  public void prepareTest() {
    endpoint = createEndpointSpy();
  }

  private AbstractEndpoint createEndpointSpy() {
    return spy(new AbstractEndpoint(null) {
      @Override
      protected void deployAsOutgoingEndpoint() {
        // do nothing
      }

      @Override
      protected void deployAsIncomingEndpoint() {
        // do nothing
      }

      @Override
      protected void deliverOutgoingMessage(Object object) {
        // do nothing
      }

      @Override
      protected void doConfigure(NodeConfiguration configuration) {
        // do nothing
      }
    });
  }

  @Test
  public void testDeployForIncomingEndpoint() throws Exception {
    endpoint.direction = EndpointDirection.INCOMING;
    endpoint.deploy();
    verify(endpoint).deployAsIncomingEndpoint();
    verify(endpoint, never()).deployAsOutgoingEndpoint();
  }

  @Test
  public void testDeployForOutgoingEndpoint() throws Exception {
    endpoint.direction = EndpointDirection.OUTGOING;
    endpoint.deploy();
    verify(endpoint).deployAsOutgoingEndpoint();
    verify(endpoint, never()).deployAsIncomingEndpoint();
  }

  @Test(expected = EndpointConfigurationException.class)
  public void testDeployWithNoDirection() throws Exception {
    endpoint.deploy();
  }

}
