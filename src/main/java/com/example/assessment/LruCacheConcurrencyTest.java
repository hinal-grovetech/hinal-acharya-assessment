package com.example.assessment;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@SpringBootTest
public class LruCacheConcurrencyTest {

    @Autowired
    private LruCacheService service;

    private static final int THREADS = 20;

    @Test
    void testConcurrentPutAndGet() throws InterruptedException {

        for (int run = 1; run <= 3; run++) {

            service.clear();

            ExecutorService executor = Executors.newFixedThreadPool(THREADS);

            CountDownLatch startLatch = new CountDownLatch(1);
            CountDownLatch doneLatch = new CountDownLatch(THREADS);

            for (int i = 0; i < THREADS; i++) {
                final int index = i;

                executor.submit(() -> {
                    try {
                        startLatch.await(); 
                        service.put("key" + index, "value" + index);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        doneLatch.countDown();
                    }
                });
            }

            // start all threads at once
            startLatch.countDown();

            // wait for all PUTs to finish
            doneLatch.await();
            executor.shutdown();
        }
    }
}
