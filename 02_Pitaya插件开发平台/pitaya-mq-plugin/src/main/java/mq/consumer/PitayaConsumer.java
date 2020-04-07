package mq.consumer;


import mq.Topic;

public abstract class PitayaConsumer<K, V> {
  final Topic<K, V> topic;

  protected PitayaConsumer(String topic, Class<K> keyClass, Class<V> valueClass) {
    this(Topic.getOrCreate(topic, keyClass, valueClass));
  }

  protected PitayaConsumer(Topic<K, V> topic) {
    this.topic = topic;
  }

  public abstract void receive(K key, V val);
}
