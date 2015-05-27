package wa.xare.core.selector;

import wa.xare.core.packet.PacketSegment;

public class SelectorBuilder {

  public Selector buildSelector(SelectorConfiguration config) {

    String expression = config.getExpression();
    String lang = config.getExpressionLanguage();
    PacketSegment segment = config.getSegment();

    switch (lang) {
    case SelectorConfiguration.JSON_PATH_EXPRESSION_LANGUAGE:
      return jsonPathSelector(segment, expression);

    default:
      throw new IllegalArgumentException("unknown expression language: " + lang);
    }

  }

  private Selector jsonPathSelector(PacketSegment segment, String expression) {
    JsonPathSelector jps = new JsonPathSelector(expression);
    jps.setSegment(segment);

    return jps;
  }

}
