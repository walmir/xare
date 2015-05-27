package wa.xare.core.node.endpoint;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.platform.Container;

import wa.xare.core.DefaultRoute;

@RunWith(MockitoJUnitRunner.class)
public class DirectEndpointTest {

	private final String address = "address";

	@Mock
	DefaultRoute defaultRoute;

	@Mock
	Vertx vertx;

	@Mock
	EventBus eventBus;

	@Mock
	Container container;

	@Mock
	Logger logger;

	DirectEndpoint endpoint;

	@Before
	public void prepareTest() {
		when(defaultRoute.getContainer()).thenReturn(container);
		when(container.logger()).thenReturn(logger);
		when(defaultRoute.getVertx()).thenReturn(vertx);
		when(vertx.eventBus()).thenReturn(eventBus);

		endpoint = spy(new DirectEndpoint(defaultRoute, EndpointDirection.INCOMING,
		    address));
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
		verify(eventBus).registerHandler(anyString(), any());
	}
}
