package wa.xare.core.builder;

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.beans.Introspector;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import wa.xare.core.Route;
import wa.xare.core.annotation.EndpointType;
import wa.xare.core.annotation.NodeType;
import wa.xare.core.configuration.EndpointConfiguration;
import wa.xare.core.configuration.NodeConfiguration;
import wa.xare.core.node.Node;
import wa.xare.core.node.endpoint.Endpoint;
import wa.xare.core.node.subroute.DefaultSubRouteNode;

public class NodeBuilder {

  private static final Logger LOGGER = LoggerFactory
      .getLogger(NodeBuilder.class);

  private static final String NODE_NAME_SUFFIX = "Node";
  private static final String ENDPOINT_NAME_SUFFIX = "Endpoint";

  private static NodeBuilder instance;
  private final Map<String, Class<Node>> nodeClassMap;
  private final Map<String, NodeConfigurator> nodeConfiguratorMap;
  private final Map<String, Class<Endpoint>> endpointClassMap;
  private Route route;

  @SuppressWarnings("unchecked")
  private NodeBuilder() {
    nodeClassMap = new HashMap<>();
    nodeConfiguratorMap = new HashMap<>();
    endpointClassMap = new HashMap<>();

    new FastClasspathScanner().matchClassesWithAnnotation(
        NodeType.class, c -> {
            if (c.getClass().isInstance(Node.class)) {
                nodeConfiguratorMap.put(getNodeName(c), new NodeConfigurator(
                    (Class<Node>) c));
            } else {
              throw new NodeConfigurationException("Class '" + c.getName()
                  + "' is not of type Node, and cannot be annotated with @NodeType");
            }
        }).matchClassesWithAnnotation(
          EndpointType.class, c -> {
            if (c.getClass().isInstance(Endpoint.class)) {
              endpointClassMap.put(getEndpointName(c), (Class<Endpoint>) c);
            } else {
              throw new NodeConfigurationException("Class '" + c.getName()
                  + "' is not of type Endpoint, and cannot be annotated with @EndpointType");
            }      
        }).scan();
  }


  private NodeBuilder(Route route) {
    this();
    this.route = route;
  }

  public void setRoute(Route route) {
    this.route = route;
  }

  public static NodeBuilder getInstance() {
    if (instance == null) {
      instance = new NodeBuilder();
    }
    return instance;
  }

  public Node getNodeInstance(Route route, NodeConfiguration configuration) {
    String type = configuration.getString("type");
    if ("endpoint".equals(type)) {
      return getEndpointInstance(route,
          new EndpointConfiguration(configuration));
    }

    if (!nodeClassMap.containsKey(type)) {
      throw new NodeConfigurationException("unkown class type: " + type);
    }
    
    Class<Node> nodeClass = nodeClassMap.get(type);
    return buildNodeInstance(nodeClass, configuration);
  }

  private Node buildNodeInstance(Class<Node> nodeClass, NodeConfiguration configuration) {
    try {
      
      Constructor<?> constructor = fetchConstructor(nodeClass);
      
      Constructor<?>[] constructors = nodeClass.getConstructors();

      
      Node node = nodeClass.newInstance();
      node.configure(route, configuration);

      if (node instanceof DefaultSubRouteNode) {
        Optional<JsonArray> nodesConfig = Optional.ofNullable(configuration
            .getJsonArray(DefaultSubRouteNode.NODES_FIELD));

        nodesConfig.ifPresent(array -> {
          for (Object obj : array) {
            Node n = NodeBuilder.getInstance().getNodeInstance(route,
                new NodeConfiguration((JsonObject) obj));
            ((DefaultSubRouteNode) node).addNode(n);
          }
        });
      }

      return node;
    } catch (InstantiationException | IllegalAccessException e) {
      throw new NodeConfigurationException(
          "could not instantiate node of type: " + nodeClass.getName(), e);
    }
  }

  private Constructor<?> fetchConstructor(Class<Node> nodeClass) {
    Constructor<?> constructor = null;
    Constructor<?>[] constructors = nodeClass.getConstructors();

    if (constructors.length == 1) {
      return constructors[0];
    }

    if (constructors.length == 0) {
      throw new NodeConfigurationException("no constructor found for class "
          + nodeClass.getName());
    }
    // else -> there are more than one constructor
    LOGGER.warn("more than one constructor found for class '"
        + nodeClass.getName()
        + "'. The one with the least parameters will be used. ");
    
    for (Constructor<?> cons : constructors) {
      int length = cons.getParameters().length;
      if (length == 0) {
        return cons;
      }
      if (constructor == null) {
        constructor = cons;
      } else {
        if (cons.getParameters().length < constructor.getParameters().length) {
          constructor = cons;
        }
      }
    }
    return constructor;
  }

  public Endpoint getEndpointInstance(Route route,
      EndpointConfiguration endpointConfiguration) {

    String endpointType = endpointConfiguration.getEndpointType();

    if (!endpointClassMap.containsKey(endpointType)) {
      throw new NodeConfigurationException("unkown endpoint class type: "
          + endpointType);
    }

    Class<Endpoint> endpointClass = endpointClassMap.get(endpointType);

    try {
      Endpoint endpoint = endpointClass.newInstance();
      endpoint.configure(route, endpointConfiguration);
      return endpoint;
    } catch (InstantiationException | IllegalAccessException e) {
      throw new NodeConfigurationException(
          "could not instantiate node of type: " + endpointClass.getName(), e);
    }
  }

  private String getNodeName(Class<?> c) {
    NodeType annotation = c.getAnnotation(NodeType.class);
    if (annotation.value() != null && !annotation.value().isEmpty()) {
      return annotation.value();
    }

    return getNodeName(c, NODE_NAME_SUFFIX);
  }

  private String getEndpointName(Class<?> c) {
    EndpointType annotation = c.getAnnotation(EndpointType.class);
    if (annotation.value() != null && !annotation.value().isEmpty()) {
      return annotation.value();
    }

    return getNodeName(c, ENDPOINT_NAME_SUFFIX);
  }

  private String getNodeName(Class<?> c, String suffix) {
    String simpleName = Introspector.decapitalize(c.getSimpleName());

    if (simpleName.endsWith(suffix)) {
      return simpleName.substring(0, simpleName.length() - suffix.length());
    }
    return simpleName;
  }

}
