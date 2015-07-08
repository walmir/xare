package wa.xare.core.node;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

import wa.xare.core.api.Endpoint;
import wa.xare.core.api.EndpointDirection;
import wa.xare.core.api.Node;
import wa.xare.core.api.PacketSegment;
import wa.xare.core.api.configuration.EndpointConfiguration;
import wa.xare.core.api.configuration.NodeConfiguration;
import wa.xare.core.api.configuration.SelectorConfiguration;
import wa.xare.core.node.endpoint.DirectEndpoint;
import wa.xare.core.node.endpoint.EndpointTypeNames;
import wa.xare.core.node.subroute.FilterNode;
import wa.xare.core.node.subroute.SplitterNode;
import wa.xare.core.selector.JsonPathSelector;

public class NodeBuilderTest {

  private NodeBuilder builder;

  @Before
  public void prepare() {
    builder = NodeBuilder.getInstance();
  }

  @Test
  public void testAnnotationScanning() {
    builder = NodeBuilder.getInstance();
    assertThat(builder).isNotNull();
  }

  @Test
  public void testBuildFilterNode() {
    NodeConfiguration filterConfig = new NodeConfiguration().withType(
        FilterNode.TYPE_NAME)
        .withSelector(
            new SelectorConfiguration().withExpression("someExpression")
                .withExpressionLanguage("jsonPath")
                .withSegment(PacketSegment.BODY));
    Node node = NodeBuilder.getInstance().getNodeInstance(null,
        filterConfig);
    assertThat(node).isInstanceOf(FilterNode.class);
  }

  @Test
  public void testBuildNode() throws Exception {
    NodeConfiguration endpointConfig = new EndpointConfiguration()
        .withEndpointDirection(EndpointDirection.OUTGOING).withEndpointType(
            EndpointTypeNames.DEFAULT_DIRECT_ENDPOINT);
    NodeConfiguration loggerNodeConfig = new NodeConfiguration()
        .withType(LoggerNode.TYPE_NAME);

    assertThat(builder.getNodeInstance(null, endpointConfig)).isInstanceOf(
        Endpoint.class);
    assertThat(builder.getNodeInstance(null, loggerNodeConfig)).isInstanceOf(
        LoggerNode.class);
  }

  @Test
  public void testBuildSplitterNode() throws Exception {
    String language = SelectorConfiguration.JSON_PATH_EXPRESSION_LANGUAGE;
    String expression = "expression";
    String token = "token";

    NodeConfiguration splitterConfig = new NodeConfiguration().withType(
        SplitterNode.TYPE_NAME).withSelector(
        new SelectorConfiguration().withExpressionLanguage(language)
            .withSegment(PacketSegment.HEADERS).withExpression(expression));
    splitterConfig.put(SplitterNode.GROUP_FIELD, 1);
    splitterConfig.put(SplitterNode.TOKEN_FIELD, token);

    Node node = builder.getNodeInstance(null, splitterConfig);

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

    Endpoint point = builder.getEndpointInstance(null, conf);
    assertThat(point).isInstanceOf(DirectEndpoint.class);

  }

}
