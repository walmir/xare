package wa.xare.core.selector;

import wa.xare.core.annotation.Field;
import wa.xare.core.packet.PacketSegment;

public abstract class AbstractSelector implements Selector {

  @Field(required = true)
  private String expression;

  @Field
  private PacketSegment segment = PacketSegment.BODY;

  public AbstractSelector(String expression) {
    this.expression = expression;
  }

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
