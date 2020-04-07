package mq.local;

import mq.Topic;
import mq.config.QueueMqConfig;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class LocalQueueTest {

  private LocalQueue<String, String> queue;

  @Before
  public void before() throws Exception {
    Topic<String, String> topic = Topic.getOrCreate("test", String.class, String.class);
    QueueMqConfig config = new QueueMqConfig(100, 5);

    queue = new LocalQueue<>(topic, config);
  }

  @After
  public void after() throws Exception {}

  /**
   * Method: offer(K k, V v)
   */
  @Test
  public void testOffer() throws Exception {
    IntStream.range(1, 1000).forEach(i -> {
      queue.offer("key" + i, "val" + i);
    });

    System.out.println(queue.poll().getLeft());

    List<Pair<String, String>> pairs = queue.pollAll();
    pairs.forEach(pair -> {
      System.out.println(pair.getLeft() + " " + pair.getRight());
    });
    System.out.println("Before sleep, poll list size: " + pairs.size());

    TimeUnit.SECONDS.sleep(6);

    List<Pair<String, String>> pairs1 = queue.pollAll();
    System.out.println("After sleep, poll list size: " + pairs1.size());
    pairs1.forEach(pair -> {
      System.out.println(pair.getLeft() + " " + pair.getRight());
    });

  }

  /**
   * Method: poll()
   */
  @Test
  public void testPoll() throws Exception {
    // TODO: Test goes here...
  }

  /**
   * Method: pollAll()
   */
  @Test
  public void testPollAll() throws Exception {
    // TODO: Test goes here...
  }


  /**
   * Method: isOutOfDate()
   */
  @Test
  public void testIsOutOfDate() throws Exception {
    // TODO: Test goes here...
    /*
     * try { Method method = LocalQueue.getClass().getMethod("isOutOfDate");
     * method.setAccessible(true); method.invoke(<Object>, <Parameters>); }
     * catch(NoSuchMethodException e) { } catch(IllegalAccessException e) { }
     * catch(InvocationTargetException e) { }
     */
  }

  /**
   * Method: isOverflow()
   */
  @Test
  public void testIsOverflow() throws Exception {
    // TODO: Test goes here...
    /*
     * try { Method method = LocalQueue.getClass().getMethod("isOverflow");
     * method.setAccessible(true); method.invoke(<Object>, <Parameters>); }
     * catch(NoSuchMethodException e) { } catch(IllegalAccessException e) { }
     * catch(InvocationTargetException e) { }
     */
  }

  /**
   * Method: discardElement()
   */
  @Test
  public void testDiscardElement() throws Exception {
    // TODO: Test goes here...
    /*
     * try { Method method = LocalQueue.getClass().getMethod("discardElement");
     * method.setAccessible(true); method.invoke(<Object>, <Parameters>); }
     * catch(NoSuchMethodException e) { } catch(IllegalAccessException e) { }
     * catch(InvocationTargetException e) { }
     */
  }

}
