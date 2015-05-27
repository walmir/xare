package wa.xare.core.node.endpoint;

import wa.xare.core.packet.Packet;

public interface EndpointHandler {

	void handleIncomingPacket(Packet packet);

}
