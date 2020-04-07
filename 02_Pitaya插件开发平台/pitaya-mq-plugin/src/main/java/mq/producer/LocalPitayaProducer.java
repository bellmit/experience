package mq.producer;

import mq.Topic;
import mq.local.LocalQueue;

class LocalPitayaProducer<K, V> extends PitayaProducer<K, V> {
  private final LocalQueue<K, V> localQueue;

  LocalPitayaProducer(Topic<K, V> topic, LocalQueue<K, V> localQueue) {
    super(topic);
    this.localQueue = localQueue;
  }

  @Override
  public void send(K k, V v) {
    localQueue.offer(k, v);
  }
}
