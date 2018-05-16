package com.helabs.veloe.aws.sqs.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.helabs.veloe.aws.sqs.service.SqsProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.logging.Logger;

@Component
public class SqsConsumerListener {

    private final Logger logger = Logger.getLogger(SqsConsumerListener.class.getName());

    @Autowired
    private SqsProducerService sqsProducerService;

    @JmsListener(destination = "${sqs.consumer.queue.name}")
    public void messageConsumer(@Headers Map<String, Object> attributes,
                                @Payload String message) {

        //TODO: acknowledgement manual pós-sucesso das operações realizadas pelo micro-serviço

        logger.info("Atributos: " + attributes);
        logger.info("Mensagem: " + message);

        responder(attributes, message);
    }

    private void responder(Map<String, Object> attributes, String message) {
        ImmutableMap<String, Object> resposta = ImmutableMap.of(
                "data-processamento", new Date(),
                "mensagem", message,
                "atributos", attributes
        );

        try {
            String json = new ObjectMapper().writeValueAsString(resposta);
            logger.info("Resposta: " + json);
            sqsProducerService.sentToQueue(json);
        } catch (JsonProcessingException e) {
            logger.severe(e.getMessage());
        }

    }

}
