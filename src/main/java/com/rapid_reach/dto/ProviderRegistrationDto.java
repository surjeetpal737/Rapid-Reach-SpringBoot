package com.rapid_reach.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ProviderRegistrationDto {

    @NotBlank
    @Size(max = 120)
    private String name;

    @NotBlank
    @Size(max = 100)
    private String serviceType;

    @NotBlank
    @Pattern(regexp = "^[0-9+\\- ]{7,20}$", message = "Enter a valid phone number")
    private String phone;

    @NotBlank
    @Email
    @Size(max = 160)
    private String email;

    @NotBlank
    @Size(max = 100)
    private String city;

    @NotBlank
    @Size(min = 4, max = 100)
    private String password;

    @NotNull
    @Min(18)
    @Max(100)
    private Integer age;

    @NotBlank
    private String gender;

    @NotBlank
    @Size(max = 255)
    private String pastexperience;

    @NotBlank
    @Size(max = 160)
    private String highestQualification;

    @NotBlank
    @Size(max = 160)
    private String experties;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPastexperience() {
        return pastexperience;
    }

    public void setPastexperience(String pastexperience) {
        this.pastexperience = pastexperience;
    }

    public String getHighestQualification() {
        return highestQualification;
    }

    public void setHighestQualification(String highestQualification) {
        this.highestQualification = highestQualification;
    }

    public String getExperties() {
        return experties;
    }

    public void setExperties(String experties) {
        this.experties = experties;
    }
}
