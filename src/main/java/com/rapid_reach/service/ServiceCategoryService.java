package com.rapid_reach.service;

import com.rapid_reach.entity.ServiceCategory;
import com.rapid_reach.repository.ServiceCategoryRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServiceCategoryService {

    private final ServiceCategoryRepository serviceCategoryRepository;

    public ServiceCategoryService(ServiceCategoryRepository serviceCategoryRepository) {
        this.serviceCategoryRepository = serviceCategoryRepository;
    }

    @Transactional(readOnly = true)
    public List<ServiceCategory> activeServices() {
        return serviceCategoryRepository.findByActiveTrueOrderByNameAsc();
    }

    @Transactional
    public void ensureDefaults() {
        addIfMissing("Plumber", "Home plumbing repair and fitting work");
        addIfMissing("Electrician", "Electrical repair and installation");
        addIfMissing("Carpenter", "Woodwork and furniture repair");
        addIfMissing("Appliance Repair", "Basic home appliance servicing");
        addIfMissing("Cleaning", "Home and office cleaning services");
    }

    private void addIfMissing(String name, String description) {
        if (serviceCategoryRepository.existsByNameIgnoreCase(name)) {
            return;
        }
        ServiceCategory service = new ServiceCategory();
        service.setName(name);
        service.setDescription(description);
        serviceCategoryRepository.save(service);
    }
}
