package wa.xare.core.configuration;

import io.vertx.core.json.JsonObject;
import wa.xare.core.packet.PacketSegment;

public class SelectorConfiguration extends JsonObject {

  public static final String SEGMENT_FIELD = "segment";
  public static final String EXPRESSION_FIELD = "expression";
  public static final String EXPRESSION_LANGUAGE_FIELD = "expressionLanguage";

  public static final String JSON_PATH_EXPRESSION_LANGUAGE = "jsonPath";

  public SelectorConfiguration() {}

  public SelectorConfiguration(JsonObject config) {
    mergeIn(config);
  }

  public PacketSegment getSegment() {
    String name = getString(SEGMENT_FIELD);
    return name == null ? null : PacketSegment.valueOf(name.toUpperCase());
  }

  public void setSegment(PacketSegment segment) {
    put(SEGMENT_FIELD, segment.name().toLowerCase());
  }

  public String getExpressionLanguage() {
    return getString(EXPRESSION_LANGUAGE_FIELD);
  }

  public void setExpressionLanguage(String expLan) {
    put(EXPRESSION_LANGUAGE_FIELD, expLan);
  }

  public String getExpression() {
    return getString(EXPRESSION_FIELD);
  }

  public void setExpression(String expression) {
    put(EXPRESSION_FIELD, expression);
  }

  public SelectorConfiguration withSegment(PacketSegment segment) {
    setSegment(segment);
    return this;
  }

  public SelectorConfiguration withExpressionLanguage(String language) {
    setExpressionLanguage(language);
    return this;
  }

  public SelectorConfiguration withExpression(String expression) {
    setExpression(expression);
    return this;
  }

}
