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
import utm.ptm.mtransportserver.models.db.Transport;
import utm.ptm.mtransportserver.repositories.TransportRepository;
import utm.ptm.mtransportserver.services.MqttService;
import utm.ptm.mtransportserver.services.RouteService;
import utm.ptm.mtransportserver.utils.OverpassDataParser;

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

	public static void main(String[] args) {
		SpringApplication.run(MtransportServerApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		mqttService.subscibe("t2");

		overpassDataParser.getRouteDataFromServer("t2", "t8");

//		Route route = routeService.getRoute("T2").get();
//		Transport transport = new Transport();
//		transport.setRoute(route);
//		transport.setId(7690);
//		transport = transportRepository.save(transport);
//		transportSimulation.simulate(transport);

		// Parse routes from files
//		File routesRootDir = ResourceUtils.getFile("classpath:routes/");
//		File[] routesDir = routesRootDir.listFiles();
//		for (File routeDir : routesDir) {
//			String routeName = routeDir.getName();
//			String routeDirPath = routeDir.getPath() + "/";
////			overpassDataParser.getRouteWaysFromJson(routeName,routeDirPath + routeName + "-ways.json");
//			overpassDataParser.getRouteStopsFromJson(routeName,routeDirPath + routeName + "-stops.json");
//		}*
	}

//	@Bean
//	public MqttPahoClientFactory mqttClientFactory() {
//		DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
//		MqttConnectOptions options = new MqttConnectOptions();
//		options.setServerURIs(new String[] { "tcp://localhost:1883" });
////		options.setUserName("username");
////		options.setPassword("password".toCharArray());
////		factory.setConnectionOptions(options);
//		return factory;
//	}

//	@Bean
//	@ServiceActivator(inputChannel = "mqttOutboundChannel")
//	public MessageHandler mqttOutbound() {
//		MqttPahoMessageHandler messageHandler =
//				new MqttPahoMessageHandler("testClient", mqttClientFactory());
//		messageHandler.setAsync(true);
//		messageHandler.setDefaultTopic("testTopic");
//		return messageHandler;
//	}
//
//	@Bean
//	public MessageChannel mqttOutboundChannel() {
//		return new DirectChannel();
//	}
//
//	@MessagingGateway(defaultRequestChannel = "mqttOutboundChannel")
//	public interface MyGateway {
//
//		void sendToMqtt(String data);
//
//	}
//
//	// consumer
//
//	@Bean
//	public IntegrationFlow mqttInFlow() {
//		return IntegrationFlows.from(mqttInbound())
//				.transform(p -> p + ", received from MQTT")
//				.get();
//	}
//
//	@Bean
//	public MessageProducerSupport mqttInbound() {
//		MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter("siSampleConsumer",
//				mqttClientFactory(), "testTopic");
//		adapter.setCompletionTimeout(5000);
//		adapter.setConverter(new DefaultPahoMessageConverter());
//		adapter.setQos(1);
//		return adapter;
//	}


}
