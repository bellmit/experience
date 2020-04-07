package mq.consumer;

import com.google.common.collect.Lists;
import mq.Topic;
import mq.config.KafkaMqConfig;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class KafkaConsumerTransfer<K, V> extends ConsumerTransfer<K, V> {

  private KafkaConsumer<K, V> consumer;

  KafkaConsumerTransfer(Topic<K, V> topic, List<PitayaConsumer<K, V>> topicConsumers,
      KafkaMqConfig config) {
    super(topic, topicConsumers);
    consumer = new KafkaConsumer(config.kafkaProperties);
    Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(this::transfer, 10,
        config.period, TimeUnit.SECONDS);
  }

  private void transfer() {
    consumer.subscribe(Collections.singleton(super.topic.name));

    ConsumerRecords<K, V> records = consumer.poll(1000);
    List<Pair<K, V>> recordList = Lists.newArrayList();

    records.forEach(x -> recordList.add(Pair.of(x.key(), x.value())));

    notify(recordList);
  }
}
