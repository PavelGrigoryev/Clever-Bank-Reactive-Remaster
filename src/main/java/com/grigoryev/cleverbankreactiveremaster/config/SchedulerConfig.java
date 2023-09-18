package com.grigoryev.cleverbankreactiveremaster.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@ConditionalOnProperty(name = "schedulers.enabled", matchIfMissing = true)
public class SchedulerConfig {
}
