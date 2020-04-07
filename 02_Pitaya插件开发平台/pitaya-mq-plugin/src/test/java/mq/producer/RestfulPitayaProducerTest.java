package mq.producer;

import mq.MqMsg;
import mq.Topic;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.client.RestTemplate;

public class RestfulPitayaProducerTest {

  @Before
  public void before() throws Exception {}

  @After
  public void after() throws Exception {}

  /**
   * Method: send(K k, V v)
   */
  @Test
  public void testSend() throws Exception {
    // Topic<String, DataPoint> topic = Topic.getOrCreate("test", String.class, DataPoint.class);
    // String key = "r.abc";
    // DataPoint val = new DataPoint(key, LocalDateTime.now(), "1.414", 192);
    // MqMsg<String, DataPoint> msg = new MqMsg<>(topic, key, val);

    RestTemplate restTemplate = new RestTemplate();
    restTemplate.put("http://localhost:8088/mq/producer",
        new MqMsg<>(Topic.getOrCreate("test", String.class, String.class), "test", "test"));
    System.out.println("success");
  }


}
