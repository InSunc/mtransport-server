package utm.ptm.mtransportserver;

import org.eclipse.paho.client.mqttv3.IMqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.endpoint.MessageProducerSupport;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import utm.ptm.mtransportserver.models.db.Route;
import utm.ptm.mtransportserver.models.db.Ticket;
import utm.ptm.mtransportserver.models.db.Transport;
import utm.ptm.mtransportserver.repositories.TransportRepository;
import utm.ptm.mtransportserver.services.MqttService;
import utm.ptm.mtransportserver.services.RouteService;
import utm.ptm.mtransportserver.services.TransportService;
import utm.ptm.mtransportserver.utils.OverpassDataParser;

import java.time.LocalDateTime;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalUnit;

@SpringBootApplication
public class MtransportServerApplication implements CommandLineRunner {

    @Autowired
    private OverpassDataParser overpassDataParser;

    @Autowired
    private MqttService mqttService;

    @Autowired
    private RouteService routeService;

    @Autowired
    private TransportRepository transportRepository;


	@Autowired
	private TransportService transportService;

    public static void main(String[] args) {
        SpringApplication.run(MtransportServerApplication.class, args);
    }


    @Override
    public void run(String... args) throws Exception {

        mqttService.subscibe("raw/T2");
        mqttService.subscibe("raw/T8");
        mqttService.subscibe("raw/T10");

        overpassDataParser.getRouteDataFromServer("t2", "t24", "t10", "t12", "t8");

        Transport transport = new Transport();
        transport.setId(100);
        Route route = routeService.findById("T2").get();
        transport.setRoute(route);
        transportService.save(transport);
//        Ticket ticket = new Ticket();;
//        for (int i = 0; i < 3; i++) {
//            ticket = new Ticket();
//            ticket.setTransport(transport);
//            ticket.setCreationTime(LocalDateTime.now().minusDays(3));
//            transportService.save(ticket);
//        }
//		ticket.setCreationTime(LocalDateTime.now().minusDays(2));
//		ticket.setExpirationTime(LocalDateTime.now());
//        transportService.save(ticket);


        System.out.println("====================== O V E R P A S S    D O N E =================================");

    }
}
