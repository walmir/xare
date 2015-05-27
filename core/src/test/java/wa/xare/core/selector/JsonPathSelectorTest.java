package wa.xare.core.selector;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;

import wa.xare.core.packet.DefaultPacket;
import wa.xare.core.packet.Packet;
import wa.xare.core.packet.PacketSegment;

public class JsonPathSelectorTest {

  private static final int FIRST_AMOUNT = 11;
  private static final String FIRST_NAME = "first-name";
  private static final int SECOND_AMOUNT = 9;
  private static final String SECOND_NAME = "second-name";
  private static final String NAME_ARRAY_FIELD = "name";
  private static final String AMOUNT_ARRAY_FIELD = "amount";
  private static final String ARRAY_FIELD = "the-array";
  private static final String SIMPLE_FIELD = "simple";
  private static final String SIMPLE_VALUE = "simple-value";

  private static final String FIRST_HEADER = "header1";
  private static final String FIRST_HEADER_VALUE = "header1-value";
  private static final String SECOND_HEADER = "header2";
  private static final String SECOND_HEADER_VALUE = "header2-value";

  private Packet packet;

  @Before
  public void prepare() {
    packet = new DefaultPacket();
    JsonObject body = prepareBody();

    packet.setBody(body);
    packet.addHeader(FIRST_HEADER, FIRST_HEADER_VALUE);
    packet.addHeader(SECOND_HEADER, SECOND_HEADER_VALUE);
  }

  private JsonObject prepareBody() {
    JsonObject body = new JsonObject();
    body.putString(SIMPLE_FIELD, SIMPLE_VALUE);

    JsonArray array = new JsonArray();

    JsonObject arrObj1 = new JsonObject();
    arrObj1.putString(NAME_ARRAY_FIELD, FIRST_NAME);
    arrObj1.putNumber(AMOUNT_ARRAY_FIELD, FIRST_AMOUNT);
    JsonObject arrObj2 = new JsonObject();
    arrObj2.putString(NAME_ARRAY_FIELD, SECOND_NAME);
    arrObj2.putNumber(AMOUNT_ARRAY_FIELD, SECOND_AMOUNT);
    array.add(arrObj1);
    array.add(arrObj2);

    body.putArray(ARRAY_FIELD, array);
    return body;

  }

  @Test
  public void testGetSelectionFromHeader() throws Exception {
    String header1Expression = "$['" + FIRST_HEADER + "']";
    Selector selector = new JsonPathSelector(header1Expression);
    try {
      selector.getSelection(packet);
      Assertions.fail("expecting NoPathFoundExceltion");
    } catch (Exception e) {
      assertThat(e.getMessage()).contains(
          "No results for path: " + header1Expression);
    }
    selector.setSegment(PacketSegment.HEADERS);

    String selection = (String) selector.getSelection(packet);
    assertThat(selection).isEqualTo(FIRST_HEADER_VALUE);
  }

  @Test
  public void testGetSelectionFromBody() throws Exception {
    String arrayExpression = "$." + ARRAY_FIELD;
    Selector selector = new JsonPathSelector(arrayExpression);
    Object selection = selector.getSelection(packet);
    JsonArray jsonArray = new JsonArray(selection.toString());
    assertThat(jsonArray.size()).isEqualTo(2);

    String elementExpression = "$." + ARRAY_FIELD + "[?(@.amount < 10)]."
        + NAME_ARRAY_FIELD;
    selector.setExpression(elementExpression);
    JsonArray array = (JsonArray) selector.getSelection(packet);
    System.out.println(array);
    assertThat(array.size()).isEqualTo(1);
    assertThat(array.<String> get(0)).isEqualTo(SECOND_NAME);

  }

}