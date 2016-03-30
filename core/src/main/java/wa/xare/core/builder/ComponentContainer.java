package wa.xare.core.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComponentContainer {

  private Map<String, NodeDefinition> componentMap;
  private String groupName;

  public ComponentContainer(String groupName) {
    this.groupName = groupName;
    componentMap = new HashMap<>();
  }

  public void addComponentDefinition(String name, NodeDefinition def) {
    componentMap.put(name, def);
  }

  public NodeDefinition getComponentDefinition(String componentTypeName) {
    return componentMap.get(componentTypeName);
  }

  public List<String> getComponentNames() {
    return new ArrayList<>(componentMap.keySet());
  }

}
