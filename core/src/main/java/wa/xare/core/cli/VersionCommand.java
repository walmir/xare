package wa.xare.core.cli;

import io.vertx.core.cli.CLIException;
import io.vertx.core.cli.annotations.Description;
import io.vertx.core.cli.annotations.Name;
import io.vertx.core.cli.annotations.Summary;
import io.vertx.core.spi.launcher.DefaultCommand;


@Name("version")
@Summary("Displays the version.")
@Description("Prints version of this application.")
public class VersionCommand extends DefaultCommand {

  @Override
  public void run() throws CLIException {
    System.out.println("__  ____ _ _ __ ___ \n" +
                       "\\ \\/ / _` | '__/ _ \\\n" +
                       " >  < (_| | | |  __/\n" +
                       "/_/\\_\\__,_|_|  \\___|");

    System.out.println("\nVersion: " + getClass().getPackage().getImplementationVersion());


  }
}
