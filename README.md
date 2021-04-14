# rabbitmq

## RabbitMQ server
rabbitmq server credentials can be found in application.yml file.

Start rabbitmq server in docker  
``
docker-compose up
``

## How to run

POST - http://localhost:9001/api/v1/orders/producer

Send Order data

Body 

```
{
    "orderId": "order4",
    "itemName": "pen",
    "quantity": 4
}
```

GET - http://localhost:9002/api/v1/orders/consumer

Received Orders

