package monitor.observer.alpha;

import java.time.ZonedDateTime;
import java.util.Random;

import monitor.observer.DiagnosticDataPoint;
import monitor.observer.ServiceObserver;

public class AlphaServiceObserver implements ServiceObserver {

	private static final Random RANDOM = new Random();

	private final String serviceName;

	AlphaServiceObserver(String serviceName) {
		this.serviceName = serviceName;
	}

	@Override
	public DiagnosticDataPoint gatherDataFromService() {
		// this check should actually contact the serviceName
		boolean alive = RANDOM.nextFloat() > 0.25;
		return DiagnosticDataPoint.of(serviceName, ZonedDateTime.now(), alive);
	}

}
