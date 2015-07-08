package wa.xare.core.packet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import wa.xare.core.api.Packet;

@SuppressWarnings("serial")
public class DefaultPacket implements Packet {

  private Map<String, String> headers;
  private Object body;

  @Override
  public void addHeader(String name, String value) {
    if (headers == null) {
      headers = new HashMap<>();
    }

    headers.put(name, value);
  }

  @Override
  public String getHeader(String name) {
    if (headers != null && name != null) {
      return headers.get(name);
    }
    return null;
  }

  @Override
  public Map<String, String> getHeaders() {
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
    for (Entry<String, String> headerEntry : headers.entrySet()) {
      packet.addHeader(headerEntry.getKey(), headerEntry.getValue());
    }

    return packet;
  }

}
