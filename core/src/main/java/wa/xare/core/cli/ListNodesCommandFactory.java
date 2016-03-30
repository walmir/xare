package wa.xare.core.cli;

import io.vertx.core.spi.launcher.DefaultCommandFactory;

/**
 * Created by wajdi on 20/03/16.
 */
public class ListNodesCommandFactory extends DefaultCommandFactory<ListNodesCommand> {

  public ListNodesCommandFactory() {
    super(ListNodesCommand.class);
  }

}
