package wa.xare.core.node;

import java.util.ArrayList;
import java.util.List;

import wa.xare.core.Route;
import wa.xare.core.selector.Selector;
import wa.xare.core.selector.SelectorBuilder;
import wa.xare.core.selector.SelectorConfiguration;

public abstract class AbstractNode implements Node {

	private List<ProcessingListener> listeners;
  protected Route route;

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

  @Override
  public void configure(Route route, NodeConfiguration configuration) {
    if (configuration.containsField(NodeConfiguration.SELECTOR_CONFIG_FIELD)) {
      SelectorConfiguration selectorConfig = new SelectorConfiguration(
          configuration.getObject(NodeConfiguration.SELECTOR_CONFIG_FIELD));
      SelectorBuilder builder = new SelectorBuilder();
      Selector selector = builder.buildSelector(selectorConfig);
      this.setSelector(selector);
    }
    setRoute(route);
    doConfigure(configuration);
  }

  protected abstract void doConfigure(NodeConfiguration configuration);

}
