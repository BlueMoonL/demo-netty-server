package com.demo.thread.config;

import com.demo.properties.ThreadProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Configuration
@EnableConfigurationProperties(ThreadProperties.class)
@RequiredArgsConstructor
public class ExecutorConfig {

    private final ThreadProperties threadProperties;

    @Bean(name="taskExecutor")
    public Executor taskExecutor() {

        int coreCount = Runtime.getRuntime().availableProcessors();

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(Math.max(1, coreCount * threadProperties.getPoolSizeMultiplier()));
        executor.setMaxPoolSize(Math.max(executor.getCorePoolSize(), threadProperties.getMaxPoolSize()));
        executor.setQueueCapacity(Math.max(100, threadProperties.getQueueCapacity()));
        executor.setKeepAliveSeconds(Math.max(30, threadProperties.getKeepAliveSeconds()));
        executor.setThreadNamePrefix("task-exec-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardOldestPolicy());

        executor.initialize();
        log.info("ThreadPoolTaskExecutor initialized successfully.");
        return executor;
    }
}
