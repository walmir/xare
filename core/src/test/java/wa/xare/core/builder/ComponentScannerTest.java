package wa.xare.core.builder;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import wa.xare.core.node.LoggerNode;
import wa.xare.core.node.PipelineNode;
import wa.xare.core.node.endpoint.DirectEndpoint;
import wa.xare.core.node.endpoint.HttpEndpoint;
import wa.xare.core.node.subroute.ChoiceNode;
import wa.xare.core.node.subroute.FilterNode;
import wa.xare.core.node.subroute.SplitterNode;
import wa.xare.core.selector.JsonPathSelector;

public class ComponentScannerTest {

  ComponentScanner scanner;

  @Before
  public void setUp() {
    scanner = ComponentScanner.getInstance();
  }

  /**
   * Tests by scanning the main node package {@link wa.xare.core.node"}. Adding
   * nodes requires updating this test accordingly.
   */
  @Test
  public void testScanForNodes() throws Exception {
    HashMap<String, Class<?>> nodes = scanner.scanForNodes("wa.xare.core.node");
    assertThat(nodes).hasSize(5);

    assertThat(nodes.values()).contains(LoggerNode.class);
    assertThat(nodes.values()).contains(PipelineNode.class);
    assertThat(nodes.values()).contains(FilterNode.class);
    assertThat(nodes.values()).contains(SplitterNode.class);
    assertThat(nodes.values()).contains(ChoiceNode.class);
  }

  /**
   * Tests by scanning the package. {@link wa.xare.core.selector}. When adding
   * new components to the package, the test has to be updated accordingly.
   */
  @Test
  public void testScanForComponents() throws Exception {
    HashMap<String, Map<String, Class<?>>> components = scanner
        .scanForComponents("wa.xare.core.selector");

    assertThat(components).hasSize(1);
    assertThat(components.keySet()).contains("selector");
    assertThat(components.get("selector")).hasSize(1);
    assertThat(components.get("selector").keySet())
        .contains("jsonPathSelector");
    assertThat(components.get("selector").get("jsonPathSelector")).isEqualTo(
        JsonPathSelector.class);
  }

  /**
   * 
   */
  @Test
  public void testScanForEndpoints() throws Exception {
    HashMap<String, Class<?>> endpointsMap = scanner
        .scanForEndpoints("wa.xare.core.node");

    assertThat(endpointsMap.values()).containsOnly(DirectEndpoint.class,
        HttpEndpoint.class);
  }
}
