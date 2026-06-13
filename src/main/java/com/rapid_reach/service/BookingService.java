package com.rapid_reach.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import com.rapid_reach.dto.BookingRequestDto;
import com.rapid_reach.entity.Booking;
import com.rapid_reach.entity.Customer;
import com.rapid_reach.entity.ServiceProvider;
import com.rapid_reach.exception.ResourceNotFoundException;
import com.rapid_reach.repository.BookingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final ProviderService providerService;
    private static final Random RANDOM = new Random();

    public BookingService(BookingRepository bookingRepository, ProviderService providerService) {
        this.bookingRepository = bookingRepository;
        this.providerService = providerService;
    }

    @Transactional
    public Booking createBooking(Customer customer, BookingRequestDto dto) {
        ServiceProvider provider = providerService.getById(dto.getProviderId());

        Booking booking = new Booking();
        booking.setUserId(customer.getId());
        booking.setUserName(customer.getName());
        booking.setProviderId(provider.getId());
        booking.setProviderName(provider.getName());
        booking.setProviderPhone(provider.getPhone());
        booking.setServiceType(dto.getServiceType());
        booking.setCity(dto.getCity());
        booking.setArea(dto.getArea());
        booking.setBookingDate(dto.getPreferredDate() == null ? LocalDate.now() : dto.getPreferredDate());
        booking.setPreferredTime(dto.getPreferredTime());
        booking.setProblemDescription(dto.getProblemDescription());
        booking.setStatus("Pending");
        booking.setCompletionOtp(String.valueOf(100000 + RANDOM.nextInt(900000)));
        booking.setCreatedAt(LocalDateTime.now());
        return bookingRepository.save(booking);
    }

    @Transactional(readOnly = true)
    public List<Booking> findForProvider(Long providerId) {
        return bookingRepository.findByProviderIdOrderByBookingDateDescIdDesc(providerId);
    }

    @Transactional(readOnly = true)
    public List<Booking> findForCustomer(Long userId) {
        return bookingRepository.findByUserIdOrderByBookingDateDescIdDesc(userId);
    }

    @Transactional(readOnly = true)
    public List<Booking> findAll() {
        return bookingRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Booking getById(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found."));
    }

    @Transactional
    public void updateStatus(Long bookingId, String status) {
        if (!status.equals("Accepted") && !status.equals("Cancelled") && !status.equals("Pending")
                && !status.equals("In Progress") && !status.equals("Completed")) {
            throw new IllegalArgumentException("Unsupported booking status: " + status);
        }

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found."));
        booking.setStatus(status);
        bookingRepository.save(booking);
    }

    @Transactional
    public void completeWithOtp(Long bookingId, String otp, Long providerId) {
        Booking booking = getById(bookingId);
        if (!booking.getProviderId().equals(providerId)) {
            throw new IllegalArgumentException("This booking does not belong to the logged in provider.");
        }
        if (!booking.getCompletionOtp().equals(otp)) {
            throw new IllegalArgumentException("Invalid OTP.");
        }
        booking.setStatus("Completed");
        bookingRepository.save(booking);
    }
}
