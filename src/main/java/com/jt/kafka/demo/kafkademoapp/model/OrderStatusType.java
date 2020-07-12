package com.jt.kafka.demo.kafkademoapp.model;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Order status type to show rather the order is
 *  - verified (all products in the order are verified with the inventory)
 *  - rejected (inventory dont have enough quantity for specified products)
 *  - pending (not yet verified)
 * Created by Jason Tao on 7/8/2020
 */
public enum OrderStatusType {

    VERIFIED("Verified"),
    REJECT("Rejected"),
    PENDING("Pending");

    private String label;

    OrderStatusType(final String label) {
        this.label = label;
    }

    public static OrderStatusType findByLabel(String label) {

        for(OrderStatusType status : OrderStatusType.values()) {
            if(status.label.equalsIgnoreCase(label)) {
                return status;
            }
        }
        return null;
    }

    @JsonValue
    public String toString() {
        return this.label;
    }

}
