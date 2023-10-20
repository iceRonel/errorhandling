package com.batch.errorhandling;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);



	}

	@Bean
	public NewTopic titlesTopic(){
		return TopicBuilder
				.name("titles.csv")
				.partitions(3)
				.replicas(1)
				.build();
	}

}
