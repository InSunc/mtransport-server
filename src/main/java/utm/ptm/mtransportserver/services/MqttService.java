package utm.ptm.mtransportserver.services;

import com.google.gson.Gson;
import org.eclipse.paho.client.mqttv3.*;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateXY;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.stereotype.Service;
import utm.ptm.mtransportserver.models.db.Stop;
import utm.ptm.mtransportserver.models.db.Transport;
import utm.ptm.mtransportserver.models.db.TransportArrival;
import utm.ptm.mtransportserver.models.dto.TransportDTO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class MqttService implements IMqttMessageListener{
    @Autowired
    private StopService stopService;

    @Autowired
    private TransportService transportService;


    private IMqttClient mqttClient;
    private List<String> topics = new ArrayList<>();
    private int count = 0;

    @Bean
    @ConfigurationProperties(prefix = "mqtt")
    private MqttConnectOptions mqttConnectOptions() {
        return new MqttConnectOptions();
    }

    @Bean
    private IMqttClient mqttClient(@Value("${mqtt.clientId}") String clientId,
                                       @Value("${mqtt.host}") String host, @Value("${mqtt.port}") int port)
                                        throws MqttException {

        if (mqttClient == null) {
            MqttPahoClientFactory mqttPahoClientFactory = new DefaultMqttPahoClientFactory();
            mqttClient = mqttPahoClientFactory.getClientInstance("tcp://"
                    + host
                    + ":"
                    + port, clientId);

            mqttClient.connect(mqttConnectOptions());
        }

        return mqttClient;
    }

    public List<String> getSubsribedTopics() {
        return topics;
    }

    public void subscibe(String topic) throws MqttException {
        this.topics.add(topic);
        this.mqttClient.subscribe(topic, this);
    }

    public void publish(String topic, String data) {
        topics.add(topic);
        MqttMessage mqttMessage = new MqttMessage(data.getBytes());
        System.out.println(mqttMessage.toString());
        try {
            mqttClient.publish(topic, mqttMessage);
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
        Coordinate coordinate = new Coordinate(transportDTO.latitude, transportDTO.longitude);
        Point point = geometryFactory.createPoint(coordinate);
        Stop stop = stopService.findByDistance(point, 10000).orElse(null);
        if (stop != null) {
            Transport transport = transportService.findById(transportDTO.id);
            LocalDateTime timestamp = LocalDateTime.now();
            TransportArrival transportArrival = new TransportArrival(transport, stop, timestamp);
            transportService.save(transportArrival);
        }
        int nrOfProple = transportService.getNrOfProple(transportDTO.id);
        transportDTO.loadLevel = 0;
        if (nrOfProple > 50) {
            transportDTO.loadLevel = 2;
        } else if (nrOfProple > 36) {
            transportDTO.loadLevel = 1;
        }
        String nextTopic = topic.substring(topic.indexOf('/') + 1);
        String msg = gson.toJson(transportDTO, TransportDTO.class);
        publish(nextTopic, msg);
    }
}
