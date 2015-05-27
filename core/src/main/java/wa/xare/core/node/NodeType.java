package wa.xare.core.node;

import java.util.HashMap;
import java.util.Map;

public enum NodeType {

  ENDPOINT("endpoint"), 
  LOGGER("logger"), 
  SPLITTER("splitter");

  private String name;

  private static Map<String, NodeType> map;

  private NodeType(String value) {
    this.name = value;
  }

  static {
    map = new HashMap<>();
    for (NodeType nt : NodeType.values()) {
      map.put(nt.name, nt);
    }
  }

  public static NodeType getNodeType(String name) {
    if (map.containsKey(name)) {
      return map.get(name);
    }
    throw new IllegalArgumentException("node type is not known: " + name);
  }

  public String getName() {
    return name;
  }

}
