package mq.consumer;

import org.springframework.stereotype.Component;

@Component
public class Test1PitayaConsumer extends PitayaConsumer<String, String> {
  protected Test1PitayaConsumer() {
    super("test", String.class, String.class);
  }

  @Override
  public void receive(String key, String val) {
    System.out.println(String.format("test1111111111 receive: key: %s, val: %s", key, val));
  }
}
