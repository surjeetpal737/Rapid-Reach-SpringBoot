package com.rapid_reach.repository;

import com.rapid_reach.entity.Booking;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByProviderIdOrderByBookingDateDescIdDesc(Long providerId);

    List<Booking> findByUserIdOrderByBookingDateDescIdDesc(Long userId);

    long countByStatus(String status);
}
