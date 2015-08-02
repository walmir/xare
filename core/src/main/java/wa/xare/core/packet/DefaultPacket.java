package wa.xare.core.packet;

import io.vertx.core.json.JsonObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

@SuppressWarnings("serial")
public class DefaultPacket implements Packet {

  private Map<String, Object> headers;
  private Object body;

  @Override
  public void addHeader(String name, Object value) {
    if (!(value instanceof String || value instanceof JsonObject)) {
      throw new IllegalArgumentException(
          "value of header can only be a String or JsonObject");
    }

    if (headers == null) {
      headers = new HashMap<>();
    }

    headers.put(name, value);
  }

  @Override
  public Object getHeader(String name) {
    if (headers != null && name != null) {
      return headers.get(name);
    }
    return null;
  }

  @Override
  public Map<String, Object> getHeaders() {
    return headers;
  }

  @Override
  public Object getBody() {
    return body;
  }

  @Override
  public void setBody(Object body) {
    this.body = body;
  }

  @Override
  public byte[] serialize() {
    try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream(bos)) {
      out.writeObject(this);
      byte[] packetBytes = bos.toByteArray();
      return packetBytes;
    } catch (IOException e) {
      throw new PacketBuildingException("could not serialize packet", e);
    }
  }

  @Override
  protected Packet clone() throws CloneNotSupportedException {

    DefaultPacket packet = new DefaultPacket();
    packet.setBody(this.getBody());
    for (Entry<String, Object> headerEntry : headers.entrySet()) {
      packet.addHeader(headerEntry.getKey(), headerEntry.getValue());
    }

    return packet;
  }

}
