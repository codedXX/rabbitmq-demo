# RabbitMQ 持久化说明与 Apifox 测试

## 持久化代码位置

本项目的持久化由以下三部分共同完成。只有三者都具备时，未被消费的消息才能在 RabbitMQ 服务重启后保留。

| 持久化对象 | 代码位置 | 关键代码 | 作用 |
| --- | --- | --- | --- |
| 交换机 | `src/main/java/com/example/rabbitmqdemo/MessageConsumer.java` | `@Exchange(..., durable = "true")` | 将 `demo.exchange` 声明为持久化交换机。 |
| 队列 | `src/main/java/com/example/rabbitmqdemo/MessageConsumer.java` | `@Queue(..., durable = "true")` | 将 `demo.queue` 声明为持久化队列。 |
| 消息 | `src/main/java/com/example/rabbitmqdemo/service/MessageProducer.java` | `setDeliveryMode(MessageDeliveryMode.PERSISTENT)` | 将每条发送的业务消息标记为持久消息。 |

交换机、队列和绑定关系会在 Spring Boot 应用启动时，由 `@RabbitListener` 自动声明。消息持久化通过 `RabbitTemplate.convertAndSend` 的消息后处理器设置。

> 注意：持久化只保证 RabbitMQ 服务正常写盘后，尚未被消费者确认的消息可以恢复。当前消费者收到消息后会立即成功处理，因此要验证消息重启后仍存在，需要先停止应用中的消费者，或暂时让消费者不确认消息。

## 启动前检查

1. 启动本地 RabbitMQ，确保 AMQP 地址为 `localhost:5672`，账号密码为 `admin/admin`。
2. 启动 Spring Boot 应用：

```bash
./mvnw spring-boot:run
```

如果项目没有 Maven Wrapper，则使用：

```bash
mvn spring-boot:run
```

3. 在 RabbitMQ 管理后台确认存在以下资源：
   - 交换机：`demo.exchange`，类型 `direct`，`Durable` 为 `true`。
   - 队列：`demo.queue`，`Durable` 为 `true`。
   - 绑定：`demo.exchange` 使用路由键 `demo` 绑定到 `demo.queue`。

## 使用 Apifox 发送消息

在 Apifox 新建 HTTP 请求，并按下面配置：

| 配置项 | 值 |
| --- | --- |
| 请求方法 | `POST` |
| 请求地址 | `http://localhost:8081/messages` |
| 请求头 | `Content-Type: text/plain` |
| Body 类型 | `Raw` / `Text` |
| Body 内容 | `persistent-message-001` |

点击发送后，预期得到 `200 OK`，响应内容为：

```text
sent: persistent-message-001
```

应用日志会依次出现生产者确认成功日志和消费者接收日志，例如：

```text
publisher confirm success, correlationId=...
received message: persistent-message-001
```

## 验证持久化

默认消费者会迅速消费消息，不能通过已经消费的消息验证持久化。请按下面步骤关闭消费者后，仍使用 Apifox 发送消息。

1. 使用以下命令启动应用。此命令仅禁用消费者监听，HTTP 接口和生产者仍正常工作：

```bash
SPRING_RABBITMQ_LISTENER_SIMPLE_AUTO_STARTUP=false mvn spring-boot:run
```

2. 在 Apifox 按“使用 Apifox 发送消息”中的配置发送 `persistent-message-001`。
3. 打开 RabbitMQ 管理后台，确认 `demo.queue` 的 `Ready` 数量增加，且消息尚未被消费。
4. 重启 RabbitMQ 服务。
5. 回到 RabbitMQ 管理后台，确认 `demo.exchange`、`demo.queue` 仍存在，且 `demo.queue` 的 `Ready` 数量未丢失。
6. 停止第 1 步的应用，再使用普通命令 `mvn spring-boot:run` 启动应用。观察日志出现 `received message: persistent-message-001`，同时 `Ready` 数量归零。

这套步骤中，业务消息由 Apifox 通过 `POST /messages` 发送；RabbitMQ 管理后台仅用于观察未消费消息和重启后的恢复结果。
