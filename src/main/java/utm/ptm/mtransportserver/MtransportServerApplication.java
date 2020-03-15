package utm.ptm.mtransportserver;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import sun.tools.jar.CommandLine;
import utm.ptm.mtransportserver.utils.OverpassDataParser;

import javax.smartcardio.CommandAPDU;

@SpringBootApplication
public class MtransportServerApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(MtransportServerApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

	}
}
