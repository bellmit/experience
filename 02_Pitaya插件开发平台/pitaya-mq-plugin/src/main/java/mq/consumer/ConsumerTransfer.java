package mq.consumer;

import mq.Topic;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

abstract class ConsumerTransfer<K, V> {
  final Topic<K, V> topic;
  final List<PitayaConsumer<K, V>> pitayaConsumers;

  ConsumerTransfer(Topic<K, V> topic, List<PitayaConsumer<K, V>> pitayaConsumers) {
    this.topic = topic;
    this.pitayaConsumers = pitayaConsumers;
  }

  void notify(List<Pair<K, V>> recordList) {
    recordList.forEach(pair -> notify(pair.getKey(), pair.getValue()));
  }

  void notify(K key, V val) {
    pitayaConsumers.forEach(consumer -> consumer.receive(key, val));
  }
}
