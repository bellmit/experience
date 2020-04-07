package mq.consumer;

import org.springframework.stereotype.Component;

@Component
public class Test2PitayaConsumer extends PitayaConsumer<String, String> {
  protected Test2PitayaConsumer() {
    super("test", String.class, String.class);
  }

  @Override
  public void receive(String key, String val) {
    System.out.println(String.format("test2222222 receive: key: %s, val: %s", key, val));
  }
}
