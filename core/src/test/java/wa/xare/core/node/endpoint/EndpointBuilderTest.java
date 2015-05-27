package wa.xare.core.node.endpoint;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.platform.Container;

import wa.xare.core.DefaultRoute;
import wa.xare.core.node.endpoint.DirectEndpoint;
import wa.xare.core.node.endpoint.Endpoint;
import wa.xare.core.node.endpoint.EndpointBuilder;
import wa.xare.core.node.endpoint.EndpointConfiguration;
import wa.xare.core.node.endpoint.EndpointDirection;
import wa.xare.core.node.endpoint.EndpointTypeNames;

@RunWith(MockitoJUnitRunner.class)
public class EndpointBuilderTest {

	@Mock
	DefaultRoute defaultRoute;

	@Mock
	Container container;

	EndpointBuilder builder;

	@Before
	public void prepare() {
		when(defaultRoute.getContainer()).thenReturn(container);
		when(container.logger()).thenReturn(mock(Logger.class));
		builder = spy(new EndpointBuilder(defaultRoute));
	}

	@Test
	public void testBuildEndpoint() throws Exception {
		EndpointConfiguration conf = new EndpointConfiguration()
		    .withEndpointAddress("address")
		    .withEndpointDirection(EndpointDirection.INCOMING)
		    .withEndpointType(EndpointTypeNames.DEFAULT_DIRECT_ENDPOINT);

		Endpoint point = builder.buildEndpoint(conf);
		assertThat(point).isInstanceOf(DirectEndpoint.class);

	}
}
