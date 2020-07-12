package com.jt.kafka.demo.kafkademoapp.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.*;

/**
 * Just a simple POJO that contains the order detail
 *
 * Created by Jason Tao on 7/7/2020
 */
public class OrderDetail {

    private int id;

    @JsonProperty("orderStatus")
    private OrderStatusType orderStatus;

    @JsonProperty("productsList")
    private Set<Product> productsList = new HashSet<>();

    @JsonProperty("additionalProperties")
    public Map<String, Object> additionalProperties = new HashMap<>();

    public OrderDetail() {}

    public OrderDetail(int id, OrderStatusType orderStatus, Set<Product> productsList) {
        this.id = id;
        this.orderStatus = orderStatus;
        this.productsList = productsList;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public OrderStatusType getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatusType orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Set<Product> getProductsList() {
        return productsList;
    }

    public void setProductsList(Set<Product> productsList) {
        this.productsList = productsList;
    }

    public Map<String, Object> getAdditionalProperties() {
        return additionalProperties;
    }

    public void setAdditionalProperties(String key, Object value) {
        this.additionalProperties.put(key, value);
    }
}
