package wa.xare.core.builder;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.beans.Introspector;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import wa.xare.core.Route;
import wa.xare.core.annotation.Component;
import wa.xare.core.annotation.EndpointType;
import wa.xare.core.annotation.NodeType;

public class NodeDefinitionBuilder {

  private static final Logger LOGGER = LoggerFactory
      .getLogger(NodeDefinitionBuilder.class);

  private static final String NODE_NAME_SUFFIX = "Node";
  private static final String ENDPOINT_NAME_SUFFIX = "Endpoint";

  private static NodeDefinitionBuilder instance;

  private final Map<String, NodeDefinition> nodeDefinitionMap;
  private final Map<String, NodeDefinition> endpointDefinitionMap;
  private final Map<String, ComponentContainer> componentContainerMap;
  
  private Route route;

  private NodeDefinitionBuilder() {
    nodeDefinitionMap = new HashMap<>();
    endpointDefinitionMap = new HashMap<>();
    componentContainerMap = new HashMap<>();

    ComponentScanner scanner = ComponentScanner.getInstance();
    scanner.initialScan();

    HashMap<String, Class<?>> nodeClassMap = scanner.getNodeClassMap();
    for (Entry<String, Class<?>> e : nodeClassMap.entrySet()) {
      NodeDefinition def = new NodeDefinition(e.getValue());
      nodeDefinitionMap.put(e.getKey(), def);
    }

    HashMap<String, Class<?>> endpointClassMap = scanner.getEndpointClassMap();
    for (Entry<String, Class<?>> e : endpointClassMap.entrySet()) {
      NodeDefinition def = new NodeDefinition(e.getValue());
      endpointDefinitionMap.put(e.getKey(), def);
    }

    HashMap<String, Map<String, Class<?>>> componentClassMap = scanner
        .getComponentClassMap();
    for (Entry<String, Map<String, Class<?>>> e : componentClassMap.entrySet()) {

      ComponentContainer container = componentContainerMap.get(e.getKey());
      if (container == null) {
        container = new ComponentContainer(e.getKey());
        componentContainerMap.put(e.getKey(), container);
      }

      for (Entry<String, Class<?>> selectorEntry : e.getValue().entrySet()) {
        NodeDefinition componentDef = new NodeDefinition(
            selectorEntry.getValue());
        container.addComponentDefinition(selectorEntry.getKey(), componentDef);
      }
    }
  }

  private NodeDefinitionBuilder(Route route) {
    this();
    this.route = route;
  }

  public void setRoute(Route route) {
    this.route = route;
  }

  public static NodeDefinitionBuilder getInstance() {
    if (instance == null) {
      instance = new NodeDefinitionBuilder();
    }
    return instance;
  }

  /**
   * Method used only for testing. Inserts a mock instead of the real
   * NodeDefinitionBuilder instance.
   * 
   * @param mock
   *          mock of NodeDefinitionBuilder
   * @return real instance to be set back after testing.
   */
  static NodeDefinitionBuilder setInstance(NodeDefinitionBuilder mock) {
    // NodeDefinitionBuilder tmp = instance;
    instance = mock;
    return null;
  }

  private static String getNodeName(Class<?> c) {
    NodeType annotation = c.getAnnotation(NodeType.class);
    if (annotation.value() != null && !annotation.value().isEmpty()) {
      return annotation.value();
    }
    return getNodeName(c, NODE_NAME_SUFFIX);
  }

  private static String getEndpointName(Class<?> c) {
    EndpointType annotation = c.getAnnotation(EndpointType.class);
    if (annotation.value() != null && !annotation.value().isEmpty()) {
      return annotation.value();
    }
    return getNodeName(c, ENDPOINT_NAME_SUFFIX);
  }

  private static String getComponentName(Class<?> c, String suffix) {
    Component annotation = c.getAnnotation(Component.class);
    if (annotation.value() != null && !annotation.value().isEmpty()) {
      return annotation.value();
    }
    return getNodeName(c, suffix);
  }

  private static String getNodeName(Class<?> c, String suffix) {
    String simpleName = Introspector.decapitalize(c.getSimpleName());
    if (simpleName.endsWith(suffix)) {
      return simpleName.substring(0, simpleName.length() - suffix.length());
    }
    return simpleName;
  }

  /**
   * Returns the {@link NodeDefinition} for the given node type
   * 
   * @param type
   *          node type name
   * @return node definition or null if no such type is known
   */
  public NodeDefinition getNodeDefinition(String type) {
    return nodeDefinitionMap.get(type);
  }

  /**
   * Returns the {@link NodeDefinition} for the given Endpoint node type
   * 
   * @param type
   *          endpoint type name
   * @return node definition for the endpoint or null if no such type is known
   */
  public NodeDefinition getEndpointDefinition(String type) {
    return endpointDefinitionMap.get(type);
  }

  /**
   * Returns the {@link ComponentContainer} for the given component type.
   * 
   * @param componentType
   *          the component type name
   * @return component container or null if no such type is known
   */
  public ComponentContainer getComponentContainer(String componentType) {
    return componentContainerMap.get(componentType);
  }

}
