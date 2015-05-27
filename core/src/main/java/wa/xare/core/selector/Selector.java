package wa.xare.core.selector;

import wa.xare.core.packet.Packet;
import wa.xare.core.packet.PacketSegment;

public interface Selector {

	void setSegment(PacketSegment segment);

	void setExpression(String expression);

	Object getSelection(Packet packet);

}
