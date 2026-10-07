package com.learnlab;

import com.learnlab.config.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
public class LearnlabApplication {

  public static void main(String[] args) {
    SpringApplication.run(LearnlabApplication.class, args);
  }
}
