package com.jt.kafka.demo.kafkademoapp.model;

import java.io.Serializable;

/**
 * Simple POJO for the product
 *
 * Created by Jason Tao on 7/6/2020
 */
public class Product implements Serializable{

    private long id;

    private String productName;

    private int quantity;

    private double price;

    public Product() {}

    public Product(String productName, int quantity, double price) {
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
    }

    public Product(long id, String productName, int quantity, double price) {
        this.id = id;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
    }

    public long getId() {
        return this.id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Product id=[" + id + "]" +
                "productName=[" + productName + "]" +
                "quantity=[" + quantity + "]" +
                "price=[" + price + "]";
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || this.getClass() != o.getClass()) return false;
        final Product product = (Product) o;
        return product.getProductName().equalsIgnoreCase(this.productName);
    }
}
