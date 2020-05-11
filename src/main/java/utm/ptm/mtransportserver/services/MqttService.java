package utm.ptm.mtransportserver.services;

import com.google.gson.Gson;
import org.eclipse.paho.client.mqttv3.*;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import utm.ptm.mtransportserver.models.db.Route;
import utm.ptm.mtransportserver.models.db.Stop;
import utm.ptm.mtransportserver.models.db.Transport;
import utm.ptm.mtransportserver.models.db.TransportArrival;
import utm.ptm.mtransportserver.models.dto.TransportDTO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class MqttService implements IMqttMessageListener {
    @Autowired
    private StopService stopService;

    @Autowired
    private TransportService transportService;

    @Autowired
    private  RouteService routeService;


    private IMqttAsyncClient mqttClient;
    private IMqttAsyncClient simulator;
    private List<String> topics = new ArrayList<>();
    private int count = 0;

    @Bean
    @ConfigurationProperties(prefix = "mqtt")
    private MqttConnectOptions mqttConnectOptions() {
        return new MqttConnectOptions();
    }

    @Bean
    private IMqttAsyncClient mqttClient(@Value("${mqtt.clientId}") String clientId,
                                        @Value("${mqtt.host}") String host, @Value("${mqtt.port}") int port)
            throws MqttException {

        if (mqttClient == null) {
            MqttPahoClientFactory mqttPahoClientFactory = new DefaultMqttPahoClientFactory();
            mqttClient = mqttPahoClientFactory.getAsyncClientInstance("tcp://"
                    + host
                    + ":"
                    + port, clientId);

            IMqttToken token = mqttClient.connect(mqttConnectOptions());
        }

        return mqttClient;
    }

    @Bean
    private IMqttAsyncClient publisher(@Value("${mqtt.clientId}") String clientId,
                                        @Value("${mqtt.host}") String host, @Value("${mqtt.port}") int port)
            throws MqttException {

        if (simulator == null) {
            MqttPahoClientFactory mqttPahoClientFactory = new DefaultMqttPahoClientFactory();
            simulator = mqttPahoClientFactory.getAsyncClientInstance("tcp://"
                    + host
                    + ":"
                    + port, clientId + "P");

            IMqttToken token = simulator.connect(mqttConnectOptions());
        }

        return simulator;
    }


    public List<String> getSubsribedTopics() {
        return topics;
    }

    public void subscibe(String topic) throws MqttException {
        this.topics.add(topic);
        IMqttToken token = this.mqttClient.subscribe(topic, 1, this);
        token.waitForCompletion();
        System.out.println(" >>> MQTT connection to " + topic + " -> " + token.isComplete());
    }

    public void publish(String topic, String data) {
        topics.add(topic);
        MqttMessage mqttMessage = new MqttMessage(data.getBytes());
//        System.out.println(mqttMessage.toString());
        try {
//            System.out.println(" >>> MQTT client is connected -> " + mqttClient.isConnected());
            mqttClient.publish(topic, mqttMessage);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void simulate(String topic, String data) {
        topics.add(topic);
        MqttMessage mqttMessage = new MqttMessage(data.getBytes());
//        System.out.println(mqttMessage.toString());
        try {
//            System.out.println(" >>> MQTT client is connected -> " + mqttClient.isConnected());
            simulator.publish(topic, mqttMessage);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void unsubsribe(String topic) throws MqttException {
        this.topics.remove(topic);
        this.mqttClient.unsubscribe(topic);
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        GeometryFactory geometryFactory = new GeometryFactory();
        Gson gson = new Gson();
        TransportDTO transportDTO = gson.fromJson(message.toString(), TransportDTO.class);
        Coordinate coordinate = new Coordinate(transportDTO.longitude, transportDTO.latitude);
        Point point = geometryFactory.createPoint(coordinate);
        String routeId = topic.substring(topic.indexOf('/') + 1);
        Stop stop = stopService.findByDistance(point, routeId, 20).orElse(null);
        if (stop != null) {
            Transport transport = transportService.findById(transportDTO.board).orElse(null);
            if (transport != null) {
                LocalDateTime timestamp = LocalDateTime.now();
                TransportArrival transportArrival = new TransportArrival(transport, stop, timestamp);
                transportArrival = transportService.save(transportArrival);
            }
        }
        int nrOfPeople = transportService.getNrOfProple(transportDTO.board);
        transportDTO.loadLevel = 0;
        if (nrOfPeople > 50) {
            transportDTO.loadLevel = 2;
        } else if (nrOfPeople > 36) {
            transportDTO.loadLevel = 1;
        }
        String nextTopic = topic.substring(topic.indexOf('/') + 1);
        String msg = gson.toJson(transportDTO, TransportDTO.class);
        publish(nextTopic, msg);
    }
}
