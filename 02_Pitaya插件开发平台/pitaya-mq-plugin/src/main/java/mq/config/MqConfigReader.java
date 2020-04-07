package mq.config;

import org.pitaya.kernel.PitayaApplication;
import org.springframework.stereotype.Component;

import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Component
public class MqConfigReader {
  public static final String MODE_KAFKA = "Kafka";
  public static final String MODE_LOCAL = "Local";
  public static final String MODE_RESTFUL = "Restful";
  public static final String MODE_WEB_SOCKET = "WebSocket";

  private final Set<String> topics;
  private final Properties properties;

  MqConfigReader() {
    this(PitayaApplication.getProperties());
  }

  public MqConfigReader(Properties properties) {
    this.properties = properties;
    String mqTopics = properties.getProperty("mq.topics", "");

    this.topics = Stream.of(mqTopics.split(",")).map(String::trim).collect(Collectors.toSet());
  }

  public Set<String> topics() {
    return topics;
  }

  public String producerMode(String topic) {
    return mode(properties.getProperty(String.format("mq.%s.producer.mode", topic)));
  }

  public String consumerMode(String topic) {
    return mode(properties.getProperty(String.format("mq.%s.consumer.mode", topic)));
  }

  private String mode(String mode) {
    if (MODE_KAFKA.equalsIgnoreCase(mode)) {
      return MODE_KAFKA;
    } else if (MODE_RESTFUL.equalsIgnoreCase(mode)) {
      return MODE_RESTFUL;
    } else if (MODE_WEB_SOCKET.equalsIgnoreCase(mode)) {
      return MODE_WEB_SOCKET;
    } else {
      return MODE_LOCAL;
    }
  }

  private int consumerPeriod(String topic) {
    String periodStr = properties.getProperty(String.format("mq.%s.consumer.period", topic), "10");
    return Integer.parseUnsignedInt(periodStr);
  }

  public KafkaMqConfig kafkaMqConfig(String topic) {
    String prefix = String.format("mq.%s.kafka.", topic);

    Properties kafkaProperties = new Properties();
    properties
        .entrySet()
        .stream()
        //
        .filter(entry -> String.valueOf(entry.getKey()).startsWith(prefix))
        //
        .forEach(
            entry -> kafkaProperties.put(String.valueOf(entry.getKey()).substring(prefix.length()),
                entry.getValue()));

    kafkaProperties.putIfAbsent("acks", "all");
    kafkaProperties.putIfAbsent("retries", 0);
    kafkaProperties.putIfAbsent("batch.size", 16384);
    kafkaProperties.putIfAbsent("linger.ms", 1);
    kafkaProperties.putIfAbsent("buffer.memory", 33554432);

    kafkaProperties.putIfAbsent("session.timeout.ms", "100000");

    return new KafkaMqConfig(consumerPeriod(topic), kafkaProperties);
  }

  public LocalMqConfig localMqConfig(String topic) {
    return new LocalMqConfig(consumerPeriod(topic));
  }

  public RestfulMqConfig restfulMqConfig(String topic) {
    String host = properties.getProperty(String.format("mq.%s.server.host", topic), "localhost");
    String port = properties.getProperty(String.format("mq.%s.server.port", topic), "8080");
    return new RestfulMqConfig(host, port);
  }

  public WebSocketMqConfig webSocketMqConfig(String topic) {
    String host = properties.getProperty(String.format("mq.%s.server.host", topic), "localhost");
    String port = properties.getProperty(String.format("mq.%s.server.port", topic), "8080");
    return new WebSocketMqConfig(host, port);
  }

  public QueueMqConfig queueMqConfig(String topic) {
    String keepLength =
        properties.getProperty(String.format("mq.%s.queue.keep.length", topic), "1000");
    String keepTime = properties.getProperty(String.format("mq.%s.queue.keep.time", topic), "60");
    return new QueueMqConfig(Integer.parseUnsignedInt(keepLength),
        Integer.parseUnsignedInt(keepTime));
  }
}
