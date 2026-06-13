package com.rapid_reach.service;

import com.rapid_reach.dto.CustomerLoginDto;
import com.rapid_reach.dto.CustomerRegistrationDto;
import com.rapid_reach.dto.ForgotPasswordDto;
import com.rapid_reach.entity.Customer;
import com.rapid_reach.exception.DuplicateEmailException;
import com.rapid_reach.exception.ResourceNotFoundException;
import com.rapid_reach.repository.CustomerRepository;
import com.rapid_reach.security.PasswordUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    // ─── Registration ─────────────────────────────────────────────────────────

    @Transactional
    public Customer register(CustomerRegistrationDto dto) {
        if (customerRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateEmailException("A customer with this email already exists.");
        }

        Customer customer = new Customer();
        customer.setName(dto.getName());
        customer.setEmail(dto.getEmail());
        customer.setPhone(dto.getPhone());
        customer.setPassword(PasswordUtil.hash(dto.getPassword()));
        customer.setAddress(dto.getAddress());
        customer.setCity(dto.getCity());
        return customerRepository.save(customer);
    }

    // ─── Login ────────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public Optional<Customer> login(CustomerLoginDto dto) {
        return customerRepository.findByEmail(dto.getEmail())
                .filter(c -> PasswordUtil.matches(dto.getPassword(), c.getPassword()));
    }

    // ─── Profile update ───────────────────────────────────────────────────────

    @Transactional
    public Customer updateProfile(Long customerId, CustomerRegistrationDto dto) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found."));
        customer.setName(dto.getName());
        customer.setPhone(dto.getPhone());
        customer.setAddress(dto.getAddress());
        customer.setCity(dto.getCity());
        return customerRepository.save(customer);
    }

    // ─── Password reset ───────────────────────────────────────────────────────

    @Transactional
    public void resetPassword(ForgotPasswordDto dto) {
        Customer customer = customerRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No user found for this email."));
        customer.setPassword(PasswordUtil.hash(dto.getNewPassword()));
        customerRepository.save(customer);
    }

    // ─── Admin helpers ────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<Customer> findAll() {
        return customerRepository.findAll();
    }
}
