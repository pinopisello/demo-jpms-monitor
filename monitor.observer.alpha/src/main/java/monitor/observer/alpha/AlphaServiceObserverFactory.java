package monitor.observer.alpha;

import java.util.Optional;

import monitor.observer.ServiceObserver;
import monitor.observer.ServiceObserverFactory;

public class AlphaServiceObserverFactory implements ServiceObserverFactory {

  @Override
  public Optional<ServiceObserver> createIfMatchingService(String service) {
    return Optional.of(service)
        // this check should do something more sensible
        .filter(s -> s.contains("alpha"))
        .map(AlphaServiceObserver::new);
  }

}
