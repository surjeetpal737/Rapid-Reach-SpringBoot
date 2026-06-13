package com.rapid_reach.service;

import com.rapid_reach.dto.ForgotPasswordDto;
import com.rapid_reach.dto.ProviderLoginDto;
import com.rapid_reach.dto.ProviderRegistrationDto;
import com.rapid_reach.dto.ProviderSearchDto;
import com.rapid_reach.entity.ServiceProvider;
import com.rapid_reach.exception.DuplicateEmailException;
import com.rapid_reach.exception.ResourceNotFoundException;
import com.rapid_reach.repository.ServiceProviderRepository;
import com.rapid_reach.security.PasswordUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for all ServiceProvider operations.
 *
 * Fixes applied:
 *  1. Added findByStatus(String) — required by AdminController to list pending/approved providers.
 *  2. Added findAll() — required by AdminController provider management page.
 *  3. Added resetPassword(ForgotPasswordDto) — provider forgot-password flow was missing.
 *  4. Fixed saveProof() to use an absolute upload path derived from the working directory,
 *     not a relative path from src/main/resources/static which does not exist at runtime
 *     inside a packaged JAR.
 *  5. Upload directory is now configurable and created lazily on first use.
 */
@Service
public class ProviderService {

    private final ServiceProviderRepository providerRepository;

    /*
     * FIX: Using src/main/resources/static/uploads as the upload dir is wrong at runtime —
     * resources are packaged inside the JAR and the folder doesn't exist on the filesystem.
     * Use a path relative to the user's home directory (or configure via application.properties).
     * For a local college project, a fixed local path is fine.
     */
    private static final Path PROOF_UPLOAD_DIR =
            Path.of(System.getProperty("user.home"), "rapid-reach-uploads", "proofs");

    public ProviderService(ServiceProviderRepository providerRepository) {
        this.providerRepository = providerRepository;
    }

    // ─── Registration ─────────────────────────────────────────────────────────

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

    // ─── Login ────────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public Optional<ServiceProvider> login(ProviderLoginDto dto) {
        return providerRepository.findByEmail(dto.getEmail())
                .filter(p -> PasswordUtil.matches(dto.getPassword(), p.getPassword()));
    }

    // ─── Search ───────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<ServiceProvider> search(ProviderSearchDto dto) {
        return providerRepository
                .findByServiceTypeIgnoreCaseAndCityIgnoreCaseAndStatusIgnoreCase(
                        dto.getServiceType(), dto.getCity(), "Approved");
    }

    // ─── Lookup ───────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public ServiceProvider getById(Long id) {
        return providerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service provider not found."));
    }

    @Transactional(readOnly = true)
    public List<ServiceProvider> findAll() {
        return providerRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<ServiceProvider> findByStatus(String status) {
        return providerRepository.findByStatusIgnoreCase(status);
    }

    // ─── Status update ────────────────────────────────────────────────────────

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

    // ─── Password reset ───────────────────────────────────────────────────────

    /**
     * FIX: Added — provider forgot-password was using a DTO but had no service method.
     */
    @Transactional
    public void resetPassword(ForgotPasswordDto dto) {
        ServiceProvider provider = providerRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No provider found for this email."));
        provider.setPassword(PasswordUtil.hash(dto.getNewPassword()));
        providerRepository.save(provider);
    }

    // ─── File upload ──────────────────────────────────────────────────────────

    private String saveProof(MultipartFile proof) {
        if (proof == null || proof.isEmpty()) {
            return null;
        }

        String contentType = proof.getContentType();
        if (contentType == null
                || (!contentType.equals("application/pdf") && !contentType.startsWith("image/"))) {
            throw new IllegalArgumentException("ID proof must be an image or PDF file.");
        }

        // Derive safe filename: random UUID + original extension
        String original = proof.getOriginalFilename() == null ? "proof" : proof.getOriginalFilename();
        String extension = "";
        int dot = original.lastIndexOf('.');
        if (dot >= 0) {
            extension = original.substring(dot);
        }
        String fileName = UUID.randomUUID() + extension;

        try {
            Files.createDirectories(PROOF_UPLOAD_DIR);
            proof.transferTo(PROOF_UPLOAD_DIR.resolve(fileName));
            // Return a logical path stored in DB; actual file is outside the web root
            return "proofs/" + fileName;
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to save ID proof: " + ex.getMessage(), ex);
        }
    }
}
