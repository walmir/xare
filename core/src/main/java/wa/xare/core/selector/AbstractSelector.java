package wa.xare.core.selector;

import wa.xare.core.api.PacketSegment;

public abstract class AbstractSelector implements Selector {

  private String expression;

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
