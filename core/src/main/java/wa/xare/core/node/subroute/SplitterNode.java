package wa.xare.core.node.subroute;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.vertx.java.core.json.JsonArray;

import wa.xare.core.ProcessingChain;
import wa.xare.core.node.ProcessingException;
import wa.xare.core.packet.Packet;
import wa.xare.core.packet.PacketBuilder;

public class SplitterNode extends DefaultSubRouteNode {

  public static final String GROUP_FIELD = "group";
  public static final String TOKEN_FIELD = "token";

  private String token;

  private int group = 1;

  public int getGroup() {
    return group;
  }

  /**
   * Number of elements to group together
   * 
   * @param group
   *          size of the group
   */
  public void setGroup(int group) {
    this.group = group;

  }

  public String getToken() {
    return token;
  }

  /**
   * If the selected item is a string, the token is used as a string separator.
   * If the selected item is a collection the token is ignored.
   * 
   * @param token
   */
  public void setToken(String token) {
    this.token = token;
  }

  @Override
  @SuppressWarnings("unchecked")
  public void doProcess(Packet packet) {
    checkNodeChain();

    Object selection = getSelection(packet);
    
    if (selection == null){
    	throw new ProcessingException("no items could be selected for splitting");
    }

    List<Object> splitItems = null;
    if (selection instanceof JsonArray) {
      splitItems = ((JsonArray) selection).toList();
    } else if (selection instanceof String) {
      String str = (String) selection;
      if (token != null && !token.isEmpty()) {
        splitItems = Arrays.asList((Object[]) str.split(token));
      }
    } else {
      throw new ProcessingException("selection of this type cannot be split: "
          + selection.getClass().getName());
    }

    List<List<Object>> groups;
    if (group > 1) {
      groups = createGroups(splitItems);
      groups.forEach(this::buildPacketAndTraverse);
    } else {
      splitItems.forEach(this::buildPacketAndTraverse);
    }
  }

  public void buildPacketAndTraverse(Object obj) {
    Packet subpacket = PacketBuilder.build(obj);
    traverse(subpacket);
  }

  private void checkNodeChain() {
    ProcessingChain chain = getNodeChain();

    if (chain == null) {
      throw new IllegalStateException(
          "node processing chain cannot be null or empty.");
    } else if (chain.getNodes().isEmpty()) {
      throw new IllegalStateException(
          "node processing chain cannot be empty.");
    }

  }

  private List<List<Object>> createGroups(List<Object> splitItems) {
    List<List<Object>> groups = new ArrayList<>();
    for (int i = 0; i < splitItems.size(); i += group) {
      if (i + group > splitItems.size()) {
        groups.add(splitItems.subList(i, splitItems.size()));
      } else {
        groups.add(splitItems.subList(i, i + group));
      }
    }

    return groups;
  }

  private Object getSelection(Packet packet) {
    if (getSelector() != null) {
      return getSelector().getSelection(packet);
    } else {
      return packet.getBody();
    }
  }
}
