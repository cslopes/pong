package com.helabs.veloe.aws.sqs.service;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.MessageAttributeValue;
import com.amazonaws.services.sqs.model.SendMessageBatchRequest;
import com.amazonaws.services.sqs.model.SendMessageBatchRequestEntry;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class SqsProducerService {

    @Value("${sqs.producer.queue.name}")
    private String queueName;


    @Value("${sqs.producer.batch.max:10}")
    private int maxBatchSend;

    @Autowired
    private AmazonSQS amazonSQS;

    public void sentToQueue(String message) {
        final SendMessageRequest sendMessageRequest = new SendMessageRequest()
                .withQueueUrl(queueName)
                .withMessageBody(message);

        amazonSQS.sendMessage(sendMessageRequest);
    }

    public void sentToQueue(List<String> messages) {
        Lists.partition(messages, maxBatchSend)
                .forEach(strings -> {
                    final AtomicInteger index = new AtomicInteger();
                    final List<SendMessageBatchRequestEntry> entries = strings.stream()
                            .map(message -> {
                                final String messageId = String.valueOf(index.getAndIncrement());
                                return new SendMessageBatchRequestEntry(messageId, message);
                            })
                            .collect(Collectors.toList());

                    final SendMessageBatchRequest sendMessageRequest = new SendMessageBatchRequest()
                            .withQueueUrl(queueName)
                            .withEntries(entries);

                    amazonSQS.sendMessageBatch(sendMessageRequest);
                });
    }

    public void sentToQueue(String message, Map<String, MessageAttributeValue> attributes) {
        final SendMessageRequest sendMessageRequest = new SendMessageRequest()
                .withQueueUrl(queueName)
                .withMessageAttributes(attributes)
                .withMessageBody(message);

        amazonSQS.sendMessage(sendMessageRequest);
    }

    public void sentToQueueWithMeta(String message) {
        final Map<String, MessageAttributeValue> attributes = new HashMap<>();
        attributes.put("id",
                new MessageAttributeValue()
                        .withDataType("String")
                        .withStringValue(UUID.randomUUID().toString()));

        attributes.put("date",
                new MessageAttributeValue()
                        .withDataType("String")
                        .withStringValue(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))));

        sentToQueue(message, attributes);
    }


}
