package com.rapid_reach.controller;

import com.rapid_reach.dto.BookingRequestDto;
import com.rapid_reach.entity.Customer;
import com.rapid_reach.service.BookingService;
import com.rapid_reach.service.ProviderService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class BookingController {

    private final BookingService bookingService;
    private final ProviderService providerService;

    public BookingController(BookingService bookingService, ProviderService providerService) {
        this.bookingService = bookingService;
        this.providerService = providerService;
    }

    @GetMapping({"/bookings/new", "/BookService.jsp"})
    public String bookingForm(@RequestParam("providerId") Long providerId, HttpSession session, Model model) {
        Customer customer = (Customer) session.getAttribute("customer");
        if (customer == null) {
            return "redirect:/customers/register";
        }

        BookingRequestDto bookingRequest = new BookingRequestDto();
        bookingRequest.setProviderId(providerId);
        model.addAttribute("customer", customer);
        model.addAttribute("provider", providerService.getById(providerId));
        model.addAttribute("bookingRequest", bookingRequest);
        return "customer/book-service";
    }

    @PostMapping({"/bookings", "/BookingServlet"})
    public String createBooking(
            @Valid @ModelAttribute("bookingRequest") BookingRequestDto dto,
            BindingResult bindingResult,
            HttpSession session,
            Model model) {
        Customer customer = (Customer) session.getAttribute("customer");
        if (customer == null) {
            return "redirect:/customers/register";
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("customer", customer);
            model.addAttribute("provider", providerService.getById(dto.getProviderId()));
            return "customer/book-service";
        }

        bookingService.createBooking(customer, dto);
        return "redirect:/customers/dashboard";
    }

    @GetMapping({"/bookings/success", "/BookingSuccess.jsp"})
    public String success() {
        return "customer/booking-success";
    }

    @PostMapping("/bookings/status")
    public String updateStatus(@RequestParam Long id, @RequestParam String status) {
        bookingService.updateStatus(id, status);
        return "redirect:/providers/dashboard";
    }

    @GetMapping("/UpdateBookingStatusServlet")
    public String updateStatusLegacy(@RequestParam Long id, @RequestParam String status) {
        bookingService.updateStatus(id, status);
        return "redirect:/providers/dashboard";
    }
}
