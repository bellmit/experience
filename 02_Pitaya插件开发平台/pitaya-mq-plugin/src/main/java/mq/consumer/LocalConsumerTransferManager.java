package mq.consumer;

import com.google.common.collect.Maps;
import mq.Topic;
import mq.config.MqConfigReader;
import mq.local.LocalQueue;
import mq.local.LocalQueueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import websocket.WebSocketConnection;
import websocket.WebSocketHandler;
import websocket.WebSocketMsg;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Component
public class LocalConsumerTransferManager implements WebSocketHandler {
  private static final Logger logger = LoggerFactory.getLogger(LocalConsumerTransferManager.class);

  private Map<String, LocalConsumerTransfer> topicTransferMap = Maps.newHashMap();
  private Map<WebSocketConnection, WebSocketPitayaConsumer> connConsumerMap = Maps.newHashMap();

  private final LocalQueueFactory localQueueFactory;

  private final MqConfigReader mqConfigReader;

  @Inject
  public LocalConsumerTransferManager(LocalQueueFactory localQueueFactory,
      MqConfigReader mqConfigReader) {
    this.localQueueFactory = localQueueFactory;
    this.mqConfigReader = mqConfigReader;
  }

  private synchronized <K, V> void addConsumer(PitayaConsumer<K, V> consumer) {
    Topic<K, V> topic = consumer.topic;
    String name = topic.name;

    LocalConsumerTransfer<K, V> transfer;
    if (topicTransferMap.containsKey(name)) {
      transfer = topicTransferMap.get(name);
    } else {
      LocalQueue<K, V> localQueue = localQueueFactory.getOrCreateLocalQueue(topic);
      transfer = new LocalConsumerTransfer<>(topic, mqConfigReader.localMqConfig(name), localQueue);
      topicTransferMap.put(name, transfer);
    }

    transfer.addConsumer(consumer);
  }

  void addConsumers(List<PitayaConsumer> topicConsumers) {
    topicConsumers.forEach(this::addConsumer);
  }

  @Override
  public void connectionClosed(WebSocketConnection connection) {
    if (connConsumerMap.containsKey(connection)) {
      WebSocketPitayaConsumer consumer = connConsumerMap.get(connection);
      LocalConsumerTransfer transfer = topicTransferMap.get(consumer.topic.name);

      transfer.removeConsumer(consumer);

      connConsumerMap.remove(connection);
    } else {
      logger.warn(String.format("This connection not has consumer: %s", connection.getName()));
    }
  }

  @Override
  public void onMsg(WebSocketConnection connection, WebSocketMsg msg) {
    if ("subscribe".equalsIgnoreCase(msg.getCmd())) {
      Topic topic = Topic.decode(msg.getBody());
      WebSocketPitayaConsumer consumer = new WebSocketPitayaConsumer(topic, connection);
      addConsumer(consumer);
      connConsumerMap.put(connection, consumer);
    } else {
      logger.warn(String.format("Not support this cmd: %s, only support subscribe", msg.getCmd()));
    }
  }
}
