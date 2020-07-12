package com.jt.kafka.demo.kafkademoapp.kafka;

import com.jt.kafka.demo.kafkademoapp.model.OrderDetail;
import com.jt.kafka.demo.kafkademoapp.util.JsonUtil;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.concurrent.ExecutionException;

/**
 * Kafka producer that consists of methods to send inventory and payment request message to kafka broker.
 *
 * Created by Jason Tao on 7/7/2020
 */
@Component
public class KafkaProducer {

    private static final Logger logger = LoggerFactory.getLogger(KafkaProducer.class);

    @Value(value = "${kafka.inventory.request.topic}")
    private String inventoryRequestTopic;

    @Value(value = "${kafka.inventory.requestreply.topic}")
    private String inventoryRequestReplyTopic;

    @Value(value = "${kafka.payment.request.topic}")
    private String paymentRequestTopic;

    private KafkaTemplate<String, String> requestKafkaTemplate;

    private ReplyingKafkaTemplate<String, String, String> replyingKafkaTemplate;

    public KafkaProducer(final KafkaTemplate<String, String> requestKafkaTemplate,
                         final ReplyingKafkaTemplate<String, String, String> replyingKafkaTemplate) {
        this.requestKafkaTemplate = requestKafkaTemplate;
        this.replyingKafkaTemplate = replyingKafkaTemplate;
    }

    /**
     * Sends order detail to inventory service thru inventory request topic and expects to receive a synchronous response from the
     * inventory consumer.
     *
     * @param orderDetail
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public OrderDetail sendAndReceiveOrderDetail(OrderDetail orderDetail) throws ExecutionException, InterruptedException {

        // create producer record
        ProducerRecord<String, String> producerRecord = new ProducerRecord<>(inventoryRequestTopic, JsonUtil.convertToJson(orderDetail));

        // set a reply topic
        RecordHeader recordHeader = new RecordHeader(KafkaHeaders.TOPIC, inventoryRequestReplyTopic.getBytes());
        producerRecord.headers().add(recordHeader);

        // Send the message and get the future back as result
        RequestReplyFuture<String, String, String> future = replyingKafkaTemplate.sendAndReceive(producerRecord);

        SendResult<String, String> sendResult = future.getSendFuture().get();
        sendResult.getProducerRecord().headers().forEach(header -> logger.info(header.key() + ":" + header.value().toString()));

        return (OrderDetail) JsonUtil.convertToObject(future.get().value(), OrderDetail.class);
    }

    /**
     * Sends the process payment request to the payment service.
     *
     * @param orderDetail
     */
    public void sendProcessPaymentRequest(OrderDetail orderDetail) {

        // Create the producer record to be sent to payment service
        ProducerRecord<String, String> producerRecord = new ProducerRecord<>(paymentRequestTopic, JsonUtil.convertToJson(orderDetail));

        // send the producer record using the kafka template and expect the future object in return.
        ListenableFuture<SendResult<String, String>> future = requestKafkaTemplate.send(producerRecord);

        // define a callback function to handle the future object.
        future.addCallback(new ListenableFutureCallback<>() {

            @Override
            public void onSuccess(SendResult<String, String> result) {
                // just log the response for simplicity.
                logger.info("Sent process payment to payment service for topic=[" + result.getProducerRecord().topic() + "]");
            }

            @Override
            public void onFailure(Throwable ex) {
                // just log the response for simplicity.
                logger.error("Error sending process payment request topic to payment service: " + ex.getMessage());
            }
        });
    }

}
