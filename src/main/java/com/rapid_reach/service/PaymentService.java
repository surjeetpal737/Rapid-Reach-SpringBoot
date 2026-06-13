package com.rapid_reach.service;

import com.rapid_reach.dto.PaymentDto;
import com.rapid_reach.entity.Payment;
import com.rapid_reach.repository.PaymentRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public Payment saveDemoPayment(PaymentDto dto) {
        Payment payment = paymentRepository.findByBookingId(dto.getBookingId()).orElseGet(Payment::new);
        payment.setBookingId(dto.getBookingId());
        payment.setAmount(dto.getAmount());
        payment.setMode(dto.getMode());
        payment.setStatus(dto.getMode().equals("Cash on Service") ? "Pending Cash Collection" : "Paid Demo");
        payment.setTransactionRef(dto.getMode().equals("Online Payment Demo") ? "DEMO-" + UUID.randomUUID().toString().substring(0, 8) : null);
        payment.setCreatedAt(LocalDateTime.now());
        return paymentRepository.save(payment);
    }

    @Transactional(readOnly = true)
    public Optional<Payment> findByBookingId(Long bookingId) {
        return paymentRepository.findByBookingId(bookingId);
    }
}
