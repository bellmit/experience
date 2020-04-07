package mq.config;

import java.util.Properties;

public class KafkaMqConfig {
  public final int period;
  public final Properties kafkaProperties;

  KafkaMqConfig(int period, Properties kafkaProperties) {
    this.period = period;
    this.kafkaProperties = kafkaProperties;
  }
}
