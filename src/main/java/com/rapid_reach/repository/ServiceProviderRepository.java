package com.rapid_reach.repository;

import com.rapid_reach.entity.ServiceProvider;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceProviderRepository extends JpaRepository<ServiceProvider, Long> {

    boolean existsByEmail(String email);

    Optional<ServiceProvider> findByEmailAndPassword(String email, String password);

    List<ServiceProvider> findByServiceTypeIgnoreCaseAndCityIgnoreCase(String serviceType, String city);

    Optional<ServiceProvider> findByEmail(String email);

    List<ServiceProvider> findByServiceTypeIgnoreCaseAndCityIgnoreCaseAndStatusIgnoreCase(String serviceType, String city, String status);

    long countByStatusIgnoreCase(String status);
}
