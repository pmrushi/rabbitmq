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

Login to RabbitMQ server using below url

```
http://localhost:15672/

By default
Username: guest
Password: guest

```


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

### Setup SonarQube

```
docker-compose -f sonarqube-docker-compose.yml up

```
Login to SonarQube server using below url
```
http://localhost:9000/

By default
Username: admin
Password: admin

```

### Run SonarQube
Create a project key in SoncaQube and generate and use in below command.

```
./gradlew sonarqube \
  -Dsonar.projectKey=rabbitmq \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.login=65ed680b57e16ea9964cd221f4a6a1afcb5d023e
```



