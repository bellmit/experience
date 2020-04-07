package mq.controllers;

import mq.MqMsg;
import mq.local.LocalQueue;
import mq.local.LocalQueueFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

@RestController
@RequestMapping("mq")
public class ProducerController {
  private LocalQueueFactory localQueueFactory;

  @Inject
  public ProducerController(LocalQueueFactory localQueueFactory) {
    this.localQueueFactory = localQueueFactory;
  }

  @RequestMapping(value = "/producer", method = RequestMethod.PUT)
  public void producer(@RequestBody MqMsg msg) {
    msg.decode();
    LocalQueue localQueue = localQueueFactory.getOrCreateLocalQueue(msg.topic());
    localQueue.offer(msg.getKey(), msg.getVal());
  }
}
