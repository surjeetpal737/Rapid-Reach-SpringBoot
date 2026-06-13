package com.rapid_reach.service;

import java.time.LocalDate;
import java.util.List;

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

    public BookingService(BookingRepository bookingRepository, ProviderService providerService) {
        this.bookingRepository = bookingRepository;
        this.providerService = providerService;
    }

    @Transactional
    public Booking createBooking(Customer customer, Long providerId) {
        ServiceProvider provider = providerService.getById(providerId);

        Booking booking = new Booking();
        booking.setUserName(customer.getName());
        booking.setProviderId(provider.getId());
        booking.setProviderName(provider.getName());
        booking.setProviderPhone(provider.getPhone());
        booking.setServiceType(provider.getServiceType());
        booking.setCity(provider.getCity());
        booking.setBookingDate(LocalDate.now());
        booking.setStatus("Pending");
        return bookingRepository.save(booking);
    }

    @Transactional(readOnly = true)
    public List<Booking> findForProvider(Long providerId) {
        return bookingRepository.findByProviderIdOrderByBookingDateDescIdDesc(providerId);
    }

    @Transactional
    public void updateStatus(Long bookingId, String status) {
        if (!status.equals("Accepted") && !status.equals("Rejected") && !status.equals("Pending")) {
            throw new IllegalArgumentException("Unsupported booking status: " + status);
        }

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found."));
        booking.setStatus(status);
        bookingRepository.save(booking);
    }
}
