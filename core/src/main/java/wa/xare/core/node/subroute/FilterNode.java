package wa.xare.core.node.subroute;

import org.vertx.java.core.json.JsonArray;

import wa.xare.core.packet.Packet;

public class FilterNode extends DefaultSubRouteNode {
	
	
	@Override
  public void doProcess(Packet packet) {
    Object selection = getSelector().getSelection(packet);
    if (selection != null) {
      if (selection instanceof String ||
          (selection instanceof JsonArray && 
          ((JsonArray) selection).size() > 0)) {

        getNodeChain().traverse(packet);
      }
    }
    // do nothing -> filter packet out
	}

	
	
}
