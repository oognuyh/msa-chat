package com.oognuyh.friendservice.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreaker;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.sleuth.instrument.async.TraceableExecutorService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class TraceConfig {
    private final BeanFactory beanFactory;
    private final Resilience4JCircuitBreakerFactory circuitBreakerFactory;

    @Bean
    public ExecutorService executorService() {
        ExecutorService executorService = Executors.newCachedThreadPool();

        return new TraceableExecutorService(beanFactory, executorService);
    }

    @Bean
    public Resilience4JCircuitBreaker userServiceCircuitBreaker() {
        circuitBreakerFactory.configureExecutorService(executorService());

        return circuitBreakerFactory.create("user-service");
    }
}
