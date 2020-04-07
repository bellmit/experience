package mq.producer;

import mq.MqMsg;
import mq.local.LocalQueue;
import mq.local.LocalQueueFactory;
import org.springframework.stereotype.Component;
import websocket.WebSocketConnection;
import websocket.WebSocketHandler;
import websocket.WebSocketMsg;

import javax.inject.Inject;

@Component
public class ProducerWebSocketHandler implements WebSocketHandler {
  private LocalQueueFactory localQueueFactory;

  @Inject
  public ProducerWebSocketHandler(LocalQueueFactory localQueueFactory) {
    this.localQueueFactory = localQueueFactory;
  }

  @Override
  public void onMsg(WebSocketConnection connection, WebSocketMsg msg) {
    String body = msg.getBody();
    MqMsg mqMsg = MqMsg.decode(body);

    LocalQueue localQueue = localQueueFactory.getOrCreateLocalQueue(mqMsg.topic());
    localQueue.offer(mqMsg.getKey(), mqMsg.getVal());
  }
}
