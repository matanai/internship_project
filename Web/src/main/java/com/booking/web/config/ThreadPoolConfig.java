package com.booking.web.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Slf4j
@Configuration
public class ThreadPoolConfig
{
    @Value("${threadPool.corePoolSize}")
    private int corePoolSize;

    @Value("${threadPool.maxPoolSize}")
    private int maxPoolSize;

    @Value("${threadPool.queueCapacity}")
    private int queueCapacity;

    @Bean
    public TaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.afterPropertiesSet();
        executor.initialize();
        log.info("{}", String.format("Executor running: corePoolSize - %d, maxPoolSize - %d, poolSize - %d, activeCount - %d",
                executor.getCorePoolSize(), executor.getMaxPoolSize(), executor.getPoolSize(), executor.getActiveCount()));
        return executor;
    }
}
