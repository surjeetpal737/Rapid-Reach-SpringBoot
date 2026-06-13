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
import com.rapid_reach.security.PasswordUtil;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ProviderService {

    private final ServiceProviderRepository providerRepository;
    private static final Path PROOF_UPLOAD_DIR = Path.of("src", "main", "resources", "static", "uploads", "proofs");

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
        provider.setArea(dto.getArea());
        provider.setPassword(PasswordUtil.hash(dto.getPassword()));
        provider.setStatus("Pending");
        provider.setAge(dto.getAge());
        provider.setGender(dto.getGender());
        provider.setPastexperience(dto.getPastexperience());
        provider.setHighestQualification(dto.getHighestQualification());
        provider.setExperties(dto.getExperties());
        provider.setIdProofPath(saveProof(dto.getIdProof()));
        return providerRepository.save(provider);
    }

    @Transactional(readOnly = true)
    public Optional<ServiceProvider> login(ProviderLoginDto dto) {
        return providerRepository.findByEmail(dto.getEmail())
                .filter(provider -> PasswordUtil.matches(dto.getPassword(), provider.getPassword()));
    }

    @Transactional(readOnly = true)
    public List<ServiceProvider> search(ProviderSearchDto dto) {
        return providerRepository.findByServiceTypeIgnoreCaseAndCityIgnoreCaseAndStatusIgnoreCase(
                dto.getServiceType(), dto.getCity(), "Approved");
    }

    @Transactional(readOnly = true)
    public ServiceProvider getById(Long id) {
        return providerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service provider not found."));
    }

    @Transactional
    public void updateStatus(Long providerId, String status, String reason) {
        if (!status.equals("Approved") && !status.equals("Rejected") && !status.equals("Pending")) {
            throw new IllegalArgumentException("Unsupported provider status: " + status);
        }
        ServiceProvider provider = getById(providerId);
        provider.setStatus(status);
        provider.setRejectionReason(reason);
        providerRepository.save(provider);
    }

    private String saveProof(MultipartFile proof) {
        if (proof == null || proof.isEmpty()) {
            return null;
        }
        String contentType = proof.getContentType();
        if (contentType == null || (!contentType.equals("application/pdf") && !contentType.startsWith("image/"))) {
            throw new IllegalArgumentException("ID proof must be an image or PDF file.");
        }
        String originalName = proof.getOriginalFilename() == null ? "proof" : proof.getOriginalFilename();
        String extension = "";
        int dot = originalName.lastIndexOf('.');
        if (dot >= 0) {
            extension = originalName.substring(dot);
        }
        String fileName = UUID.randomUUID() + extension;
        try {
            Files.createDirectories(PROOF_UPLOAD_DIR);
            proof.transferTo(PROOF_UPLOAD_DIR.resolve(fileName));
            return "/uploads/proofs/" + fileName;
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to save ID proof.", ex);
        }
    }
}
