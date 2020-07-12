package com.jt.kafka.demo.kafkademoapp.services;

import com.jt.kafka.demo.kafkademoapp.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * Inventory service uses a static set to keep track of the product inventory.
 * It verifies the product quantity from the order detail and determines the order status.
 *
 * Created by Jason Tao on 7/7/2020
 */
@Service
public class InventoryService {

    private static final Logger logger = LoggerFactory.getLogger(InventoryService.class);

    private static Set<Product> inventoryProductsList = new HashSet<>();
    private static int idCounter = 0;

    static {
        inventoryProductsList.add(new Product(++idCounter, "Coke", 2, 3.99));
        inventoryProductsList.add(new Product(++idCounter, "Poptart", 5, 2.19));
        inventoryProductsList.add(new Product(++idCounter, "Vitamin D3", 10, 3.49));
    }

    /**
     * Checks with the inventory if there is sufficient quantity for each ordered product.
     * Returns an error response for each questioned product.
     *
     * @param orderedProducts
     * @return
     */
    public Set<String> checkInventoryFromOrderDetail(Set<Product> orderedProducts) {

        logger.info("Inventory service is verifying order of products.");

        Set<String> orderErrResponse = new HashSet<>();

        orderedProducts.forEach(orderedProduct -> {
            Product foundProduct = inventoryProductsList.stream()
                    .filter(availableProduct -> availableProduct.getProductName().equals(orderedProduct.getProductName()))
                    .findFirst().orElse(null);
            if (foundProduct != null) {
                if (orderedProduct.getQuantity() < foundProduct.getQuantity()) {
                    logger.debug("Insufficient quantity for product [" + orderedProduct.getProductName() + "]");
                    orderErrResponse.add("Insufficient quantity for product [" + orderedProduct.getProductName() + "]");
                }
            } else {
                logger.debug("Product [" + orderedProduct.getProductName() + "] is not available.");
                orderErrResponse.add("Product [" + orderedProduct.getProductName() + "] is not available.");
            }
        });

        return orderErrResponse;
    }

}
