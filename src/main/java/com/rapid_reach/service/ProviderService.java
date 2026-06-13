package com.rapid_reach.service;

import java.util.List;
import java.util.Optional;

import com.rapid_reach.dto.ProviderLoginDto;
import com.rapid_reach.dto.ProviderRegistrationDto;
import com.rapid_reach.dto.ProviderSearchDto;
import com.rapid_reach.entity.ServiceProvider;
import com.rapid_reach.exception.DuplicateEmailException;
import com.rapid_reach.exception.ResourceNotFoundException;
import com.rapid_reach.repository.ServiceProviderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProviderService {

    private final ServiceProviderRepository providerRepository;

    public ProviderService(ServiceProviderRepository providerRepository) {
        this.providerRepository = providerRepository;
    }

    @Transactional
    public ServiceProvider register(ProviderRegistrationDto dto) {
        if (providerRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateEmailException("A provider with this email already exists.");
        }

        ServiceProvider provider = new ServiceProvider();
        provider.setName(dto.getName());
        provider.setServiceType(dto.getServiceType());
        provider.setPhone(dto.getPhone());
        provider.setEmail(dto.getEmail());
        provider.setCity(dto.getCity());
        provider.setPassword(dto.getPassword());
        provider.setStatus("pending");
        provider.setAge(dto.getAge());
        provider.setGender(dto.getGender());
        provider.setPastexperience(dto.getPastexperience());
        provider.setHighestQualification(dto.getHighestQualification());
        provider.setExperties(dto.getExperties());
        return providerRepository.save(provider);
    }

    @Transactional(readOnly = true)
    public Optional<ServiceProvider> login(ProviderLoginDto dto) {
        return providerRepository.findByEmailAndPassword(dto.getEmail(), dto.getPassword());
    }

    @Transactional(readOnly = true)
    public List<ServiceProvider> search(ProviderSearchDto dto) {
        return providerRepository.findByServiceTypeIgnoreCaseAndCityIgnoreCase(dto.getServiceType(), dto.getCity());
    }

    @Transactional(readOnly = true)
    public ServiceProvider getById(Long id) {
        return providerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service provider not found."));
    }
}
