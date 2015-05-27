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
import wa.xare.core.node.LoggerNode;
import wa.xare.core.node.Node;
import wa.xare.core.node.NodeBuilder;
import wa.xare.core.node.NodeConfiguration;
import wa.xare.core.node.NodeType;
import wa.xare.core.node.endpoint.Endpoint;
import wa.xare.core.node.endpoint.EndpointConfiguration;
import wa.xare.core.node.endpoint.EndpointDirection;
import wa.xare.core.node.endpoint.EndpointTypeNames;
import wa.xare.core.node.subroute.SplitterNode;
import wa.xare.core.packet.PacketSegment;
import wa.xare.core.selector.JsonPathSelector;
import wa.xare.core.selector.SelectorConfiguration;

@RunWith(MockitoJUnitRunner.class)
public class NodeBuilderTest {

	private NodeBuilder builder;

	@Mock
	private DefaultRoute defaultRoute;

	@Before
	public void prepare() {
		Container container = mock(Container.class);
		when(defaultRoute.getContainer()).thenReturn(container);
		when(container.logger()).thenReturn(mock(Logger.class));
		builder = new NodeBuilder(defaultRoute);
	}

	@Test
	public void testBuildNode() throws Exception {
		NodeConfiguration endpointConfig = new EndpointConfiguration()
		    .withEndpointDirection(EndpointDirection.OUTGOING)
		    .withEndpointType(EndpointTypeNames.DEFAULT_DIRECT_ENDPOINT);
		NodeConfiguration loggerNodeConfig = new NodeConfiguration()
		    .withType(NodeType.LOGGER);

		assertThat(builder.buildNode(endpointConfig)).isInstanceOf(Endpoint.class);
		assertThat(builder.buildNode(loggerNodeConfig)).isInstanceOf(
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
    
    Node node = builder.buildNode(splitterConfig);
    
    assertThat(node).isInstanceOf(SplitterNode.class);
    SplitterNode splitterNode = (SplitterNode) node;
    assertThat(splitterNode.getGroup()).isEqualTo(1);
    assertThat(splitterNode.getToken()).isEqualTo(token);
    
    assertThat(splitterNode.getSelector()).isInstanceOf(JsonPathSelector.class);
    JsonPathSelector selector = (JsonPathSelector) splitterNode.getSelector();
    assertThat(selector.getSegment()).isEqualTo(PacketSegment.HEADERS);
    assertThat(selector.getExpression()).isEqualTo(expression);
  }
}
