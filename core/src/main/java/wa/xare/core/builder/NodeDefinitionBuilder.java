package wa.xare.core.builder;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import wa.xare.core.Route;

public class NodeDefinitionBuilder {

  private static final Logger LOGGER = LoggerFactory
      .getLogger(NodeDefinitionBuilder.class);

  private static NodeDefinitionBuilder instance;

  private final Map<String, NodeDefinition> nodeDefinitionMap;
  private final Map<String, NodeDefinition> endpointDefinitionMap;
  private final Map<String, ComponentContainer> componentContainerMap;

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

  /**
   * Returns a singleton instance of NodeDefinitionBuilder
   * @return Singleton
   */
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

  public Map<String, NodeDefinition> getNodeDefinitionMap() {
    return nodeDefinitionMap;
  }

  public Map<String, NodeDefinition> getEndpointDefinitioMap() {
    return endpointDefinitionMap;
  }

  public Map<String, ComponentContainer> getComponentContainerMap() {
    return componentContainerMap;
  }

}
