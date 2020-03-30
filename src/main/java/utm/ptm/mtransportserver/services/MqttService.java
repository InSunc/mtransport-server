package utm.ptm.mtransportserver.services;

import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MqttService {

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
        this.mqttClient.subscribeWithResponse(topic, ((_topic, _message) -> {
            count++;
            System.out.println("\n\n\n\t" + _topic + ":\n" + _message + "\ncount:" + count + "\n\n\n");
        }));
    }

    public void publish(String topic, String data) {
        topics.add(topic);
        MqttMessage mqttMessage = new MqttMessage(data.getBytes());
        System.out.println(mqttMessage.toString());
        try {
            mqttClient.publish(topic, data.getBytes(), 0, false);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void unsubsribe(String topic) throws MqttException {
        this.topics.remove(topic);
        this.mqttClient.unsubscribe(topic);
    }
}
