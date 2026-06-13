package com.rapid_reach.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class CompletionOtpDto {

    @NotNull
    private Long bookingId;

    @NotBlank
    @Pattern(regexp = "^[0-9]{6}$", message = "Enter the 6 digit OTP")
    private String otp;

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}
