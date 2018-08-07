package monitor;

import static java.util.stream.Collectors.toList;

import java.lang.Runtime.Version;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import monitor.observer.ServiceObserver;
import monitor.observer.alpha.AlphaServiceObserver;
import monitor.observer.beta.BetaServiceObserver;
import monitor.persistence.StatisticsRepository;
import monitor.rest.MonitorServer;
import monitor.statistics.Statistician;
import monitor.statistics.Statistics;

public class Main {

	public static void main(String[] args) {
		Version version = Runtime.version();
		System.out.println(version);
		int feature = version.feature();
		int interim = version.interim();
		int update = version.update();
		int patch = version.patch();
		
		Monitor monitor = createMonitor();
  
		MonitorServer server = MonitorServer
				.create(monitor::currentStatistics)
				.start();

		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
		scheduler.scheduleAtFixedRate(monitor::updateStatistics, 1, 1, TimeUnit.SECONDS);
		scheduler.schedule(() -> {
					scheduler.shutdown();
					server.shutdown();
				},
				10,
				TimeUnit.SECONDS);
	}

	private static Monitor createMonitor() {
		List<ServiceObserver> observers = Stream.of("alpha-1", "alpha-2", "alpha-3", "beta-1")
				.map(Main::createObserver)
				.flatMap(Optional::stream)
				.collect(toList());
		Statistician statistician = new Statistician();
		StatisticsRepository repository = new StatisticsRepository();
		Statistics initialStatistics = repository.load().orElseGet(statistician::emptyStatistics);

		return new Monitor(observers, statistician, repository, initialStatistics);
	}

	
	
	private static Optional<ServiceObserver> createObserver(String serviceName) {
		return AlphaServiceObserver.createIfAlphaService(serviceName)
				.or(() -> BetaServiceObserver.createIfBetaService(serviceName))
				.or(() -> {
					System.out.printf("No observer for %s found.%n", serviceName);
					return Optional.empty();
				});
	}
	

}


/*



java  -p /Users/glocon/Miei/local_git/JavaModuleSystem/demo-jpms-monitor/monitor.observer/target/classes:/Users/glocon/Miei/local_git/JavaModuleSystem/demo-jpms-monitor/monitor.observer.alpha/target/classes:/Users/glocon/Miei/local_git/JavaModuleSystem/demo-jpms-monitor/monitor.observer.beta/target/classes:/Users/glocon/Miei/local_git/JavaModuleSystem/demo-jpms-monitor/monitor.persistence/target/classes:/Users/glocon/Miei/local_git/JavaModuleSystem/demo-jpms-monitor/monitor.statistics/target/classes:/Users/glocon/Miei/local_git/JavaModuleSystem/demo-jpms-monitor/monitor.resources/target/classes:/Users/glocon/Miei/local_git/JavaModuleSystem/demo-jpms-monitor/monitor.resources/target/test-classes:/Users/glocon/Miei/local_git/JavaModuleSystem/demo-jpms-monitor/monitor.rest/target/classes:/Users/glocon/Miei/local_git/JavaModuleSystem/test/target/classes:/Users/glocon/Miei/local_git/JavaModuleSystem/demo-jpms-monitor/monitor/target/classes:/Users/glocon/Miei/local_git/JavaModuleSystem/xmlizer/bin:/Users/glocon/.m2/repository/com/sparkjava/spark-core/2.7.2/spark-core-2.7.2.jar -classpath /Users/glocon/Miei/local_git/JavaModuleSystem/demo-jpms-monitor/monitor.observer/target/classes:/Users/glocon/Miei/local_git/JavaModuleSystem/demo-jpms-monitor/monitor.observer.alpha/target/classes:/Users/glocon/Miei/local_git/JavaModuleSystem/demo-jpms-monitor/monitor.observer.beta/target/classes:/Users/glocon/Miei/local_git/JavaModuleSystem/demo-jpms-monitor/monitor.persistence/target/classes:/Users/glocon/Miei/local_git/JavaModuleSystem/demo-jpms-monitor/monitor.statistics/target/classes:/Users/glocon/Miei/local_git/JavaModuleSystem/demo-jpms-monitor/monitor.rest/target/classes:/Users/glocon/.m2/repository/org/slf4j/slf4j-api/1.7.13/slf4j-api-1.7.13.jar:/Users/glocon/.m2/repository/org/eclipse/jetty/jetty-server/9.4.8.v20171121/jetty-server-9.4.8.v20171121.jar:/Users/glocon/.m2/repository/javax/servlet/javax.servlet-api/3.1.0/javax.servlet-api-3.1.0.jar:/Users/glocon/.m2/repository/org/eclipse/jetty/jetty-http/9.4.8.v20171121/jetty-http-9.4.8.v20171121.jar:/Users/glocon/.m2/repository/org/eclipse/jetty/jetty-util/9.4.8.v20171121/jetty-util-9.4.8.v20171121.jar:/Users/glocon/.m2/repository/org/eclipse/jetty/jetty-io/9.4.8.v20171121/jetty-io-9.4.8.v20171121.jar:/Users/glocon/.m2/repository/org/eclipse/jetty/jetty-webapp/9.4.8.v20171121/jetty-webapp-9.4.8.v20171121.jar:/Users/glocon/.m2/repository/org/eclipse/jetty/jetty-xml/9.4.8.v20171121/jetty-xml-9.4.8.v20171121.jar:/Users/glocon/.m2/repository/org/eclipse/jetty/jetty-servlet/9.4.8.v20171121/jetty-servlet-9.4.8.v20171121.jar:/Users/glocon/.m2/repository/org/eclipse/jetty/jetty-security/9.4.8.v20171121/jetty-security-9.4.8.v20171121.jar:/Users/glocon/.m2/repository/org/eclipse/jetty/websocket/websocket-server/9.4.8.v20171121/websocket-server-9.4.8.v20171121.jar:/Users/glocon/.m2/repository/org/eclipse/jetty/websocket/websocket-common/9.4.8.v20171121/websocket-common-9.4.8.v20171121.jar:/Users/glocon/.m2/repository/org/eclipse/jetty/websocket/websocket-client/9.4.8.v20171121/websocket-client-9.4.8.v20171121.jar:/Users/glocon/.m2/repository/org/eclipse/jetty/jetty-client/9.4.8.v20171121/jetty-client-9.4.8.v20171121.jar:/Users/glocon/.m2/repository/org/eclipse/jetty/websocket/websocket-servlet/9.4.8.v20171121/websocket-servlet-9.4.8.v20171121.jar:/Users/glocon/.m2/repository/org/eclipse/jetty/websocket/websocket-api/9.4.8.v20171121/websocket-api-9.4.8.v20171121.jar  -m monitor/monitor.Main

java  -p /Users/glocon/.m2/repository/org/slf4j/slf4j-api/1.7.13/slf4j-api-1.7.13.jar:/Users/glocon/.m2/repository/com/sparkjava/spark-core/2.7.2/spark-core-2.7.2.jar:/Users/glocon/Miei/local_git/JavaModuleSystem/demo-jpms-monitor/monitor.statistics/target/classes:/Users/glocon/Miei/local_git/JavaModuleSystem/demo-jpms-monitor/monitor/target/classes:/Users/glocon/Miei/local_git/JavaModuleSystem/demo-jpms-monitor/monitor.observer/target/classes:/Users/glocon/Miei/local_git/JavaModuleSystem/demo-jpms-monitor/monitor.rest/target/classes:/Users/glocon/Miei/local_git/JavaModuleSystem/demo-jpms-monitor/monitor.persistence/target/classes:/Users/glocon/Miei/local_git/JavaModuleSystem/demo-jpms-monitor/monitor.observer.alpha/target/classes:/Users/glocon/Miei/local_git/JavaModuleSystem/demo-jpms-monitor/monitor.observer.beta/target/classes  -m monitor/monitor.Main



ok
java  -p /Users/glocon/.m2/repository/com/sparkjava/spark-core/2.7.2/spark-core-2.7.2.jar:/Users/glocon/Miei/local_git/JavaModuleSystem/demo-jpms-monitor/monitor.statistics/target/classes:/Users/glocon/Miei/local_git/JavaModuleSystem/demo-jpms-monitor/monitor/target/classes:/Users/glocon/Miei/local_git/JavaModuleSystem/demo-jpms-monitor/monitor.observer/target/classes:/Users/glocon/Miei/local_git/JavaModuleSystem/demo-jpms-monitor/monitor.rest/target/classes:/Users/glocon/Miei/local_git/JavaModuleSystem/demo-jpms-monitor/monitor.persistence/target/classes:/Users/glocon/Miei/local_git/JavaModuleSystem/demo-jpms-monitor/monitor.observer.alpha/target/classes:/Users/glocon/Miei/local_git/JavaModuleSystem/demo-jpms-monitor/monitor.observer.beta/target/classes  -m monitor/monitor.Main


 /Users/glocon/Miei/local_git/JavaModuleSystem/demo-jpms-monitor/monitor.observer/target/classes:
 /Users/glocon/Miei/local_git/JavaModuleSystem/demo-jpms-monitor/monitor.observer.alpha/target/classes:
 /Users/glocon/Miei/local_git/JavaModuleSystem/demo-jpms-monitor/monitor.observer.beta/target/classes:
 /Users/glocon/Miei/local_git/JavaModuleSystem/demo-jpms-monitor/monitor.persistence/target/classes:
 /Users/glocon/Miei/local_git/JavaModuleSystem/demo-jpms-monitor/monitor.statistics/target/classes:
 /Users/glocon/Miei/local_git/JavaModuleSystem/demo-jpms-monitor/monitor.resources/target/classes:
 /Users/glocon/Miei/local_git/JavaModuleSystem/demo-jpms-monitor/monitor.resources/target/test-classes:
 /Users/glocon/Miei/local_git/JavaModuleSystem/demo-jpms-monitor/monitor.rest/target/classes:
 /Users/glocon/Miei/local_git/JavaModuleSystem/test/target/classes:
 /Users/glocon/Miei/local_git/JavaModuleSystem/demo-jpms-monitor/monitor/target/classes:
 /Users/glocon/Miei/local_git/JavaModuleSystem/demo-jpms-monitor/mods:/Users/glocon/Miei/local_git/JavaModuleSystem/xmlizer/bin:
 /Users/glocon/.m2/repository/com/sparkjava/spark-core/2.7.2/spark-core-2.7.2.jar 
 
 
 -classpath /Users/glocon/Miei/local_git/JavaModuleSystem/demo-jpms-monitor/monitor.observer/target/classes:/Users/glocon/Miei/local_git/JavaModuleSystem/demo-jpms-monitor/monitor.observer.alpha/target/classes:/Users/glocon/Miei/local_git/JavaModuleSystem/demo-jpms-monitor/monitor.observer.beta/target/classes:/Users/glocon/Miei/local_git/JavaModuleSystem/demo-jpms-monitor/monitor.persistence/target/classes:/Users/glocon/Miei/local_git/JavaModuleSystem/demo-jpms-monitor/monitor.statistics/target/classes:/Users/glocon/Miei/local_git/JavaModuleSystem/demo-jpms-monitor/monitor.rest/target/classes:/Users/glocon/.m2/repository/org/slf4j/slf4j-api/1.7.13/slf4j-api-1.7.13.jar:/Users/glocon/.m2/repository/org/eclipse/jetty/jetty-server/9.4.8.v20171121/jetty-server-9.4.8.v20171121.jar:/Users/glocon/.m2/repository/javax/servlet/javax.servlet-api/3.1.0/javax.servlet-api-3.1.0.jar:/Users/glocon/.m2/repository/org/eclipse/jetty/jetty-http/9.4.8.v20171121/jetty-http-9.4.8.v20171121.jar:/Users/glocon/.m2/repository/org/eclipse/jetty/jetty-util/9.4.8.v20171121/jetty-util-9.4.8.v20171121.jar:/Users/glocon/.m2/repository/org/eclipse/jetty/jetty-io/9.4.8.v20171121/jetty-io-9.4.8.v20171121.jar:/Users/glocon/.m2/repository/org/eclipse/jetty/jetty-webapp/9.4.8.v20171121/jetty-webapp-9.4.8.v20171121.jar:/Users/glocon/.m2/repository/org/eclipse/jetty/jetty-xml/9.4.8.v20171121/jetty-xml-9.4.8.v20171121.jar:/Users/glocon/.m2/repository/org/eclipse/jetty/jetty-servlet/9.4.8.v20171121/jetty-servlet-9.4.8.v20171121.jar:/Users/glocon/.m2/repository/org/eclipse/jetty/jetty-security/9.4.8.v20171121/jetty-security-9.4.8.v20171121.jar:/Users/glocon/.m2/repository/org/eclipse/jetty/websocket/websocket-server/9.4.8.v20171121/websocket-server-9.4.8.v20171121.jar:/Users/glocon/.m2/repository/org/eclipse/jetty/websocket/websocket-common/9.4.8.v20171121/websocket-common-9.4.8.v20171121.jar:/Users/glocon/.m2/repository/org/eclipse/jetty/websocket/websocket-client/9.4.8.v20171121/websocket-client-9.4.8.v20171121.jar:/Users/glocon/.m2/repository/org/eclipse/jetty/jetty-client/9.4.8.v20171121/jetty-client-9.4.8.v20171121.jar:/Users/glocon/.m2/repository/org/eclipse/jetty/websocket/websocket-servlet/9.4.8.v20171121/websocket-servlet-9.4.8.v20171121.jar:/Users/glocon/.m2/repository/org/eclipse/jetty/websocket/websocket-api/9.4.8.v20171121/websocket-api-9.4.8.v20171121.jar  -m monitor/monitor.Main
 
 */
 
 
 