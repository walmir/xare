package wa.xare.core.node.endpoint;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.vertx.java.core.Vertx;

import wa.xare.core.Route;
import wa.xare.core.node.endpoint.AbstractEndpoint;
import wa.xare.core.node.endpoint.EndpointConfigurationException;
import wa.xare.core.node.endpoint.EndpointDirection;

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
		return spy(new AbstractEndpoint(null, null) {
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
