package mq.config;

/**
 * Created by Administrator on 2017/2/22.
 */
public class RestfulMqConfig {
  public final String host; // localhost
  public final String port; // 8080

  RestfulMqConfig(String host, String port) {
    this.host = host;
    this.port = port;
  }
}
