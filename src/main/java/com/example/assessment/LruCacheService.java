package com.example.assessment;

import java.util.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.concurrent.locks.ReentrantLock;

import jakarta.annotation.PostConstruct;

@Service
public class LruCacheService {

    private final CacheRepository repository;
    private final int capacity;

    private final Map<String, CacheEntry> cache = new HashMap<>();
    private long totalGets = 0;
    private long totalPuts = 0;
    private long totalEvictions = 0;

    private final ReentrantLock lock = new ReentrantLock();
    /*
     * Locking Strategy:single ReentrantLock
     * Reason:
     * In an LRU cache, get() is NOT a read-only operation because it updates the
     * access
     * order (lastAccessTime). This means both get() and put() used in one method.
     * ReentrantReadWriteLock is not appropriate here because it we can require
     * write lock for this get() method so it does not fullfill the requirement.
     */

    public LruCacheService(CacheRepository repository,
            @Value("${cache.capacity:5}") int capacity) {
        this.repository = repository;
        this.capacity = capacity;
    }

    @PostConstruct
    public void loadFromDb() {
        try {
            List<CacheEntry> entries = repository.findAll();

            entries.sort(Comparator.comparingLong(CacheEntry::getLastAccessTime));

            for (CacheEntry entry : entries) {
                cache.put(entry.getKey(), entry);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* get cache */
    public String get(String key) {
        lock.lock();
        try {

            CacheEntry entry = cache.get(key);

            if (entry == null)
                return null;

            entry.setLastAccessTime(System.currentTimeMillis());

            repository.save(entry);
            totalGets++;

            return entry.getValue();
        } finally {
            lock.unlock();
        }
    }

    /* get all the data */
    public List<CacheEntry> getAll() {
        lock.lock();
        try {

            List<CacheEntry> list = new ArrayList<>(cache.values());

            list.sort(new Comparator<CacheEntry>() {
                @Override
                public int compare(CacheEntry a, CacheEntry b) {
                    return Long.compare(b.getLastAccessTime(), a.getLastAccessTime());
                }
            });

            return list;
        } finally {
            lock.unlock();
        }
    }

    public void put(String key, String value) {

        lock.lock();
        try {
            if (cache.containsKey(key)) {
                CacheEntry existing = cache.get(key);
                existing.setValue(value);
                existing.setLastAccessTime(System.currentTimeMillis());
                repository.save(existing);
                return;
            }

            if (cache.size() >= capacity) {
                evictLru();
            }

            CacheEntry entry = new CacheEntry(
                    key,
                    value,
                    System.currentTimeMillis());

            cache.put(key, entry);
            repository.save(entry);
            totalPuts++;
        } finally {
            lock.unlock();
        }
    }

    private void evictLru() {
        CacheEntry lru = cache.values()
                .stream()
                .min(Comparator.comparingLong(CacheEntry::getLastAccessTime))
                .orElseThrow();

        cache.remove(lru.getKey());
        totalEvictions++;
        repository.deleteById(lru.getKey());
    }

    public void delete(String key) {

        lock.lock();
        try {
            cache.remove(key);
            repository.deleteById(key);
        } finally {
            lock.unlock();
        }
    }

    public void clear() {

        lock.lock();
        try {
            cache.clear();
            repository.deleteAll();
        } finally {
            lock.unlock();
        }
    }

    public Map<String, Object> getStats() {
        lock.lock();
        try {
            Map<String, Object> stats = new HashMap<>();
            stats.put("currentSize", cache.size());
            stats.put("capacity", capacity);
            stats.put("totalGets", totalGets);
            stats.put("totalPuts", totalPuts);
            stats.put("totalEvictions", totalEvictions);
            return stats;
        } finally {
            lock.unlock();
        }
    }
}
