# rabbitmq

Reference : 

https://www.rabbitmq.com/tutorials/amqp-concepts.html

https://www.javainuse.com/messaging/rabbitmq/exchange

## RabbitMQ server
rabbitmq server credentials can be found in application.yml file.

Start rabbitmq server in docker using [docker compose](docker-compose.yml)

``
docker-compose up
``

### How to run using different exchange types

Import [PostMan collection file](RabbitMQ.postman_collection.json) and can test APIs.

Direct exchange - POST - http://localhost:9001/api/v1/orders/direct/producer  

Fanout exchange - POST - http://localhost:9001/api/v1/orders/fanout/producer  

Topic exchange - POST - http://localhost:9001/api/v1/orders/topic/producer  

Header exchange - POST - http://localhost:9001/api/v1/orders/header/producer  

Body 

```
{
    "orderId": "order",
    "itemName": "pen",
    "quantity": 2
}
```

Received Orders   

GET - http://localhost:9001/api/v1/orders/consumer

