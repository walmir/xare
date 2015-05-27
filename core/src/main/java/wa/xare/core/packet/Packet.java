package wa.xare.core.packet;

import java.io.Serializable;
import java.util.Map;

public interface Packet extends Serializable, Cloneable {

  public static final String INCOMING_ADDRESS_HEADER = "in-address";
  public static final String OUTGOING_ADDRESS_HEADER = "out-address";

  void addHeader(String name, String value);

  void setBody(Object body);

  public abstract Object getBody();

  public abstract String getHeader(String name);

  byte[] serialize();

  Map<String, String> getHeaders();

}
