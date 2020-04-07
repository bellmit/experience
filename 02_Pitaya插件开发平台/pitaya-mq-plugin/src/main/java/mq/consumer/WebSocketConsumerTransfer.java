package mq.consumer;

import mq.MqMsg;
import mq.Topic;
import mq.config.WebSocketMqConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import websocket.WebSocketClient;
import websocket.WebSocketConnection;
import websocket.WebSocketHandler;
import websocket.WebSocketMsg;

import java.util.List;
import java.util.Random;

class WebSocketConsumerTransfer<K, V> extends ConsumerTransfer<K, V> implements WebSocketHandler {
  private static final Logger logger = LoggerFactory.getLogger(WebSocketConsumerTransfer.class);

  private final WebSocketClient webSocketClient;

  WebSocketConsumerTransfer(Topic<K, V> topic, List<PitayaConsumer<K, V>> pitayaConsumers,
      WebSocketMqConfig config) {
    super(topic, pitayaConsumers);

    String url = String.format("ws://%s:%s/consumer", config.host, config.port);
    webSocketClient =
        new WebSocketClient(System.getenv("COMPUTERNAME") + new Random().nextInt(1000), url, this);
    webSocketClient.connect();
  }

  @Override
  public void connectionCreated(WebSocketConnection connection) {
    connection.send(new WebSocketMsg("subscribe", Topic.encode(super.topic)));
  }

  @Override
  public void onMsg(WebSocketConnection connection, WebSocketMsg msg) {
    if ("consumer".equalsIgnoreCase(msg.getCmd())) {
      MqMsg<K, V> mqMsg = MqMsg.decode(msg.getBody());

      notify(mqMsg.getKey(), mqMsg.getVal());
    } else {
      logger.warn(String.format("Not support this cmd: %s, only support consumer", msg.getCmd()));
    }
  }
}
