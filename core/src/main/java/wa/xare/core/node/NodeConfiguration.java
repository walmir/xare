package wa.xare.core.node;

import org.vertx.java.core.json.JsonObject;

import wa.xare.core.selector.SelectorConfiguration;

@SuppressWarnings("serial")
public class NodeConfiguration extends JsonObject {

  public static final String NODE_TYPE_FIELD = "type";
  public static final String SELECTOR_CONFIG_FIELD = "selector";

  public NodeConfiguration() {
  }

  public NodeConfiguration(JsonObject config) {
    mergeIn(config);
  }

  public NodeType getType() {
    String type = getString(NODE_TYPE_FIELD);
    return NodeType.getNodeType(type);
  }

  public void setType(NodeType type) {
    putString(NODE_TYPE_FIELD, type.getName());
  }

  public SelectorConfiguration getSelector() {
    JsonObject config = getObject(SELECTOR_CONFIG_FIELD);
    return config == null ? null : new SelectorConfiguration(config);
  }

  public void setSelector(SelectorConfiguration selector) {
    putObject(SELECTOR_CONFIG_FIELD, selector);
  }

  public NodeConfiguration withType(NodeType type) {
    setType(type);
    return this;
  }

  public NodeConfiguration withSelector(SelectorConfiguration selector) {
    setSelector(selector);
    return this;
  }

}
