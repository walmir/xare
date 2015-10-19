package wa.xare.core.node.subroute;

import io.vertx.core.json.JsonArray;
import wa.xare.core.annotation.NodeType;
import wa.xare.core.configuration.NodeConfiguration;
import wa.xare.core.packet.Packet;

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

}
