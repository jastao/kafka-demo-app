package com.jt.kafka.demo.kafkademoapp.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;

import java.util.HashMap;
import java.util.Map;

/**
 * Defines the Kafka consumer configuration
 *
 * Created by Jason Tao on 7/6/2020
 */
@Configuration
public class KafkaConsumerConfig {

    @Value(value = "${kafka.bootstrap-servers}")
    public String bootstrapAddress;

    @Value(value = "${kafka.inventory.consumer.group.id}")
    public String inventoryGroupId;

    @Value(value = "${kafka.payment.consumer.group.id}")
    public String paymentGroupId;

    @Value(value = "${kafka.inventory.requestreply.topic}")
    private String requestReplyTopicName;

    // The request kafka template used by the consumer
    private KafkaTemplate<String, String> requestKafkaTemplate;

    public KafkaConsumerConfig(KafkaTemplate<String, String> requestKafkaTemplate) {
        this.requestKafkaTemplate = requestKafkaTemplate;
    }

    // Setup the consumer configuration properties
    public Map<String, Object> configProperties(String groupId) {
        Map<String, Object> properties = new HashMap<>();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return properties;
    }

    // Create the inventory request consumer factory
    public ConsumerFactory<String, String> requestConsumerFactory(String groupId) {
        return new DefaultKafkaConsumerFactory<>(configProperties(groupId));
    }

    // Create the listener container for the
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> requestListenerContainerFactory(String groupId) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(requestConsumerFactory(groupId));
        factory.setReplyTemplate(requestKafkaTemplate);
        return factory;
    }

    // Define the listen container factory for the inventory request
    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> inventoryRequestListenerContainerFactory() {
        return requestListenerContainerFactory(inventoryGroupId);
    }

    // Define the listen container factory for the payment request
    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> paymentRequestListenerContainerFactory() {
        return requestListenerContainerFactory(paymentGroupId);
    }

    // Define the listen container for the ReplyingKafkaTemplate.
    @Bean
    public KafkaMessageListenerContainer<String, String> replyContainer() {
        ContainerProperties containerProperties = new ContainerProperties(requestReplyTopicName);
        return new KafkaMessageListenerContainer<>(requestConsumerFactory(inventoryGroupId), containerProperties);
    }

}
