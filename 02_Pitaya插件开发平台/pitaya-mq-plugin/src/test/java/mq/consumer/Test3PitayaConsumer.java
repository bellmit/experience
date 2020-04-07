package mq.consumer;

import org.springframework.stereotype.Component;

@Component
public class Test3PitayaConsumer extends PitayaConsumer<String, String> {
  protected Test3PitayaConsumer() {
    super("test", String.class, String.class);
  }

  @Override
  public void receive(String key, String val) {
    System.out.println(String.format("test33333333 receive: key: %s, val: %s", key, val));
  }
}
