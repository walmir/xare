package wa.xare.core.node;

import java.util.Optional;

import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonElement;
import org.vertx.java.core.json.JsonObject;

import wa.xare.core.Route;
import wa.xare.core.node.endpoint.EndpointBuilder;
import wa.xare.core.node.endpoint.EndpointConfiguration;
import wa.xare.core.node.subroute.ChoiceNode;
import wa.xare.core.node.subroute.DefaultSubRouteNode;
import wa.xare.core.node.subroute.FilterNode;
import wa.xare.core.node.subroute.SplitterNode;
import wa.xare.core.selector.Selector;
import wa.xare.core.selector.SelectorBuilder;
import wa.xare.core.selector.SelectorConfiguration;

public class NodeBuilder {

  private Route route;

  public NodeBuilder(Route route) {
    this.route = route;
  }

  public Node buildNode(NodeConfiguration nodeConfig) {
    Node node;

    NodeType type = nodeConfig.getType();

    switch (type) {
    case LOGGER:
      node = loggerNode(nodeConfig);
      break;
    case ENDPOINT:
      node = endpoint(nodeConfig);
      break;
    case SPLITTER:
      node = splitter(nodeConfig);
      break;
    case FILTER:
      node = filter(nodeConfig);
      break;
    case CHOICE:
      node = choice(nodeConfig);
      break;
    default:
      node = null;
      break;
    }

    if (node != null
        && nodeConfig.containsField(NodeConfiguration.SELECTOR_CONFIG_FIELD)) {
      SelectorConfiguration selectorConfig = new SelectorConfiguration(
          nodeConfig.getObject(NodeConfiguration.SELECTOR_CONFIG_FIELD));
      SelectorBuilder builder = new SelectorBuilder();
      Selector selector = builder.buildSelector(selectorConfig);
      ((DefaultRouteNode) node).setSelector(selector);
    }

    return node;
  }

  private Node choice(NodeConfiguration nodeConfig) {
    ChoiceNode node = choice();
    JsonElement whereElement = nodeConfig.getElement("cases");
    if (whereElement instanceof JsonArray) {
      ((JsonArray) whereElement).iterator().forEachRemaining(
          whereNodeConfig -> {
            NodeConfiguration filterConfig = new NodeConfiguration(
                (JsonObject) whereNodeConfig);
            filterConfig.setType(NodeType.FILTER);
            Node filter = buildNode(filterConfig);
            node.addNode(filter);
          });
    } else { // JsonObject
      Node filter = filter(new NodeConfiguration((JsonObject) whereElement));
      node.addNode(filter);
    }
    JsonArray otherwisePathConfig = nodeConfig.getArray("otherwise");
    if (otherwisePathConfig != null) {
      DefaultNodeProcessingChain chain = new DefaultNodeProcessingChain();
      
      otherwisePathConfig.forEach(conf -> {
        chain.addNode(buildNode(new NodeConfiguration((JsonObject) conf)));
            });
      node.setOtherwise(chain);
    }
    return node;
  }

  private ChoiceNode choice() {
    ChoiceNode node = new ChoiceNode();
    node.setRoute(route);
    return node;
  }

  private Node filter(NodeConfiguration nodeConfig) {
    FilterNode node = filter();
    bildSubRouteNodes(node, nodeConfig);
    return node;
  }

  private FilterNode filter() {
    FilterNode node = new FilterNode();
    node.setRoute(route);
    return node;
  }

  private Node splitter(NodeConfiguration nodeConfig) {
    SplitterNode node = (SplitterNode) splitter();
    Optional<Integer> groupOption = Optional.ofNullable(nodeConfig
        .getInteger(SplitterNode.GROUP_FIELD));
    Optional<String> tokenOption = Optional.ofNullable(nodeConfig
        .getString(SplitterNode.TOKEN_FIELD));

    groupOption.ifPresent(node::setGroup);
    tokenOption.ifPresent(node::setToken);

    bildSubRouteNodes(node, nodeConfig);

    return node;
  }

  private SplitterNode splitter() {
    SplitterNode node = new SplitterNode();
    node.setRoute(route);
    return node;
  }

  private Node endpoint(NodeConfiguration nodeConfig) {
    EndpointConfiguration config = new EndpointConfiguration(nodeConfig);
    EndpointBuilder builder = new EndpointBuilder(route);
    return builder.buildEndpoint(config);
  }

  private Node loggerNode(NodeConfiguration nodeConfig) {
    LoggerNode node = (LoggerNode) loggerNode();
    node.setRoute(route);
    node.setLevel(nodeConfig.getString(LoggerNode.LOG_LEVEL_FIELD));
    return node;
  }

  private Node loggerNode() {
    Node node = new LoggerNode();
    node.setRoute(route);
    return node;
  }

  private void bildSubRouteNodes(DefaultSubRouteNode node,
      NodeConfiguration nodeConfig) {
  
    Optional<JsonArray> nodesConfig = Optional.ofNullable(nodeConfig
        .getArray("nodes"));
    NodeBuilder builder = new NodeBuilder(route);
    nodesConfig.ifPresent(array -> {
      route.getContainer().logger().info("Array" + array);
      array.forEach(obj -> {
        node.addNode(builder.buildNode(new NodeConfiguration(
            (JsonObject) obj)));
      });
    });
  }
}
