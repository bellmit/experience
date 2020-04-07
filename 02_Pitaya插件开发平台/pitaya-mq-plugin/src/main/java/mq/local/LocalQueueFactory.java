package mq.local;

import com.google.common.collect.Maps;
import mq.Topic;
import mq.config.MqConfigReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

@Component
public class LocalQueueFactory {

  private final MqConfigReader mqConfigReader;
  private final Map<String, LocalQueue> queueMap = Maps.newConcurrentMap();

  @Autowired
  public LocalQueueFactory(MqConfigReader mqConfigReader) {
    this.mqConfigReader = mqConfigReader;
  }


  public synchronized <K, V> LocalQueue<K, V> getOrCreateLocalQueue(Topic<K, V> topic) {
    checkNotNull(topic);
    if (!queueMap.containsKey(topic.name)) {
      LocalQueue<K, V> localQueue =
          new LocalQueue<>(topic, mqConfigReader.queueMqConfig(topic.name));
      queueMap.put(topic.name, localQueue);
    }
    return queueMap.get(topic.name);
  }
}
