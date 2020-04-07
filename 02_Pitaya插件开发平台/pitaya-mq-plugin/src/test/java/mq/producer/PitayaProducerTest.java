package mq.producer;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class PitayaProducerTest {
  private PitayaProducer<String, String> producer;

  @Inject
  public PitayaProducerTest(PitayaProducerFactory producerFactory) {
    producer = producerFactory.getOrCreateProducer("test", String.class, String.class);
  }

  @PostConstruct
  public void before() throws Exception {
    ScheduledExecutorService executorService = Executors.newScheduledThreadPool(100);
    for (int i = 0; i < 100; i++) {
      executorService.scheduleAtFixedRate(this::testSend, 15, 1, TimeUnit.SECONDS);
    }
  }

  public void testSend() {
    System.out.println("sending...." + LocalDateTime.now().toString());
    producer.send("test key", LocalDateTime.now().toString());
    producer.send("test key", LocalDateTime.now().toString());
    producer.send("test key", LocalDateTime.now().toString());
    producer.send("test key", LocalDateTime.now().toString());
    producer.send("test key", LocalDateTime.now().toString());
  }
}
