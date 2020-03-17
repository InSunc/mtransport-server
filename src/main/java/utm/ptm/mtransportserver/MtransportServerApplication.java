package utm.ptm.mtransportserver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.ResourceUtils;
import utm.ptm.mtransportserver.utils.OverpassDataParser;

import java.io.File;

@SpringBootApplication
public class MtransportServerApplication implements CommandLineRunner {

	@Autowired
	private OverpassDataParser overpassDataParser;

	public static void main(String[] args) {
		SpringApplication.run(MtransportServerApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		// Parse routes from files
		File routesRootDir = ResourceUtils.getFile("classpath:routes/");
		File[] routesDir = routesRootDir.listFiles();
		for (File routeDir : routesDir) {
			String routeName = routeDir.getName();
			String routeDirPath = routeDir.getPath() + "/";
//			overpassDataParser.getRouteWaysFromJson(routeName,routeDirPath + routeName + "-ways.json");
			overpassDataParser.getRouteStopsFromJson(routeName,routeDirPath + routeName + "-stops.json");
		}
	}
}
