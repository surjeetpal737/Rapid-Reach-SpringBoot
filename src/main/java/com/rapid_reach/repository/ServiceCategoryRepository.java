package com.rapid_reach.repository;

import com.rapid_reach.entity.ServiceCategory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceCategoryRepository extends JpaRepository<ServiceCategory, Long> {

    boolean existsByNameIgnoreCase(String name);

    List<ServiceCategory> findByActiveTrueOrderByNameAsc();
}
