package mq.consumer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import mq.config.MqConfigReader;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import websocket.WebSocketHandler;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
class ConsumerTransferFactory implements WebSocketHandler {
  private static final Logger logger = LoggerFactory.getLogger(ConsumerTransferFactory.class);

  private final MqConfigReader mqConfigReader;
  private final LocalConsumerTransferManager localConsumerTransferManager;
  private final List<ConsumerTransfer> transferList = Lists.newArrayList();

  @Autowired(required = false)
  private List<PitayaConsumer> consumers;

  @Inject
  ConsumerTransferFactory(MqConfigReader mqConfigReader,
      LocalConsumerTransferManager localConsumerTransferManager) {
    this.mqConfigReader = mqConfigReader;
    this.localConsumerTransferManager = localConsumerTransferManager;
  }

  @PostConstruct
  private void init() {
    if (CollectionUtils.isEmpty(consumers)) {
      logger.warn("no mq consumer found");
    } else {
      Map<String, List<PitayaConsumer>> consumerMap = groupByTopic();

      for (Map.Entry<String, List<PitayaConsumer>> entry : consumerMap.entrySet()) {
        String topic = entry.getKey();
        List<PitayaConsumer> topicConsumers = entry.getValue();

        PitayaConsumer consumer = topicConsumers.get(0);

        String mode = mqConfigReader.consumerMode(topic);
        switch (mode) {
          case MqConfigReader.MODE_KAFKA:
            transferList.add(new KafkaConsumerTransfer(consumer.topic, topicConsumers,
                mqConfigReader.kafkaMqConfig(topic)));
            break;
          case MqConfigReader.MODE_WEB_SOCKET:
            transferList.add(new WebSocketConsumerTransfer(consumer.topic, topicConsumers,
                mqConfigReader.webSocketMqConfig(topic)));
            break;
          case MqConfigReader.MODE_LOCAL:
          default:
            localConsumerTransferManager.addConsumers(topicConsumers);
            break;
        }
      }
    }
  }

  @NotNull
  private Map<String, List<PitayaConsumer>> groupByTopic() {
    Map<String, List<PitayaConsumer>> consumerMap = Maps.newHashMap();
    consumers.forEach(x -> {
      String topic = x.topic.name;
      List<PitayaConsumer> pitayaConsumerList = consumerMap.getOrDefault(topic, new ArrayList<>());

      pitayaConsumerList.add(x);

      consumerMap.put(topic, pitayaConsumerList);
    });
    return consumerMap;
  }
}
