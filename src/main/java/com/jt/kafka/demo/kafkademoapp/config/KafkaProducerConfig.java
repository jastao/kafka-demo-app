package com.jt.kafka.demo.kafkademoapp.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Defines the Kafka producer configuration
 *
 * Created by Jason Tao on 7/6/2020
 */
@Configuration
public class KafkaProducerConfig {

    private KafkaProperties kafkaProperties;

    public KafkaProducerConfig(KafkaProperties kafkaProperties) {
        this.kafkaProperties = kafkaProperties;
    }

    // Define the request producer factory
    @Bean
    public ProducerFactory<String, String> requestProducerFactory() {
        Map<String, Object> properties = new HashMap<>(kafkaProperties.buildProducerProperties());
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return new DefaultKafkaProducerFactory<>(properties);
    }

    // Define the replying kafka template for returning the synchronous result back to the caller.
    @Bean
    public ReplyingKafkaTemplate<String, String, String> inventoryResponseKafkaTemplate(ProducerFactory<String, String> producerFactory,
                                                                                        KafkaMessageListenerContainer<String, String> responseListenerContainerFactory) {
        return new ReplyingKafkaTemplate<>(producerFactory, responseListenerContainerFactory);
    }

    // Define the kafka template for the request producer
    @Bean
    public KafkaTemplate<String, String> requestKafkaTemplate() {
        return new KafkaTemplate<>(requestProducerFactory());
    }

}
