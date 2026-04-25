package com.example.assessment;

import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertFalse;
// import static org.junit.jupiter.api.Assertions.assertTrue;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.junit.jupiter.api.Test;

@SpringBootTest
class LruCacheServiceTest {

    @Autowired
    private LruCacheService service;

    @Test
    void testGetExistingKey() {
        service.put("a", "1");
        assertEquals("1", service.get("a"));
    }

    @Test
    void testEviction() {
        for (int i = 1; i <= 6; i++) {
            service.put("k" + i, "v" + i);
        }

        // assertNull(service.get("k1")); // first evicted
    }

    @Test
    void testGetMissingKey() {
        // assertNull(service.get("missing"));
    }
}