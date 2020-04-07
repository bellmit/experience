package mq;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TopicTest {

  @Before
  public void before() throws Exception {}

  @After
  public void after() throws Exception {

  }

  /**
   * Method: getOrCreate(@NotNull String name, @NotNull Class<K> keyClass, @NotNull Class<V>
   * valClass)
   */
  @Test
  public void testGetOrCreateForNameKeyClassValClass() throws Exception {
    Topic<String, String> topic = Topic.getOrCreate("test", String.class, String.class);
    Topic<String, String> topic1 = Topic.getOrCreate("test", String.class, String.class);
    Assert.assertTrue(topic == topic1);

    Topic<String, String> topic2 =
        Topic.getOrCreate("test", "java.lang.String", "java.lang.String");
    Assert.assertTrue(topic == topic2);
  }

  /**
   * Method: encode(Topic<K, V> topic)
   */
  @Test
  public void testEncode() throws Exception {
    Topic<String, String> topic = Topic.getOrCreate("test", String.class, String.class);
    String json = Topic.encode(topic);
    Topic<String, String> topic1 = Topic.decode(json);
    Assert.assertTrue(topic == topic1);
  }


}
