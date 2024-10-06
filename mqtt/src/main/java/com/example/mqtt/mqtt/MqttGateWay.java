package com.example.mqtt.mqtt;

import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.handler.annotation.Header;

@MessagingGateway(defaultRequestChannel = "mqttOutboundChannel")
public interface MqttGateWay {

    void sendToMqtt(String payload);

    void sendToMqtt(@Header(MqttHeaders.TOPIC) String topic, String payload);
}
