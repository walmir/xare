package wa.xare.core.node.subroute;

import io.vertx.core.json.JsonArray;
import wa.xare.core.api.Packet;
import wa.xare.core.api.annotation.NodeType;
import wa.xare.core.api.configuration.NodeConfiguration;

@NodeType
public class FilterNode extends DefaultSubRouteNode {
	
  public static final String TYPE_NAME = "filter";
	
	@Override
  public void doProcess(Packet packet) {
    if (passesFilter(packet)) {
      getPipeline().startProcessing(packet);
    }
    // do nothing -> filter packet out
  }

  public boolean passesFilter(Packet packet) {
    Object selection = getSelector().getSelection(packet);
    if (selection != null) {
      if (selection instanceof String ||
          (selection instanceof JsonArray && 
          ((JsonArray) selection).size() > 0)) {

        return true;
      }
    }
    return false;
	}

  @Override
  protected void doConfigure(NodeConfiguration configuration) {
    // Do nothing: Abstract node takes care of configuring the selector
  }
	
}
