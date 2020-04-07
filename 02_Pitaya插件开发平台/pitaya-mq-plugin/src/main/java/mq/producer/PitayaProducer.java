package mq.producer;

import mq.Topic;

public abstract class PitayaProducer<K, V> {
  protected Topic<K, V> topic;

  PitayaProducer(Topic<K, V> topic) {
    this.topic = topic;
  }

  PitayaProducer(String topic, Class<K> keyClass, Class<V> valClass) {
    this(Topic.getOrCreate(topic, keyClass, valClass));
  }

  public abstract void send(K k, V v);
}
