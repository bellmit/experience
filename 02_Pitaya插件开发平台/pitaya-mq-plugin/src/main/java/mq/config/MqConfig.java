package mq.config;


import mq.consumer.LocalConsumerTransferManager;
import mq.producer.ProducerWebSocketHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import websocket.WebSocketServer;

@Configuration
public class MqConfig {
  @Bean(name = "mqProducerWebSocketServer")
  public WebSocketServer mqProducerWebSocketServer(ProducerWebSocketHandler handler) {
    return new WebSocketServer("producer", handler);
  }

  @Bean(name = "mqConsumerWebSocketServer")
  public WebSocketServer mqConsumerWebSocketServer(LocalConsumerTransferManager handler) {
    return new WebSocketServer("consumer", handler);
  }
}
