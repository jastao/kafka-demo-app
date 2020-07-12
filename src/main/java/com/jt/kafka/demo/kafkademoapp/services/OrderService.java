package com.jt.kafka.demo.kafkademoapp.services;

import com.jt.kafka.demo.kafkademoapp.kafka.KafkaProducer;
import com.jt.kafka.demo.kafkademoapp.model.OrderDetail;
import com.jt.kafka.demo.kafkademoapp.model.OrderStatusType;
import com.jt.kafka.demo.kafkademoapp.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 * Order service keeps track of a group of orders. For simplicity, a static hashmap is used to store the orders.
 * This service sends corresponding request message to other services thru the kafka producer.
 *
 * Created by Jason Tao on 7/7/2020
 */
@Service
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    private static Map<Integer, OrderDetail> ordersList = new HashMap<>();
    private static Integer orderCounter = 0;

    private KafkaProducer kafkaProducer;

    static {
        ordersList.put(++orderCounter,
                new OrderDetail(orderCounter, OrderStatusType.PENDING, Set.of(new Product("Coke", 3, 3.99), new Product("Poptart", 8, 2.19))));
        ordersList.put(++orderCounter,
                new OrderDetail(orderCounter, OrderStatusType.PENDING, Set.of(new Product("Apple", 5, 0.5), new Product("Milk", 2, 2.99), new Product("Orange", 1, .69))));
        ordersList.put(++orderCounter,
                new OrderDetail(orderCounter, OrderStatusType.PENDING, Set.of(new Product("Poptart", 3,2.19), new Product("Vitamin", 1, 3.99))));
    }

    public OrderService(KafkaProducer kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }

    /**
     * Sends the order to the inventory service thru the producer for verification and expects the order detail in return
     * with the error response for each product.
     *
     * @param orderId
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public OrderDetail checkInventoriesForOrder(Integer orderId) throws ExecutionException, InterruptedException {

        // Find the order detail
        OrderDetail orderDetail = ordersList.get(orderId);
        OrderDetail returnedOrderDetail = kafkaProducer.sendAndReceiveOrderDetail(orderDetail);

        if(returnedOrderDetail != null ) {
            // update the map
            OrderDetail foundOrderDetail = ordersList.get(returnedOrderDetail.getId());
            foundOrderDetail.setOrderStatus(returnedOrderDetail.getOrderStatus());
        }
        return returnedOrderDetail;
    }

    /**
     * Checks the order status of an order and sends the order to the payment service thru the producer if verified.
     *
     * @param orderId
     */
    public void processPaymentForOrder(Integer orderId) {

        // Find the order detail
        OrderDetail orderDetail = ordersList.get(orderId);

        if(OrderStatusType.VERIFIED.equals(orderDetail.getOrderStatus())) {
            kafkaProducer.sendProcessPaymentRequest(orderDetail);
        } else if(OrderStatusType.REJECT.equals(orderDetail.getOrderStatus())) {
            logger.info("Cannot process payment because order verification rejected.");
        } else {
            logger.info("Cannot process payment because order verification is pending.");
        }
    }

    public Collection<OrderDetail> retrieveOrders() {
        return ordersList.values();
    }

    public OrderDetail retrieveOrder(Integer orderId) {
        return ordersList.get(orderId);
    }

}
