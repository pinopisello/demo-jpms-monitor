> :arrow_upper_right: This README only covers one section of the demo.
> The [master branch](../../tree/master) contains more information.

# Services

Services follow the [service locator pattern](https://en.wikipedia.org/wiki/Service_locator_pattern) and decouple the user of an API from its implementors.

In this example the _monitor_ module declares that it uses the `ServiceObserverFactory` as a service:

```java
module monitor {
	requires monitor.observer;
	// no more requires on alpha and beta observer modules!
	requires monitor.statistics;
	requires monitor.persistence;
	requires monitor.rest;

	uses ServiceObserverFactory;
}
```

Now _monitor.observer.alpha_ and _monitor.observer.beta_ declare that they provide that service:

```java
module monitor.observer.alpha {
	requires monitor.observer;
	// no exports needed; API is well-encapsulated!
	provides monitor.observer.ServiceObserverFactory
		with monitor.observer.alpha.AlphaServiceObserverFactory;
}
```

And that's it from the module declaration side.
The code in _monitor_ loading those service looks as follows:

```java
List<ServiceObserverFactory> observerFactories = ServiceLoader
		.load(ServiceObserverFactory.class).stream()
		.map(Provider::get)
		.collect(toList());
```

Building and executing doesn't change.

## Compatibility

To demonstrate that services in plain JARs still work the way they used to, I created an additional service _monitor.observer.zero_.
It uses `META-INF/services` to declare `ZeroObserverFactory`  and `run.sh` as well as Maven exec load it as an automatic module.
