package com.helabs.veloe.aws.sqs.service;

import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazon.sqs.javamessaging.ProviderConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.Session;

@Configuration
public class SqsConfiguration {

    @Value("${cloud.aws.region.static}")
    private String region;

    @Bean
    public AmazonSQS createSQSClient() {
        return AmazonSQSClient.builder()
                .withCredentials(new ProfileCredentialsProvider())
                .withRegion(Regions.fromName(region))
                .build();
    }

    @Bean
    public SQSConnectionFactory createSQSConnectionFactory(AmazonSQS sqs) {
        return new SQSConnectionFactory(new ProviderConfiguration(), sqs);
    }

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(SQSConnectionFactory connectionFactory) {
        final DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        return factory;
    }

    @Bean
    public JmsTemplate defaultJmsTemplate(SQSConnectionFactory connectionFactory) {
        return new JmsTemplate(connectionFactory);
    }

}
