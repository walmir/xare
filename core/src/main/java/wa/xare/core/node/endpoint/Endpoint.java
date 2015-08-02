package wa.xare.core.node.endpoint;

import wa.xare.core.node.Node;
import wa.xare.core.packet.Packet;

public interface Endpoint extends Node {

  public static final String TYPE_NAME = "endpoint";

	void deploy();

	void setHandler(EndpointHandler handler);

  public interface EndpointHandler {

    void handleIncomingPacket(Packet packet);

  }

}
