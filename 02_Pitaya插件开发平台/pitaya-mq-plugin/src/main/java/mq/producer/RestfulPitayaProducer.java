package mq.producer;

import mq.MqMsg;
import mq.Topic;
import mq.config.RestfulMqConfig;
import org.springframework.web.client.RestTemplate;

class RestfulPitayaProducer<K, V> extends PitayaProducer<K, V> {
  private RestTemplate restTemplate = new RestTemplate();
  private String url;

  RestfulPitayaProducer(Topic<K, V> topic, RestfulMqConfig config) {
    super(topic);
    this.url = String.format("http://%s:%s/mq/producer", config.host, config.port);
  }

  @Override
  public void send(K k, V v) {
    restTemplate.put(url, new MqMsg<>(topic, k, v));
  }
}
