package com.helabs.veloe.aws.sqs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SqsApplication {

    public static void main(String[] args) {
        SpringApplication.run(SqsApplication.class, args);
    }

}
