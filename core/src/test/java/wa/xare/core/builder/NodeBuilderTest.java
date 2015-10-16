package wa.xare.core.builder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import io.vertx.core.json.JsonObject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import wa.xare.core.builder.mocks.IMockComponent;
import wa.xare.core.builder.mocks.MockComponent;
import wa.xare.core.builder.mocks.MockEndpoint;
import wa.xare.core.builder.mocks.MockNodeComplex;
import wa.xare.core.builder.mocks.MockNodeWithComponents;
import wa.xare.core.node.Node;
import wa.xare.core.node.endpoint.Endpoint;

@RunWith(MockitoJUnitRunner.class)
public class NodeBuilderTest {

  private final static String NODE_TYPE_NAME = "mockNodeComplex";
  private final static String ENDPOINT_TYPE_NAME = "mockNodeComplex";
  private final static String COMPONENT_NODE_TYPE_NAME = "mockNodeWithComponents";

  NodeBuilder builder;

  @Mock
  NodeDefinitionBuilder defBuilder;
  private NodeDefinitionBuilder tempDefBuilder;

  @Before
  public void setup() {

    tempDefBuilder = NodeDefinitionBuilder.setInstance(defBuilder);

    NodeDefinition mockNodeDef = new NodeDefinition(MockNodeComplex.class);
    NodeDefinition mockEndpointDef = new NodeDefinition(MockEndpoint.class);
    NodeDefinition mockNodeWithComponentsDef = new NodeDefinition(MockNodeWithComponents.class);
    
    NodeDefinition mockComponentDef = new NodeDefinition(MockComponent.class);
    
    when(defBuilder.getNodeDefinition(NODE_TYPE_NAME)).thenReturn(mockNodeDef);
    when(defBuilder.getEndpointDefinition(ENDPOINT_TYPE_NAME)).thenReturn(
        mockEndpointDef);
    when(defBuilder.getNodeDefinition(COMPONENT_NODE_TYPE_NAME)).thenReturn(
        mockNodeWithComponentsDef);

    ComponentContainer cc = new ComponentContainer("anything");
    cc.addComponentDefinition("mockComponent", mockComponentDef);
    
    when(defBuilder.getComponentContainer("iMockComponent")).thenReturn(cc);

    builder = new NodeBuilder(null);
  }

  @After
  public void tearDown() {
    NodeDefinitionBuilder.setInstance(tempDefBuilder);
  }



  // @Test
  // public void testGetEndpointInstance() throws Exception {
  //
  // Map<String, PropertyDescriptor> map = Arrays.stream(
  // Introspector.getBeanInfo(MockClass.class)
  // .getPropertyDescriptors())
  // .collect(Collectors.toMap(pd -> pd.getDisplayName(), pd -> pd));
  //
  // // map.keySet().forEach(System.out::println);
  //
  // PropertyDescriptor[] pds = Introspector.getBeanInfo(MockClass.class)
  // .getPropertyDescriptors();
    // for (PropertyDescriptor pd : pds) {
    // System.out.println(pd.getName());
    // System.out.println(pd.getDisplayName());
    // System.out.println(pd.getShortDescription());
    // System.out.println(pd.getPropertyEditorClass());
    // }

  // }

  @Test
  public void testComplexMock() throws Exception {

    String config = 
          "{"
        + " \"type\": \"" + NODE_TYPE_NAME +"\","
        + " \"stringField\": \"stringValue\","
        + " \"intField\": 3,"
        + " \"bool\":true,"
        + " \"strings\": [\"one\",\"two\",\"three\"],"
        + " \"theList\": [4,3,2,1,0],"
        + " \"accessible\": \"directlySet\""
        + "}";
    
    JsonObject jsonConfig = new JsonObject(config);

    Node nodeInstance = builder.getNodeInstance(jsonConfig);
    assertThat(nodeInstance).isExactlyInstanceOf(MockNodeComplex.class);

    MockNodeComplex node = (MockNodeComplex) nodeInstance;
    assertThat(node.getIntField()).isEqualTo(3);
    assertThat(node.isBooleanField()).isEqualTo(true);
    assertThat(node.getStringField()).isEqualTo("stringValue");
    assertThat(node.getStrings()).containsExactly("one", "two", "three");
    assertThat(node.getIntList()).containsOnly(4, 3, 2, 1, 0);
    assertThat(node.remainsEmpty).isNullOrEmpty();
    assertThat(node.accessible).isEqualTo("directlySet");

  }

  @Test
  public void testBuildEndpoint() {
    String config = 
        "{"
      + " \"type\": \"" + Endpoint.TYPE_NAME + "\","
      + " \"endpointType\": \"" + ENDPOINT_TYPE_NAME + "\","
      + " \"someString\": \"someStringValue\""
      + "}";

    JsonObject jsonConfig = new JsonObject(config);

    Node nodeInstance = builder.getNodeInstance(jsonConfig);
    assertThat(nodeInstance).isInstanceOf(Endpoint.class);
    assertThat(nodeInstance).isInstanceOf(MockEndpoint.class);

    MockEndpoint endpoint = (MockEndpoint) nodeInstance;
    assertThat(endpoint.getSomeString()).isEqualTo("someStringValue");
  }

  @Test
  public void testBuildNodeWithComponents() {
    String config = 
        "{"
      + " \"type\": \"" + COMPONENT_NODE_TYPE_NAME +"\","
      + " \"component\": {"
      + "                   \"" + IMockComponent.DISCRIMINATOR_NAME + "\": \"mockComponent\","
      + "                    \"componentField\": \"componentFieldValue\""
      + "                },"
      + " \"simpleNode\": {"
      +                     " \"type\": \"" + NODE_TYPE_NAME +"\","
      +                     " \"stringField\": \"stringValue\","
      +                     " \"intField\": 3,"
      +                     " \"bool\":true,"
      +                     " \"strings\": [\"one\",\"two\",\"three\"],"
      +                     " \"theList\": [4,3,2,1,0],"
      +                     " \"accessible\": \"directlySet\""
        + "}"
      + "}";

    JsonObject jsonConfig = new JsonObject(config);

    Node nodeInstance = builder.getNodeInstance(jsonConfig);
    assertThat(nodeInstance).isExactlyInstanceOf(MockNodeWithComponents.class);

    MockComponent component = (MockComponent) ((MockNodeWithComponents) nodeInstance)
        .getComponent();
    assertThat(component.getComponentField()).isEqualTo("componentFieldValue");

    MockNodeComplex subNode = ((MockNodeWithComponents) nodeInstance).getSimpleNode();
    assertThat(subNode.getIntField()).isEqualTo(3);
    assertThat(subNode.isBooleanField()).isEqualTo(true);
    assertThat(subNode.getStringField()).isEqualTo("stringValue");
    assertThat(subNode.getStrings()).containsExactly("one", "two", "three");
    assertThat(subNode.getIntList()).containsOnly(4, 3, 2, 1, 0);
    assertThat(subNode.remainsEmpty).isNullOrEmpty();
    assertThat(subNode.accessible).isEqualTo("directlySet");

  }


}
