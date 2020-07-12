package com.jt.kafka.demo.kafkademoapp.config;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

/**
 * Defines the Kafka administrator configuration
 *
 * Created by Jason Tao on 7/6/2020
 */
@Configuration
public class KafkaTopicConfig {

    // default to 1 partition and 1 replica
    private static final int DEFAULT_INVENTORY_NUM_PARTITIONS       = 1;
    private static final short DEFAULT_INVENTORY_REPLICATION_FACTOR = 1;
    private static final int DEFAULT_PAYMENT_NUM_PARTITIONS         = 1;
    private static final short DEFAULT_PAYMENT_REPLICATION_FACTOR   = 1;

    @Value(value = "${kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Value(value = "${kafka.inventory.request.topic}")
    private String inventoryRequestTopic;

    @Value(value = "${kafka.inventory.requestreply.topic}")
    private String inventoryRequestReplyTopic;

    @Value(value = "${kafka.payment.request.topic}")
    private String paymentRequestTopic;

    // Define the kafka admin to manually create our topics
    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new KafkaAdmin(configs);
    }

    // The inventory request topic to check for inventories for the order.
    @Bean
    public NewTopic getInventoryRequestTopic() {
        return new NewTopic(inventoryRequestTopic, DEFAULT_INVENTORY_NUM_PARTITIONS, DEFAULT_INVENTORY_REPLICATION_FACTOR);
    }

    // The inventory request reply topic that the inventory service replies to as a response to the inventory request.
    @Bean NewTopic getInventoryRequestReplyTopic() {
        return new NewTopic(inventoryRequestReplyTopic, DEFAULT_INVENTORY_NUM_PARTITIONS, DEFAULT_INVENTORY_REPLICATION_FACTOR);
    }

    // The payment request topic to process the payment
    @Bean NewTopic getPaymentRequestTopic() {
        return new NewTopic(paymentRequestTopic, DEFAULT_PAYMENT_NUM_PARTITIONS, DEFAULT_PAYMENT_REPLICATION_FACTOR);
    }
}
