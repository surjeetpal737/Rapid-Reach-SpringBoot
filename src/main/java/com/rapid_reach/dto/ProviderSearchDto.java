package com.rapid_reach.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ProviderSearchDto {

    @NotBlank
    @Size(max = 100)
    private String serviceType;

    @NotBlank
    @Size(max = 100)
    private String city;

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
