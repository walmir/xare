package wa.xare.core.packet;

import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.io.ByteArrayInputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.Map;

public class PacketBuilder {

	public static Packet build(Message<?> message) {
		Packet packet = new DefaultPacket();
		packet.setBody(message.body());
		packet.addHeader(Packet.INCOMING_ADDRESS_HEADER, message.address());

		return packet;
	}

	public static Packet build(byte[] packetBytes) {
		try (ByteArrayInputStream bis = new ByteArrayInputStream(packetBytes);
		    ObjectInput in = new ObjectInputStream(bis);) {
			Object o = in.readObject();
			return (Packet) o;
		} catch (Exception e) {
			throw new PacketBuildingException("could not deserialize packet.", e);
		}
	}

	public static Packet build(List<Object> list) {
		JsonArray array = new JsonArray(list);
		Packet packet = new DefaultPacket();
		packet.setBody(array);
		return packet;
	}

	@SuppressWarnings("unchecked")
	public static Packet build(Object object) {
		if (object == null) {
			throw new IllegalArgumentException("object cannot be null");
		}

		Packet packet = new DefaultPacket();
		if (object instanceof Map) {
			try {
				Map<String, Object> map = (Map<String, Object>) object;
				packet.setBody(new JsonObject(map));
			} catch (ClassCastException e) {
				throw new PacketBuildingException("could don't cast packet body.", e);
			}
		} else if (object instanceof String) {
			packet.setBody(new JsonObject((String) object));
		} else if (object instanceof List<?>) {
			packet.setBody(new JsonArray((List<?>) object));
		} else {
			throw new PacketBuildingException(
			    "could not build packet body from given object");
		}

		return packet;
	}
}
