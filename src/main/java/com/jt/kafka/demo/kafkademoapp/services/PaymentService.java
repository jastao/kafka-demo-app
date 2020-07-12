package com.jt.kafka.demo.kafkademoapp.services;

import com.jt.kafka.demo.kafkademoapp.model.OrderDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;

/**
 * A bare-boned payment service that calculates the sub total of an order from the order detail.
 *
 * Created by Jason Tao on 7/8/2020
 */
@Service
public class PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    /**
     * Calculate the total price of the order.
     *
     * @param orderDetail
     */
    public void calculatePaymentFromOrder(OrderDetail orderDetail) {

        Double subTotal = orderDetail.getProductsList().stream().mapToDouble(product -> product.getQuantity() * product.getPrice()).sum();
        logger.info("The subtotal for order id[" + orderDetail.getId() + "] : " + new DecimalFormat("#.##").format(subTotal));
    }
}
