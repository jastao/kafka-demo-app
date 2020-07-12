package com.jt.kafka.demo.kafkademoapp.kafka;

import com.jt.kafka.demo.kafkademoapp.model.OrderDetail;
import com.jt.kafka.demo.kafkademoapp.model.OrderStatusType;
import com.jt.kafka.demo.kafkademoapp.services.InventoryService;
import com.jt.kafka.demo.kafkademoapp.services.PaymentService;
import com.jt.kafka.demo.kafkademoapp.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * This class holds all the consumer methods that listen for request topic.
 *
 * Created by Jason Tao on 7/7/2020
 */
@Component
public class KafkaConsumer {

    private static final Logger logger = LoggerFactory.getLogger(InventoryService.class);

    @Value(value = "${kafka.inventory.consumer.group.id}")
    private String inventoryGroupId;

    @Value(value = "${kafka.payment.consumer.group.id}")
    private String paymentGroupId;

    private InventoryService inventoryService;

    private PaymentService paymentService;

    public KafkaConsumer(final InventoryService inventoryService,
                         final PaymentService paymentService) {
        this.inventoryService = inventoryService;
        this.paymentService = paymentService;
    }

    /**
     * Listens for inventory request topic within the inventory consumer group and calls the inventory service to verify order detail.
     * Returns the order detail with error response if available thru the request-reply topic.
     *
     * @param orderDetailJSON
     * @return
     */
    @KafkaListener(topics = "${kafka.inventory.request.topic}",
            groupId = "${kafka.inventory.consumer.group.id}",
            containerFactory = "inventoryRequestListenerContainerFactory")
    @SendTo
    public String listenToGroupInventoryRequest(String orderDetailJSON) {

        logger.info("Received request message in group '" + inventoryGroupId);

        // convert the order detail from JSON to POJO
        OrderDetail orderDetail = (OrderDetail) JsonUtil.convertToObject(orderDetailJSON, OrderDetail.class);

        if(orderDetail != null) {
            // call the inventory service to check the quantity of each product within the order.
            Set<String> orderErrorResponse = inventoryService.checkInventoryFromOrderDetail(orderDetail.getProductsList());
            if(!orderErrorResponse.isEmpty()) {

                // if there is error response, set the order status to REJECT.
                orderDetail.setOrderStatus(OrderStatusType.REJECT);
                orderDetail.setAdditionalProperties("response", orderErrorResponse);

                // convert the order detail from POJO to JSON
                return JsonUtil.convertToJson(orderDetail);
            }
            // if no error, set the order status to VERIFIED
            orderDetail.setOrderStatus(OrderStatusType.VERIFIED);
        }
        return JsonUtil.convertToJson(orderDetail);
    }

    /**
     * Listens for payment request topic within the payment consumer group and call the payment service to process
     * the payment request.
     *
     * @param orderDetailJSON
     */
    @KafkaListener(topics = "${kafka.payment.request.topic}",
                   groupId = "${kafka.payment.consumer.group.id}",
                   containerFactory = "paymentRequestListenerContainerFactory")
    public void listenToGroupPaymentRequest(String orderDetailJSON) {

        logger.info("Received request message in group '" + paymentGroupId);

        //Convert the order detail json to OrderDetail
        OrderDetail orderDetail = (OrderDetail) JsonUtil.convertToObject(orderDetailJSON, OrderDetail.class);

        //if not null, send to the payment service for processing
        if(orderDetail != null) {
            paymentService.calculatePaymentFromOrder(orderDetail);
        }
    }
}
