package wa.xare.core.node;

import wa.xare.core.Route;
import wa.xare.core.annotation.Field;
import wa.xare.core.annotation.RouteElement;
import wa.xare.core.packet.ProcessingListener;
import wa.xare.core.packet.ProcessingResult;
import wa.xare.core.selector.Selector;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractNode implements Node {

	private List<ProcessingListener> listeners;

  @RouteElement
  protected Route route;

  @Field(required = false)
  private Selector selector;

	public void setRoute(Route route) {
		this.route = route;
	}

	@Override
	public void addProcessingListener(ProcessingListener listener) {
		if (listeners == null) {
			listeners = new ArrayList<>();
		}
		listeners.add(listener);
	}

  public Selector getSelector() {
    return selector;
  }

  public void setSelector(Selector selector) {
    this.selector = selector;
  }

  protected void notifyProcessingListeners(ProcessingResult result) {
		if (listeners != null) {
			listeners.forEach(l -> l.done(result));
		}
	}

}
