package com.example.assessment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "cache_entries")
public class CacheEntry {

    @Id
    @Column(name = "cache_key")
    private String key;

    @Column(name = "cache_value")
    private String value;

    @Column(name = "last_access_time")
    private long lastAccessTime;

    public CacheEntry() {
    }

    public CacheEntry(String key, String value, long lastAccessTime) {
        this.key = key;
        this.value = value;
        this.lastAccessTime = lastAccessTime;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public long getLastAccessTime() {
        return lastAccessTime;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setLastAccessTime(long lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
    }
}
