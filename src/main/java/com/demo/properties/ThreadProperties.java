package com.demo.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "thread")
public class ThreadProperties {

    private int poolSizeMultiplier;
    private int maxPoolSize;
    private int queueCapacity;
    private int keepAlive;
}
