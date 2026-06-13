package com.rapid_reach.repository;

import com.rapid_reach.entity.Feedback;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    Optional<Feedback> findByBookingId(Long bookingId);

    List<Feedback> findByProviderIdOrderByCreatedAtDesc(Long providerId);

    @Query("select avg(f.rating) from Feedback f where f.providerId = :providerId")
    Double averageRatingForProvider(Long providerId);
}
