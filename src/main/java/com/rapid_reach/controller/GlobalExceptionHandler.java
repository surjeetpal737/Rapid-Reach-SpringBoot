package com.rapid_reach.controller;

import com.rapid_reach.exception.DuplicateEmailException;
import com.rapid_reach.exception.ResourceNotFoundException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateEmailException.class)
    public String handleDuplicateEmail(DuplicateEmailException ex, Model model) {
        model.addAttribute("errorTitle", "Registration could not be completed");
        model.addAttribute("errorMessage", ex.getMessage());
        return "error";
    }

    @ExceptionHandler({ResourceNotFoundException.class, IllegalArgumentException.class})
    public String handleBadRequest(RuntimeException ex, Model model) {
        model.addAttribute("errorTitle", "Request could not be completed");
        model.addAttribute("errorMessage", ex.getMessage());
        return "error";
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleFileTooLarge(MaxUploadSizeExceededException ex, Model model) {
        model.addAttribute("errorTitle", "File too large");
        model.addAttribute("errorMessage",
                "The uploaded file exceeds the maximum allowed size of 5 MB. "
                        + "Please compress or resize your ID proof and try again.");
        return "error";
    }

    @ExceptionHandler(Exception.class)
    public String handleUnexpected(Exception ex, Model model) {
        model.addAttribute("errorTitle", "Something went wrong");
        // FIX: don't expose internal exception class to end users
        model.addAttribute("errorMessage",
                "An unexpected error occurred. Please try again. "
                        + "If the problem persists, contact the administrator.");
        return "error";
    }
}
