package wa.xare.core.selector;

import wa.xare.core.annotation.Field;
import wa.xare.core.packet.PacketSegment;

public abstract class AbstractSelector implements Selector {

  @Field
  private String expression;

  @Field(required = false)
  private PacketSegment segment = PacketSegment.BODY;

  public String getExpression() {
    return expression;
  }

  @Override
  public void setSegment(PacketSegment segment) {
    this.segment = segment;

  }

  public PacketSegment getSegment() {
    return segment == null ? PacketSegment.BODY : segment;
  }

  @Override
  public void setExpression(String expression) {
    this.expression = expression;
  }

}
