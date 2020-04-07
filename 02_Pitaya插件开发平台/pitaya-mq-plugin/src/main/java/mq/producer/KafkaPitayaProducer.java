package mq.producer;

import mq.Topic;
import mq.config.KafkaMqConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import static com.google.common.base.Preconditions.checkNotNull;

class KafkaPitayaProducer<K, V> extends PitayaProducer<K, V> {

  private Producer<K, V> producer;

  KafkaPitayaProducer(Topic<K, V> topic, KafkaMqConfig kafkaMqConfig) {
    super(topic);
    producer = new KafkaProducer<>(kafkaMqConfig.kafkaProperties);
  }

  @Override
  public void send(K k, V v) {
    checkNotNull(k);
    checkNotNull(v);
    producer.send(new ProducerRecord<>(topic.name, k, v));
  }
}
