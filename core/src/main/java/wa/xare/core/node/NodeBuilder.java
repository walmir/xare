package wa.xare.core.node;

import java.util.Optional;

import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;

import wa.xare.core.Route;
import wa.xare.core.node.endpoint.EndpointBuilder;
import wa.xare.core.node.endpoint.EndpointConfiguration;
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

  
  private Node splitter(NodeConfiguration nodeConfig) {
    SplitterNode node = (SplitterNode) splitter();
    Optional<Integer> groupOption = Optional.ofNullable(nodeConfig
        .getInteger(SplitterNode.GROUP_FIELD));
    Optional<String> tokenOption = Optional.ofNullable(nodeConfig
        .getString(SplitterNode.TOKEN_FIELD));
    Optional<JsonArray> nodesConfig = Optional.ofNullable(nodeConfig
        .getArray("nodes"));

    NodeBuilder builder = new NodeBuilder(route);

    groupOption.ifPresent(node::setGroup);
    tokenOption.ifPresent(node::setToken);

    nodesConfig.ifPresent(array -> {
          route.getContainer().logger().info("Array" + array);
      array.forEach(obj -> {
            node.addNode(builder.buildNode(new NodeConfiguration(
                (JsonObject) obj)));
      });
    });

    return node;
  }

  private Node splitter() {
    Node node = new SplitterNode();
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

	public Node loggerNode() {
		Node node = new LoggerNode();
		node.setRoute(route);
		return node;
	}
}
