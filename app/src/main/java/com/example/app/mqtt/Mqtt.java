package com.example.app.mqtt;

import static java.nio.charset.StandardCharsets.UTF_8;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.Arrays;

import javax.net.ssl.SSLSocketFactory;

public class Mqtt {
    private final String pass = "Stronk!PasswordSuperAdmin1";
    private final String user = "AndroidPhone";

    private MqttClient client = null;
    private MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();

    private final String url = "ssl://7b10c1a6effd49c798757d01597a1663.s2.eu.hivemq.cloud:8883";

    public Mqtt() {
        try {
            client = new MqttClient(
                    url,
                    MqttClient.generateClientId(),
                    new MemoryPersistence());
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }

        mqttConnectOptions.setUserName(user);
        mqttConnectOptions.setPassword(pass.toCharArray());
        mqttConnectOptions.setSocketFactory(SSLSocketFactory.getDefault());
    }

    public void connect() throws MqttException {
        client.connect(mqttConnectOptions);
    }

    public void disconnect() {
        try {
            client.disconnect();
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMessage(String message, String topic) throws MqttException {
        client.publish(
                topic,
                message.getBytes(UTF_8),
                2,
                false);
    }
}
