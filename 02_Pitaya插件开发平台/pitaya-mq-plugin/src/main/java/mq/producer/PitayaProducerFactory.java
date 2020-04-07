package mq.producer;

import com.google.common.collect.Maps;
import mq.Topic;
import mq.config.MqConfigReader;
import mq.local.LocalQueue;
import mq.local.LocalQueueFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Map;

@Component
public class PitayaProducerFactory {
  private final MqConfigReader mqConfigReader;
  private final LocalQueueFactory localQueueFactory;

  private final Map<String, PitayaProducer> pitayaProducerMap = Maps.newHashMap();

  @Inject
  public PitayaProducerFactory(MqConfigReader mqConfigReader, LocalQueueFactory localQueueFactory) {
    this.mqConfigReader = mqConfigReader;
    this.localQueueFactory = localQueueFactory;
  }

  public synchronized <K, V> PitayaProducer<K, V> getOrCreateProducer(Topic<K, V> topic) {
    if (!pitayaProducerMap.containsKey(topic.name)) {
      PitayaProducer<K, V> producer;

      switch (mqConfigReader.producerMode(topic.name)) {
        case MqConfigReader.MODE_KAFKA:
          producer = new KafkaPitayaProducer<>(topic, mqConfigReader.kafkaMqConfig(topic.name));
          break;
        case MqConfigReader.MODE_RESTFUL:
          producer = new RestfulPitayaProducer<>(topic, mqConfigReader.restfulMqConfig(topic.name));
          break;
        case MqConfigReader.MODE_WEB_SOCKET:
          producer =
              new WebSocketPitayaProducer<>(topic, mqConfigReader.webSocketMqConfig(topic.name));
          break;
        case MqConfigReader.MODE_LOCAL:
        default:
          LocalQueue<K, V> localQueue = localQueueFactory.getOrCreateLocalQueue(topic);
          producer = new LocalPitayaProducer<>(topic, localQueue);
          break;
      }

      pitayaProducerMap.put(topic.name, producer);
    }
    return pitayaProducerMap.get(topic.name);
  }

  public synchronized <K, V> PitayaProducer<K, V> getOrCreateProducer(String topic,
      Class<K> keyClass, Class<V> valClass) {
    return getOrCreateProducer(Topic.getOrCreate(topic, keyClass, valClass));
  }

}
