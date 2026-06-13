package com.rapid_reach.service;

import com.rapid_reach.dto.CustomerLoginDto;
import com.rapid_reach.dto.CustomerRegistrationDto;
import com.rapid_reach.entity.Customer;
import com.rapid_reach.exception.DuplicateEmailException;
import com.rapid_reach.repository.CustomerRepository;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Transactional
    public Customer register(CustomerRegistrationDto dto) {
        if (customerRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateEmailException("A customer with this email already exists.");
        }

        Customer customer = new Customer();
        customer.setName(dto.getName());
        customer.setEmail(dto.getEmail());
        customer.setPhone(dto.getPhone());
        customer.setPassword(dto.getPassword());
        return customerRepository.save(customer);
    }

    @Transactional(readOnly = true)
    public Optional<Customer> login(CustomerLoginDto dto) {
        return customerRepository.findByEmailAndPassword(dto.getEmail(), dto.getPassword());
    }
}
