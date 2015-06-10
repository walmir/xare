package wa.xare.core.node;

import java.util.ArrayList;
import java.util.List;

import wa.xare.core.Route;
import wa.xare.core.selector.Selector;

public abstract class AbstractNode implements Node {

	private List<ProcessingListener> listeners;

	protected Route Route;

  private Selector selector;

	public void setRoute(Route route) {
		this.Route = route;
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
