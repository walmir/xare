package wa.xare.core.node.subroute;

import org.vertx.java.core.json.JsonArray;

import wa.xare.core.packet.Packet;

public class FilterNode extends DefaultSubRouteNode {
	
	
	@Override
  public void doProcess(Packet packet) {
    if (passesFilter(packet)) {
      getPipline().startProcessing(packet);
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
