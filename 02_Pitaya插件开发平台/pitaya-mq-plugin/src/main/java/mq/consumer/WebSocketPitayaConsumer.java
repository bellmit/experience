package mq.consumer;


import mq.MqMsg;
import mq.Topic;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import websocket.WebSocketConnection;
import websocket.WebSocketMsg;

class WebSocketPitayaConsumer<K, V> extends PitayaConsumer<K, V> {
  private static final Logger logger = LoggerFactory.getLogger(WebSocketPitayaConsumer.class);

  private final WebSocketConnection connection;

  WebSocketPitayaConsumer(@NotNull Topic<K, V> topic, @NotNull WebSocketConnection connection) {
    super(topic);
    this.connection = connection;
  }

  @Override
  public void receive(K key, V val) {
    if (connection.isOpen()) {
      connection.send(new WebSocketMsg("consumer", MqMsg.encode(new MqMsg<>(topic, key, val))));
    } else {
      logger.warn(String.format("Connection offline, topic: %s, connection: %s", topic.name,
          connection.getName()));
    }
  }
}
