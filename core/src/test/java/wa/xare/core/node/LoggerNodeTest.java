package wa.xare.core.node;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import io.vertx.core.logging.JULLogDelegateFactory;
import io.vertx.core.logging.LoggerFactory;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import wa.xare.core.api.Packet;


@RunWith(MockitoJUnitRunner.class)
public class LoggerNodeTest {


  // @Spy
  LoggerNode node;

  // @Mock
  // Route Route;

  @Mock
  Packet packet;

  LogHandler handler;

  private String oldLoggerClass;

  private Level oldLevel;

  private java.util.logging.Logger logger;

  // @Spy
  // Logger logger = LoggerFactory.getLogger(LoggerNode.class);

  @BeforeClass
  public static void prepareLogger() {

  }

  @Before
  public void prepare() {

    try{
      oldLoggerClass = System
          .getProperty(LoggerFactory.LOGGER_DELEGATE_FACTORY_CLASS_NAME);
    } catch (Exception e) {
    }

    System.setProperty(LoggerFactory.LOGGER_DELEGATE_FACTORY_CLASS_NAME,
        JULLogDelegateFactory.class.getName());

    MockitoAnnotations.initMocks(this);
    node = new LoggerNode();
    logger = java.util.logging.Logger
        .getLogger(LoggerNode.class.getName());

    handler = new LogHandler();

    oldLevel = logger.getLevel();

    logger.setLevel(Level.ALL);
    logger.setUseParentHandlers(false);
    logger.addHandler(handler);
  }

  @After
  public void restoreLogger() {
    if (oldLoggerClass != null) {
      System.setProperty(LoggerFactory.LOGGER_DELEGATE_FACTORY_CLASS_NAME,
          oldLoggerClass);
    }

    logger.setLevel(oldLevel);
  }

  @Test
  public void testStartProcessing() throws Exception {
    String msg = "msg:asfdasdf90234jhgf";

    when(packet.getBody()).thenReturn(msg);

    node.setLevel(LoggerNode.INFO);
    node.startProcessing(packet);

    assertThat(handler.checkLevel()).isNotNull();
    assertThat(handler.checkMessage()).isNotNull();

    assertThat(handler.checkLevel()).isEqualTo(Level.INFO);
    assertThat(handler.checkMessage()).contains(msg);

    handler.reset();

    node.setLevel(LoggerNode.ERROR);
    node.startProcessing(packet);

    assertThat(handler.checkLevel()).isEqualTo(Level.SEVERE);
    assertThat(handler.checkMessage()).contains(msg);

    handler.reset();

    node.setLevel(LoggerNode.TRACE);
    node.startProcessing(packet);

    assertThat(handler.checkLevel()).isEqualTo(Level.FINEST);
    assertThat(handler.checkMessage()).contains(msg);

    handler.reset();

    node.setLevel(LoggerNode.DEBUG);
    node.startProcessing(packet);

    assertThat(handler.checkLevel()).isEqualTo(Level.FINE);
    assertThat(handler.checkMessage()).contains(msg);

    handler.reset();

    node.setLevel(LoggerNode.WARN);
    node.startProcessing(packet);

    assertThat(handler.checkLevel()).isEqualTo(Level.WARNING);
    assertThat(handler.checkMessage()).contains(msg);
  }

  class LogHandler extends Handler {
    Level lastLevel = null;
    private String lastMessage = null;

    public Level checkLevel() {
      return lastLevel;
    }

    public String checkMessage() {
      return lastMessage;
    }

    public void publish(LogRecord record) {
      lastMessage = record.getMessage();
      lastLevel = record.getLevel();
    }

    public void close() {
    }

    public void flush() {
    }

    public void reset() {
      lastLevel = null;
      lastMessage = null;
    }
  }


}
