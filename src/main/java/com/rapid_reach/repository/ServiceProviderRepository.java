package com.rapid_reach.repository;

import com.rapid_reach.entity.ServiceProvider;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceProviderRepository extends JpaRepository<ServiceProvider, Long> {

    boolean existsByEmail(String email);

    Optional<ServiceProvider> findByEmailAndPassword(String email, String password);

    List<ServiceProvider> findByServiceTypeIgnoreCaseAndCityIgnoreCase(String serviceType, String city);
}
