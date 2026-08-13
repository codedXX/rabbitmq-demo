# RabbitMQ Demo Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build a minimal Spring Boot 3.x Maven application that publishes HTTP messages to a local RabbitMQ queue and consumes them.

**Architecture:** Spring Boot exposes `POST /messages`, `RabbitTemplate` publishes plain text to `demo.queue`, and a `@RabbitListener` logs consumed messages. Spring configuration declares the queue and reads local RabbitMQ connection settings from `application.yml`.

**Tech Stack:** Java 17, Spring Boot 3.5.x, Spring AMQP, Maven, JUnit 5, Mockito.

## Global Constraints

- Use Spring Boot 3.x and Java 17.
- Use only the `spring-boot-starter-amqp` and `spring-boot-starter-web` starters plus the Spring Boot test starter.
- Connect to RabbitMQ at `localhost:5672` with `guest/guest` by default.
- Keep the example limited to one queue, one producer endpoint, and one consumer.

---

### Task 1: Create the Maven project skeleton

**Files:**
- Create: `pom.xml`
- Create: `.gitignore`
- Create: `src/main/java/com/example/rabbitmqdemo/RabbitmqDemoApplication.java`
- Create: `src/main/resources/application.yml`

**Interfaces:**
- Produces the Spring Boot application entry point and build configuration used by later tasks.

- [ ] **Step 1: Create the Maven metadata and ignore rules**

  Configure the Spring Boot parent, Java 17, the AMQP/web/test starters, and the Spring Boot Maven plugin. Ignore `target/` and IDE files.

- [ ] **Step 2: Add the application entry point and local connection defaults**

  Create the `main` method with `SpringApplication.run` and configure `spring.rabbitmq.host`, `port`, `username`, and `password` in YAML.

- [ ] **Step 3: Run the compile check**

  Run: `mvn -q -DskipTests compile`

  Expected: exit code 0 and compiled classes under `target/classes`.

### Task 2: Add RabbitMQ publishing and consuming

**Files:**
- Create: `src/main/java/com/example/rabbitmqdemo/RabbitMqConfig.java`
- Create: `src/main/java/com/example/rabbitmqdemo/MessageProducer.java`
- Create: `src/main/java/com/example/rabbitmqdemo/MessageConsumer.java`
- Create: `src/main/java/com/example/rabbitmqdemo/MessageController.java`

**Interfaces:**
- `MessageProducer.send(String message): void` publishes to `demo.queue`.
- `MessageController.send(String message): String` accepts `POST /messages` and returns `sent: <message>`.
- `MessageConsumer.receive(String message): void` is invoked by the `demo.queue` listener and logs the message.

- [ ] **Step 1: Add the failing controller test**

  Test that posting plain text to `/messages` returns HTTP 200 with `sent: hello` and invokes `MessageProducer.send("hello")`.

- [ ] **Step 2: Run the focused test to verify it fails**

  Run: `mvn -q -Dtest=MessageControllerTest test`

  Expected: FAIL because the controller and producer do not exist yet.

- [ ] **Step 3: Implement the minimal queue, producer, consumer, and controller**

  Declare a durable `demo.queue`, inject `RabbitTemplate` into the producer, annotate the consumer with `@RabbitListener(queues = RabbitMqConfig.QUEUE_NAME)`, and map `POST /messages` with `text/plain` request content.

- [ ] **Step 4: Run the focused test to verify it passes**

  Run: `mvn -q -Dtest=MessageControllerTest test`

  Expected: PASS.

### Task 3: Verify the complete project

**Files:**
- Test: `src/test/java/com/example/rabbitmqdemo/MessageControllerTest.java`

**Interfaces:**
- The project is runnable with `mvn spring-boot:run` when local RabbitMQ is available.

- [ ] **Step 1: Run all tests**

  Run: `mvn -q test`

  Expected: exit code 0.

- [ ] **Step 2: Package the application**

  Run: `mvn -q package -DskipTests`

  Expected: exit code 0 and a Spring Boot jar under `target/`.

- [ ] **Step 3: Inspect the final files and report the local run command**

  Confirm the endpoint is `POST http://localhost:8080/messages` with a plain-text body, and provide the command to start the app.

