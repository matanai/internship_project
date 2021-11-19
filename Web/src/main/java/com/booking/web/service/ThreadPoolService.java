package com.booking.web.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Component
public final class ThreadPoolService
{
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    private ThreadPoolService() {}

    public void submitTask(Runnable task) {
        executorService.submit(task);
    }

    @PreDestroy
    private void closeThreadPool() {
        log.info("Thread pool service is shutting down");
        executorService.shutdownNow();
    }
}
