package com.rapid_reach.repository;

import com.rapid_reach.entity.ServiceProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ServiceProviderRepository extends JpaRepository<ServiceProvider, Long> {

    boolean existsByEmail(String email);

    Optional<ServiceProvider> findByEmail(String email);

    List<ServiceProvider> findByServiceTypeIgnoreCaseAndCityIgnoreCase(
            String serviceType, String city);

    List<ServiceProvider> findByServiceTypeIgnoreCaseAndCityIgnoreCaseAndStatusIgnoreCase(
            String serviceType, String city, String status);

    List<ServiceProvider> findByStatusIgnoreCase(String status);

    long countByStatusIgnoreCase(String status);
}
