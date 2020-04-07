package mq.local;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import mq.Topic;
import mq.config.QueueMqConfig;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

public class LocalQueue<K, V> {
  private static final Logger logger = LoggerFactory.getLogger(LocalQueue.class);

  private final Topic<K, V> topic;
  private final QueueMqConfig config;

  private final BlockingQueue<Triple<LocalDateTime, K, V>> queue = Queues.newLinkedBlockingQueue();

  LocalQueue(Topic<K, V> topic, QueueMqConfig queueMqConfig) {
    this.topic = topic;
    config = queueMqConfig;
  }

  public void offer(K k, V v) {
    checkNotNull(k);
    checkNotNull(v);

    while (isOverflow() || isOutOfDate()) {
      logger.debug(String.format("Topic: %s is over flow or out of date.", topic.name));
      queue.poll();
    }
    Triple<LocalDateTime, K, V> triple = Triple.of(LocalDateTime.now(), k, v);
    boolean offer = queue.offer(triple);
    if (!offer) {
      logger.warn(String.format("Topic: %s offer fail.", topic.name));
    }
  }

  public Pair<K, V> poll() {
    try {
      Triple<LocalDateTime, K, V> triple = queue.take();
      return Pair.of(triple.getMiddle(), triple.getRight());
    } catch (InterruptedException e) {
      logger.error(String.format("Topic: %s take fail.", topic.name), e);
      Thread.currentThread().interrupt();
      return null;
    }
  }

  public List<Pair<K, V>> pollAll() {
    ArrayList<Triple<LocalDateTime, K, V>> tripleArrayList = Lists.newArrayList();
    queue.drainTo(tripleArrayList);
    return tripleArrayList.stream() //
        .map(triple -> Pair.of(triple.getMiddle(), triple.getRight())) //
        .collect(Collectors.toList());
  }

  private boolean isOutOfDate() {
    LocalDateTime compareTime = LocalDateTime.now().minusSeconds(config.keepTime);
    Triple<LocalDateTime, K, V> peek = queue.peek();
    return peek != null && peek.getLeft().isBefore(compareTime);
  }

  private boolean isOverflow() {
    return queue.size() >= config.keepLength;
  }

  public int size() {
    return queue.size();
  }
}
