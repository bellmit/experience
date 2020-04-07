package mq.consumer;

import com.google.common.collect.Lists;
import mq.Topic;
import mq.config.LocalMqConfig;
import mq.local.LocalQueue;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


class LocalConsumerTransfer<K, V> extends ConsumerTransfer<K, V> {
  private static final Logger logger = LoggerFactory.getLogger(LocalConsumerTransfer.class);
  private final LocalQueue<K, V> localQueue;

  LocalConsumerTransfer(Topic<K, V> topic, LocalMqConfig config, LocalQueue<K, V> localQueue) {
    super(topic, Lists.newLinkedList());
    this.localQueue = localQueue;
    Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(this::transfer, 10,
        config.period, TimeUnit.SECONDS);
  }

  private void transfer() {
    try {
      int size = localQueue.size();
      do {
        if (size > 1) {
          notify(localQueue.pollAll());
        } else {
          Pair<K, V> pair = localQueue.poll();
          if (pair != null) {
            notify(pair.getLeft(), pair.getRight());
          }
        }
        size = localQueue.size();
      } while (localQueue.size() > 0);
    } catch (RuntimeException e) {
      logger.error("local consumer transfer error.", e);
    }
  }

  void addConsumer(PitayaConsumer<K, V> pitayaConsumer) {
    super.pitayaConsumers.add(pitayaConsumer);
  }

  void removeConsumer(PitayaConsumer<K, V> pitayaConsumer) {
    super.pitayaConsumers.remove(pitayaConsumer);
  }
}
