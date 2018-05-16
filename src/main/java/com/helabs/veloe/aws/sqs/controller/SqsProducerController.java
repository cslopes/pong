package com.helabs.veloe.aws.sqs.controller;

import com.helabs.veloe.aws.sqs.service.SqsProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/messages")
public class SqsProducerController {

    @Autowired
    private SqsProducerService sqsProducerService;

    @RequestMapping(value = "/send/{message}", method = RequestMethod.GET)
    public void send(@PathVariable @RequestBody String message) {
        sqsProducerService.sentToQueue(message);
    }

    @RequestMapping(value="/send", method = RequestMethod.POST)
    public void post(@RequestBody String message) {
        sqsProducerService.sentToQueue(message);
    }

    @RequestMapping(value="/send/batch", method = RequestMethod.POST)
    public void batchSend(@RequestBody List<String> messages) {
        sqsProducerService.sentToQueue(messages);
    }

    @RequestMapping(value="/send/with-meta", method = RequestMethod.POST)
    public void sendWithMeta(@RequestBody String message) {
        sqsProducerService.sentToQueueWithMeta(message);
    }
}
