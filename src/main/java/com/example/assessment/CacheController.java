package com.example.assessment;

import java.util.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cache")
public class CacheController {

    private final LruCacheService service;

    public CacheController(LruCacheService service) {
        this.service = service;
    }

    @GetMapping("/{key}")
    public ResponseEntity<ApiResponse<String>> get(@PathVariable String key) {
        String value = service.get(key);

        if (value == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, null, "Key not found"));
        }

        return ResponseEntity.ok(new ApiResponse<>(true, value, "Record found"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> put(@RequestBody Map<String, String> body) {

        String key = body.get("key");
        String value = body.get("value");

        if (key == null || value == null) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, null, "Invalid request"));
        }

        service.put(key, value);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, null, "Record Created."));
    }

    @DeleteMapping("/{key}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String key) {
        service.delete(key);
        return ResponseEntity.ok(new ApiResponse<>(true, null, null));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> clear() {
        service.clear();
        return ResponseEntity.ok(new ApiResponse<>(true, null, null));
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<CacheEntry>>> getAll() {

        List<CacheEntry> list = service.getAll();

        return ResponseEntity.ok(new ApiResponse<>(true, list, null));
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStats() {
        return ResponseEntity.ok(
                new ApiResponse<>(true, service.getStats(), null));
    }
}
