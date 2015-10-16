package wa.xare.core.selector;

import io.vertx.core.json.JsonArray;
import wa.xare.core.annotation.Component;
import wa.xare.core.packet.Packet;
import wa.xare.core.packet.PacketSegment;

@Component(discriminator = "expressionLanguage")
public interface Selector {

  /**
   * Set the segment from which an element is selected.
   * 
   * @param segment
   *          either {@link PacketSegment#BODY} or {@link PacketSegment#HEADERS}
   */
  void setSegment(PacketSegment segment);

  /**
   * Set the expression used to select an element out of the packet.
   * 
   * @param expression
   */
  void setExpression(String expression);

  /**
   * 
   * @param packet
   * @return A string if one item is selected or a {@link JsonArray} if multiple
   *         items are found
   */
  Object getSelection(Packet packet);

}
