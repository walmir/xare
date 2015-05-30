package wa.xare.core.node.subroute;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.vertx.java.core.json.JsonObject;

import wa.xare.core.node.Node;
import wa.xare.core.packet.DefaultPacket;
import wa.xare.core.packet.Packet;
import wa.xare.core.selector.JsonPathSelector;

@RunWith(MockitoJUnitRunner.class)
public class FilterNodeTest {

  private static final String AUTHOR = "author";
  private static final String PRICE = "price";
  private static final String RIGHT_AUTHOR = "Joseph Tribiani";
  private static final String WRONG_AUTHOR = "Sam Wan";

  private JsonObject firstBook;
  private JsonObject secondBook;

  private FilterNode node;

  @Mock
  private Node mockNode;
  private Packet firstPacket;
  private Packet secondPacket;


  @Before
  public void prepareNode(){
    String expression = "$.[?(@.author=='" + RIGHT_AUTHOR + "')]";
    
    node = new FilterNode();
    node.setSelector(new JsonPathSelector(expression));
    node.addNode(mockNode);

    firstBook = new JsonObject();
    firstBook.putString(AUTHOR, RIGHT_AUTHOR);
    firstBook.putNumber(PRICE, 20);
    firstPacket = new DefaultPacket();
    firstPacket.setBody(firstBook);

    secondBook = new JsonObject();
    secondBook.putString(AUTHOR, WRONG_AUTHOR);
    secondBook.putNumber(PRICE, 10);
    secondPacket = new DefaultPacket();
    secondPacket.setBody(secondBook);
  }

  @Test
  public void testFilteringByString() throws Exception {
    reset(mockNode);

    node.startProcessing(firstPacket);
    verify(mockNode).startProcessing(any());

    reset(mockNode);

    node.startProcessing(secondPacket);
    verify(mockNode, never()).startProcessing(any());
  }

  @Test
  public void testFilteringByNumber() throws Exception {
    reset(mockNode);

    String byNumExpression = "$.[?(@.price <= 15)]";
    node.getSelector().setExpression(byNumExpression);

    node.startProcessing(firstPacket);
    verify(mockNode, never()).startProcessing(any());

    reset(mockNode);

    node.startProcessing(secondPacket);
    verify(mockNode).startProcessing(any());
  }

}
