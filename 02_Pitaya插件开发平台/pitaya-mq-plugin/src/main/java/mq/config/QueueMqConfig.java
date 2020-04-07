package mq.config;

public class QueueMqConfig {
  public final int keepLength;
  public final int keepTime;

  public QueueMqConfig(int keepLength, int keepTime) {
    this.keepLength = keepLength;
    this.keepTime = keepTime;
  }
}
