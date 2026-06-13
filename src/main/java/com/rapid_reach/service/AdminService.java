package com.rapid_reach.service;

import com.rapid_reach.dto.AdminLoginDto;
import com.rapid_reach.entity.Admin;
import com.rapid_reach.repository.AdminRepository;
import com.rapid_reach.security.PasswordUtil;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminService {

    private final AdminRepository adminRepository;

    public AdminService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    @Transactional(readOnly = true)
    public Optional<Admin> login(AdminLoginDto dto) {
        return adminRepository.findByEmail(dto.getEmail())
                .filter(admin -> PasswordUtil.matches(dto.getPassword(), admin.getPassword()));
    }

    @Transactional
    public void ensureDefaultAdmin() {
        if (adminRepository.findByEmail("admin@rapidreach.com").isPresent()) {
            return;
        }
        Admin admin = new Admin();
        admin.setName("Rapid Reach Admin");
        admin.setEmail("admin@rapidreach.com");
        admin.setPassword(PasswordUtil.hash("admin123"));
        adminRepository.save(admin);
    }
}
