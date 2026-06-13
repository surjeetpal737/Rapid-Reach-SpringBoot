package com.rapid_reach.service;

import com.rapid_reach.dto.FeedbackDto;
import com.rapid_reach.entity.Booking;
import com.rapid_reach.entity.Customer;
import com.rapid_reach.entity.Feedback;
import com.rapid_reach.repository.FeedbackRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final BookingService bookingService;

    public FeedbackService(FeedbackRepository feedbackRepository, BookingService bookingService) {
        this.feedbackRepository = feedbackRepository;
        this.bookingService = bookingService;
    }

    @Transactional
    public Feedback save(Customer customer, FeedbackDto dto) {
        Booking booking = bookingService.getById(dto.getBookingId());
        if (!booking.getUserId().equals(customer.getId())) {
            throw new IllegalArgumentException("You can review only your own booking.");
        }
        if (!"Completed".equals(booking.getStatus())) {
            throw new IllegalArgumentException("Feedback is allowed only after booking completion.");
        }
        Feedback feedback = feedbackRepository.findByBookingId(dto.getBookingId()).orElseGet(Feedback::new);
        feedback.setBookingId(booking.getId());
        feedback.setUserId(customer.getId());
        feedback.setProviderId(booking.getProviderId());
        feedback.setRating(dto.getRating());
        feedback.setComments(dto.getComments());
        feedback.setCreatedAt(LocalDateTime.now());
        return feedbackRepository.save(feedback);
    }

    @Transactional(readOnly = true)
    public Optional<Feedback> findByBookingId(Long bookingId) {
        return feedbackRepository.findByBookingId(bookingId);
    }

    @Transactional(readOnly = true)
    public List<Feedback> findForProvider(Long providerId) {
        return feedbackRepository.findByProviderIdOrderByCreatedAtDesc(providerId);
    }

    @Transactional(readOnly = true)
    public double averageForProvider(Long providerId) {
        Double average = feedbackRepository.averageRatingForProvider(providerId);
        return average == null ? 0.0 : average;
    }
}
