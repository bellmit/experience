package mq;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MqMsgTest {

  @Before
  public void before() throws Exception {}

  @After
  public void after() throws Exception {}

  /**
   * 
   * Method: encode(MqMsg<K, V> msg)
   * 
   */
  @Test
  public void testEncode() throws Exception {

    // Topic<String, DataPoint> topic = Topic.getOrCreate("test", String.class, DataPoint.class);
    // String key = "r.abc";
    // DataPoint val = new DataPoint(key, LocalDateTime.now(), "1.414", 192);
    // MqMsg<String, DataPoint> msg = new MqMsg<>(topic, key, val);
    //
    // String json = MqMsg.encode(msg);
    // MqMsg msg1 = MqMsg.decode(json);
    //
    // Assert.assertEquals(msg, msg1);
  }

}
