package com.jt.kafka.demo.kafkademoapp.controllers;

import com.jt.kafka.demo.kafkademoapp.model.OrderDetail;
import com.jt.kafka.demo.kafkademoapp.services.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.concurrent.ExecutionException;

/**
 * Main controller
 *
 * Created by Jason Tao on 7/7/2020
 */
@RestController
public class OrderResource {

    public OrderService orderService;

    public OrderResource(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/orders")
    @ResponseStatus(HttpStatus.OK)
    public Collection<OrderDetail> retrieveOrders() {
        return orderService.retrieveOrders();
    }

    @GetMapping("/orders/{id}")
    @ResponseStatus(HttpStatus.OK)
    public OrderDetail retrieveOrder(@PathVariable("id") int orderId) {

        if(orderId < 1) {
            throw new IllegalArgumentException("Invalid order id.");
        }
        return orderService.retrieveOrder(orderId);
    }

    @PostMapping("/orders/{id}/verify")
    @ResponseStatus(HttpStatus.OK)
    public String verifyOrderDetail(@PathVariable("id") int orderId) throws ExecutionException, InterruptedException {

        if(orderId < 1) {
            throw new IllegalArgumentException("Invalid order id.");
        }

        OrderDetail orderDetail = orderService.checkInventoriesForOrder(orderId);

        return orderDetail.getAdditionalProperties().get("response") == null ?
                "Order verification completed. " :
                 orderDetail.getAdditionalProperties().get("response").toString();
    }

    @PostMapping("/orders/{id}/checkout")
    @ResponseStatus(HttpStatus.OK)
    public String processCheckout(@PathVariable("id") Integer id) {

        if(id == null || id < 1) {
            throw new IllegalArgumentException("Invalid order id.");
        }

        //Call order service to process checkout
        orderService.processPaymentForOrder(id);

        return "Process payment request sent";
    }

    @ExceptionHandler(value = IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleIllegalArgumentException(IllegalArgumentException ex) {
        return ex.getMessage();
    }

}
