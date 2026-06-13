package com.rapid_reach;

import com.rapid_reach.service.AdminService;
import com.rapid_reach.service.ServiceCategoryService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final AdminService adminService;
    private final ServiceCategoryService serviceCategoryService;

    public DataInitializer(AdminService adminService, ServiceCategoryService serviceCategoryService) {
        this.adminService = adminService;
        this.serviceCategoryService = serviceCategoryService;
    }

    @Override
    public void run(String... args) {
        adminService.ensureDefaultAdmin();
        serviceCategoryService.ensureDefaults();
    }
}
