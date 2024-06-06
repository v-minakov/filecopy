package com.vminakov.javafxfilecopy.app.configuration;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@Slf4j(topic = "THREADING")
public class ThreadingConfig {
    
    @Bean("BackgroundOperationsThreadPool")
    public ExecutorService executorService() {
        
        try (ExecutorService executorService = Executors.newCachedThreadPool(new BasicThreadFactory.Builder()
                .namingPattern("BgOpsThread-%d")
                .daemon(true)
                .priority(5)
                .uncaughtExceptionHandler((thread, throwable) -> log.error("Uncaught exception", throwable))
                .build())) {
            
            // Here we can configure another pool options like core pool size etc.
            
            return executorService;
        }
    }
}
