package wa.xare.core.node.builder;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import wa.xare.core.node.Node;
import wa.xare.core.node.NodeConfiguration;
import wa.xare.core.node.NodeType;
import wa.xare.core.node.subroute.FilterNode;
import wa.xare.core.packet.PacketSegment;
import wa.xare.core.selector.SelectorConfiguration;

public class ScanningNodeBuilderTest {

  private ScanningNodeBuilder builder;

  @Test
  public void testAnnotationScanning() {
    builder = ScanningNodeBuilder.getInstance();
    assertThat(builder).isNotNull();
  }

  @Test
  public void testBuildFilterNode() {
    NodeConfiguration filterConfig = new NodeConfiguration().withType(
        NodeType.FILTER)
        .withSelector(
            new SelectorConfiguration().withExpression("someExpression")
                .withExpressionLanguage("jsonPath")
                .withSegment(PacketSegment.BODY));
    Node node = ScanningNodeBuilder.getInstance().getNodeInstance(null,
        filterConfig);
    assertThat(node).isInstanceOf(FilterNode.class);
  }

}
