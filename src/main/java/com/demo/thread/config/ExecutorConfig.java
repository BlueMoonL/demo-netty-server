package com.demo.thread.config;

import com.demo.properties.ThreadProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@RequiredArgsConstructor
public class ExecutorConfig {

    private final ThreadProperties threadProperties;

    @Bean
    public Executor taskExecutor() {

        int coreCount = Runtime.getRuntime().availableProcessors();

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(coreCount * threadProperties.getPoolSizeMultiplier());
        executor.setMaxPoolSize(threadProperties.getMaxPoolSize());
        executor.setQueueCapacity(threadProperties.getQueueCapacity());
        executor.setKeepAliveSeconds(threadProperties.getKeepAlive());
        executor.setThreadNamePrefix("task-exec-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy()); // 과부하 시 호출한 스레드에서 실행
        executor.initialize();
        return executor;
    }
}
