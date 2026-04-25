package com.example.assessment;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CacheRepository extends JpaRepository<CacheEntry, String> {
      List<CacheEntry> findAllByOrderByLastAccessTimeAsc();
}

/*using H2 database due to some system restriction */
