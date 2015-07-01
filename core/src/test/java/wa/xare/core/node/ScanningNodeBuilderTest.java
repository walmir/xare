package wa.xare.core.node;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.platform.Container;

import wa.xare.core.DefaultRoute;
import wa.xare.core.node.builder.ScanningNodeBuilder;
import wa.xare.core.node.endpoint.DirectEndpoint;
import wa.xare.core.node.endpoint.Endpoint;
import wa.xare.core.node.endpoint.EndpointConfiguration;
import wa.xare.core.node.endpoint.EndpointDirection;
import wa.xare.core.node.endpoint.EndpointTypeNames;
import wa.xare.core.node.subroute.SplitterNode;
import wa.xare.core.packet.PacketSegment;
import wa.xare.core.selector.JsonPathSelector;
import wa.xare.core.selector.SelectorConfiguration;

@RunWith(MockitoJUnitRunner.class)
public class ScanningNodeBuilderTest {

  private ScanningNodeBuilder builder;

	@Mock
	private DefaultRoute route;

	@Before
	public void prepare() {
		Container container = mock(Container.class);
		when(route.getContainer()).thenReturn(container);
		when(container.logger()).thenReturn(mock(Logger.class));
    builder = ScanningNodeBuilder.getInstance();
	}

	@Test
	public void testBuildNode() throws Exception {
		NodeConfiguration endpointConfig = new EndpointConfiguration()
		    .withEndpointDirection(EndpointDirection.OUTGOING)
		    .withEndpointType(EndpointTypeNames.DEFAULT_DIRECT_ENDPOINT);
		NodeConfiguration loggerNodeConfig = new NodeConfiguration()
		    .withType(NodeType.LOGGER);

    assertThat(builder.getNodeInstance(route, endpointConfig)).isInstanceOf(
        Endpoint.class);
    assertThat(builder.getNodeInstance(route, loggerNodeConfig)).isInstanceOf(
		    LoggerNode.class);
	}

  @Test
  public void testBuildSplitterNode() throws Exception {
    String language = SelectorConfiguration.JSON_PATH_EXPRESSION_LANGUAGE;
    String expression = "expression";
    String token = "token";
    
    NodeConfiguration splitterConfig = new NodeConfiguration().withType(
        NodeType.SPLITTER).withSelector(
        new SelectorConfiguration().withExpressionLanguage(language)
            .withSegment(PacketSegment.HEADERS).withExpression(expression));
    splitterConfig.putNumber(SplitterNode.GROUP_FIELD, 1);
    splitterConfig.putString(SplitterNode.TOKEN_FIELD, token);
    
    Node node = builder.getNodeInstance(route, splitterConfig);
    
    assertThat(node).isInstanceOf(SplitterNode.class);
    SplitterNode splitterNode = (SplitterNode) node;
    assertThat(splitterNode.getGroup()).isEqualTo(1);
    assertThat(splitterNode.getToken()).isEqualTo(token);
    
    assertThat(splitterNode.getSelector()).isInstanceOf(JsonPathSelector.class);
    JsonPathSelector selector = (JsonPathSelector) splitterNode.getSelector();
    assertThat(selector.getSegment()).isEqualTo(PacketSegment.HEADERS);
    assertThat(selector.getExpression()).isEqualTo(expression);
  }

  @Test
  public void testBuildEndpoint() throws Exception {
    EndpointConfiguration conf = new EndpointConfiguration()
        .withEndpointAddress("address")
        .withEndpointDirection(EndpointDirection.INCOMING)
        .withEndpointType(EndpointTypeNames.DEFAULT_DIRECT_ENDPOINT);

    Endpoint point = builder.getEndpointInstance(route, conf);
    assertThat(point).isInstanceOf(DirectEndpoint.class);

  }
}
