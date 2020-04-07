package mq.producer;

import mq.MqMsg;
import mq.Topic;
import mq.config.WebSocketMqConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import websocket.WebSocketClient;
import websocket.WebSocketConnection;
import websocket.WebSocketMsg;

import java.util.Random;

class WebSocketPitayaProducer<K, V> extends PitayaProducer<K, V> {
  private static final Logger logger = LoggerFactory.getLogger(WebSocketPitayaProducer.class);
  private final WebSocketClient webSocketClient;
  private final WebSocketMqConfig config;

  WebSocketPitayaProducer(Topic<K, V> topic, WebSocketMqConfig config) {
    super(topic);
    this.config = config;
    String url = String.format("ws://%s:%s/producer", config.host, config.port);
    webSocketClient =
        new WebSocketClient(System.getenv("COMPUTERNAME") + new Random().nextInt(1000), url);
    webSocketClient.connect();
  }

  @Override
  public void send(K k, V v) {
    WebSocketConnection connection = webSocketClient.getConnection();
    if (connection != null && connection.isOpen()) {
      MqMsg<K, V> mqMsg = new MqMsg<>(topic, k, v);
      WebSocketMsg webSocketMsg = new WebSocketMsg("producer", MqMsg.encode(mqMsg));
      connection.send(webSocketMsg);
    } else {
      logger.debug(String.format("Connection offline, topic: %s, host: %s, port: %s", topic.name,
          config.host, config.port));
    }
  }
}
