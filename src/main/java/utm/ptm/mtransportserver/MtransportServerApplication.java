package utm.ptm.mtransportserver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import utm.ptm.mtransportserver.utils.OverpassDataParser;

@SpringBootApplication
public class MtransportServerApplication implements CommandLineRunner {

	@Autowired
	private OverpassDataParser overpassDataParser;

	public static void main(String[] args) {
		SpringApplication.run(MtransportServerApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		String[] routes = {"T2", "T8"};
		for (String route : routes) {
			overpassDataParser.getRouteDataFromServer(route, OverpassDataParser.RouteDataType.STOPS);
			overpassDataParser.getRouteDataFromServer(route, OverpassDataParser.RouteDataType.WAYS);
		}

		// Parse routes from files
//		File routesRootDir = ResourceUtils.getFile("classpath:routes/");
//		File[] routesDir = routesRootDir.listFiles();
//		for (File routeDir : routesDir) {
//			String routeName = routeDir.getName();
//			String routeDirPath = routeDir.getPath() + "/";
////			overpassDataParser.getRouteWaysFromJson(routeName,routeDirPath + routeName + "-ways.json");
//			overpassDataParser.getRouteStopsFromJson(routeName,routeDirPath + routeName + "-stops.json");
//		}
	}
}
