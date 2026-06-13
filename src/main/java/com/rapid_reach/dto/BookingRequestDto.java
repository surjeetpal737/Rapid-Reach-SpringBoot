package com.rapid_reach.dto;

import jakarta.validation.constraints.NotNull;

public class BookingRequestDto {

    @NotNull
    private Long providerId;

    public Long getProviderId() {
        return providerId;
    }

    public void setProviderId(Long providerId) {
        this.providerId = providerId;
    }
}
