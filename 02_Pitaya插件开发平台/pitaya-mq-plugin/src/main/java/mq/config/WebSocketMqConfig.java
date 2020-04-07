package mq.config;

public class WebSocketMqConfig {
  public final String host; // localhost
  public final String port; // 8080

  WebSocketMqConfig(String host, String port) {
    this.host = host;
    this.port = port;
  }
}
