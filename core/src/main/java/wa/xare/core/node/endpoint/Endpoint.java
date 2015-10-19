package wa.xare.core.node.endpoint;

import wa.xare.core.node.Node;
import wa.xare.core.packet.Packet;

public interface Endpoint extends Node {

  String TYPE_NAME = "endpoint";
  String ENDPOINT_TYPE_FIELD = "endpointType";

	void deploy();

	void setHandler(EndpointHandler handler);

  interface EndpointHandler {

    void handleIncomingPacket(Packet packet);

  }

}
