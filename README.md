# Getting Started

##### TravisCI build status: [![Linux Build Status](https://travis-ci.org/jastao/kafka-demo-app.svg?branch=master)](https://travis-ci.org/jastao/kafka-demo-app)

### Reference Documentation

Event driven architecture is used in developing software application in various business domain nowadays. It has been utilized in 
microservices as a communication mechanism between services. With different publish and subscribe messaging frameworks out in the
market, Apache Kafka is one of the most widely used framework. 

Kakfa is known for its asynchronous communication pattern. The request-reply semantics are not natural to Kafka, which provides
synchronous communication between the producer and consumer. The request-reply semantics are now made possible thanks to Spring Kafka.

 # Apache Kafka Installation
 
 This project is using the following docker image for installing Apache Kafka
 
 Docker image: wurstmeister/kafka
 
 Kafka also depends on ZooKeeper to store its metadata. The following docker image is used for ZooKeeper.
 
 Docker image: wurstmeister/zookeeper
 
 Execute the following docker command in the command prompt to download the docker images and start up Kakfa with
 a single broker on localhost IP (127.0.0.1) with a single partition defined programmatically.
 
 docker-compose up 
 
 To stop the containers, execute 
 
 docker-compose stop
 
 ## Spring Kafka demo
 
 This is a simple Spring boot application on showing how to use Spring Kakfa to send synchronous communciation 
 between services. This application also shows how to use the request-reply semantics provided by Spring Kafka
 to achieve synchronous response. 
 
 For simplicity purpose, we will use static collections to serve as data repository.

 This application consists of three dummy services: OrderService, InventoryService and PaymentService. 
 
 ##### Order Service
 
 The OrderService retrieves the order detail by order id and sends the "inventory-request" topic with the order detail 
 to KAFKA using the Kakfa producer under the group (inventory-consumer-group). The Kafka consumer listens on the topic and receives the order detail, then later calls
 the inventory service with the order detail to verify it. It expects a synchronous response (e.g returned order detail
 with the order status or with the error response) from the producer's call.
 
 When the OrderService receives instruction to process payment, it checks the order status. If the status is "verified", 
 then the order detail is sent to Kakfa on (payment-request-topic) topic and (inventory-consumer-group) group. Once the 
 Kakfa consumer receives the order detail, it will send the order to the payment service for further processing.
 
 ##### Inventory Service

 The InventoryService verifies the quantity of each product in the order with the inventory. If the inventory has enough
 product quantity for all product within the order, the order status will change to "verified". If the inventory do not have 
 sufficient quantity, an error response (just a simple string) that states this product does have enough quantity or does not exist
 in the inventory adds to the order detail to be sent back to the caller as response. The order status will change to "rejected". 
 The returned order detail is returned back with the (inventory-response-topic) reply-response topic. 
 This is handled by the Kafka consumer automatically. 

 ##### Payment Service

The Payment Service receives the order detail from the Kafka consumer when the topic comes in. The service calculates the
sub total of the order and simply logs the result. 

 # Available REST Endpoints
 
 The root URL is "http://localhost:8080"
 
 URL | Method | Description
 --- | --- | ---
 /orders/{id} | GET | Gets the order detail by order id.
 /orders | GET | Gets all the order details.
 /orders/{id}/verify | POST | Verifies the order with the inventory to see if all the products are available. 
 /orders/{id}/checkout | POST | Checks if the order status is verified. If so, process payment request.
 
 #### What's next
 
 It would great to investigate how to use Spring Cloud Stream with Kakfa. Spring cloud stream relies on Spring Kakfa.
