package mq.config;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.pitaya.kernel.PitayaApplication;
import org.pitaya.kernel.PitayaException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;

public class MqConfigReaderTest {
  private MqConfigReader reader;

  @Before
  public void before() throws Exception {
    Properties properties = new Properties();
    try (InputStream is = PitayaApplication.class.getResourceAsStream("/application.properties")) {
      properties.load(is);
    } catch (IOException e) {
      throw new PitayaException("load application.properties error.", e);
    }

    reader = new MqConfigReader(properties);
  }

  @After
  public void after() throws Exception {}

  /**
   * Method: topics()
   */
  @Test
  public void testTopics() throws Exception {
    Set<String> topics = reader.topics();
    topics.forEach(System.out::println);
  }

  /**
   * Method: producerMode(String topic)
   */
  @Test
  public void testProducerMode() throws Exception {
    System.out.println(reader.producerMode("test1"));
    System.out.println(reader.producerMode("test2"));
    System.out.println(reader.producerMode("test3"));
    System.out.println(reader.producerMode("test4"));
    System.out.println(reader.producerMode("not config"));
  }

  /**
   * Method: consumerMode(String topic)
   */
  @Test
  public void testConsumerMode() throws Exception {
    System.out.println(reader.consumerMode("test1"));
    System.out.println(reader.consumerMode("test2"));
    System.out.println(reader.consumerMode("test3"));
    System.out.println(reader.consumerMode("test4"));
    System.out.println(reader.consumerMode("not config"));
  }

  /**
   * Method: kafkaMqConfig(String topic)
   */
  @Test
  public void testKafkaMqConfig() throws Exception {
    KafkaMqConfig config = reader.kafkaMqConfig("test1");
    System.out.println(config.period);

    config.kafkaProperties.entrySet().forEach(entry -> {
      System.out.println(String.format("%s = %s", entry.getKey(), entry.getValue()));
    });
  }

  /**
   * Method: localMqConfig(String topic)
   */
  @Test
  public void testLocalMqConfig() throws Exception {
    System.out.println(reader.localMqConfig("test1").period);
    System.out.println(reader.localMqConfig("test2").period);
    System.out.println(reader.localMqConfig("not config").period);
  }

  /**
   * Method: restfulMqConfig(String topic)
   */
  @Test
  public void testRestfulMqConfig() throws Exception {
    RestfulMqConfig config = reader.restfulMqConfig("test3");
    System.out.println(config.host);
    System.out.println(config.port);
  }

  /**
   * Method: webSocketMqConfig(String topic)
   */
  @Test
  public void testWebSocketMqConfig() throws Exception {
    WebSocketMqConfig config = reader.webSocketMqConfig("test4");
    System.out.println(config.host);
    System.out.println(config.port);
  }

  /**
   * Method: queueMqConfig(String topic)
   */
  @Test
  public void testQueueMqConfig() throws Exception {
    QueueMqConfig config = reader.queueMqConfig("test2");
    System.out.println(config.keepLength);
    System.out.println(config.keepTime);
  }
}
