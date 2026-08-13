# RabbitMQ Demo Design

## Goal

Create the smallest runnable Spring Boot 3.x Maven project for testing RabbitMQ on the local Docker instance.

## Architecture

The application uses Spring AMQP with one durable queue named `demo.queue`. A `POST /messages` endpoint publishes the request body to that queue, and a listener consumes messages and prints them to the application log.

RabbitMQ is expected at `localhost:5672` with the default `guest/guest` credentials. Queue declaration is handled by Spring configuration so the application can start against an empty RabbitMQ vhost.

## Components

- `RabbitMqDemoApplication`: Spring Boot entry point.
- `RabbitMqConfig`: declares the queue and binds the listener to it.
- `MessageController`: accepts plain-text HTTP messages and delegates publishing.
- `MessageProducer`: sends text messages through `RabbitTemplate`.
- `MessageConsumer`: receives and logs text messages.

## Error handling

The demo intentionally has no custom retry, persistence, authentication, or dead-letter setup. Spring AMQP's default behavior is sufficient for local functional testing, and connection settings remain configurable through `application.yml`.

## Verification

- A focused Spring test verifies that the HTTP endpoint delegates the submitted message to the producer.
- `mvn test` verifies compilation and tests.
- `mvn package -DskipTests` verifies the runnable artifact can be packaged.

