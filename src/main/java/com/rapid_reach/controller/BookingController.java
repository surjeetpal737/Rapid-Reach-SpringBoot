package com.rapid_reach.controller;

import com.rapid_reach.dto.BookingRequestDto;
import com.rapid_reach.dto.CompletionOtpDto;
import com.rapid_reach.entity.Booking;
import com.rapid_reach.entity.Customer;
import com.rapid_reach.entity.ServiceProvider;
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

import java.util.List;

@Controller
public class BookingController {

    private final BookingService bookingService;
    private final ProviderService providerService;

    public BookingController(BookingService bookingService, ProviderService providerService) {
        this.bookingService = bookingService;
        this.providerService = providerService;
    }

    /** Show the booking form pre-filled with provider details. */
    @GetMapping({"/bookings/new", "/BookService.jsp"})
    public String bookingForm(@RequestParam("providerId") Long providerId,
                              HttpSession session,
                              Model model) {
        Customer customer = (Customer) session.getAttribute("customer");
        if (customer == null) {
            return "redirect:/customers/login";
        }

        BookingRequestDto bookingRequest = new BookingRequestDto();
        bookingRequest.setProviderId(providerId);

        model.addAttribute("customer", customer);
        model.addAttribute("provider", providerService.getById(providerId));
        model.addAttribute("bookingRequest", bookingRequest);
        return "customer/book-service";
    }

    /** Create a new booking submitted from the booking form. */
    @PostMapping({"/bookings", "/BookingServlet"})
    public String createBooking(
            @Valid @ModelAttribute("bookingRequest") BookingRequestDto dto,
            BindingResult bindingResult,
            HttpSession session,
            Model model) {

        Customer customer = (Customer) session.getAttribute("customer");
        if (customer == null) {
            return "redirect:/customers/login";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("customer", customer);
            model.addAttribute("provider", providerService.getById(dto.getProviderId()));
            return "customer/book-service";
        }

        bookingService.createBooking(customer, dto);
        // FIX: redirect to a confirmation page, not silently back to dashboard
        return "redirect:/bookings/success";
    }

    /** Booking confirmation page shown after successful booking. */
    @GetMapping({"/bookings/success", "/BookingSuccess.jsp"})
    public String success() {
        return "customer/booking-success";
    }


    @GetMapping("/bookings/my")
    public String myBookings(HttpSession session, Model model) {
        Customer customer = (Customer) session.getAttribute("customer");
        if (customer == null) {
            return "redirect:/customers/login";
        }

        List<Booking> bookings = bookingService.findForCustomer(customer.getId());
        model.addAttribute("customer", customer);
        model.addAttribute("bookings", bookings);
        return "customer/my-bookings";
    }

    /** Provider accepts or rejects a booking from their dashboard. */
    @PostMapping("/bookings/status")
    public String updateStatus(@RequestParam Long id,
                               @RequestParam String status,
                               HttpSession session) {
        // FIX: guard — only a logged-in provider can change booking status
        ServiceProvider provider = (ServiceProvider) session.getAttribute("provider");
        if (provider == null) {
            return "redirect:/providers/login";
        }
        bookingService.updateStatus(id, status);
        return "redirect:/providers/dashboard";
    }

    /** Legacy GET-based status update (kept for backward compat). */
    @GetMapping("/UpdateBookingStatusServlet")
    public String updateStatusLegacy(@RequestParam Long id,
                                     @RequestParam String status,
                                     HttpSession session) {
        ServiceProvider provider = (ServiceProvider) session.getAttribute("provider");
        if (provider == null) {
            return "redirect:/providers/login";
        }
        bookingService.updateStatus(id, status);
        return "redirect:/providers/dashboard";
    }

    @PostMapping("/bookings/complete")
    public String completeBooking(
            @Valid @ModelAttribute("otpDto") CompletionOtpDto otpDto,
            BindingResult bindingResult,
            HttpSession session,
            Model model) {

        ServiceProvider provider = (ServiceProvider) session.getAttribute("provider");
        if (provider == null) {
            return "redirect:/providers/login";
        }

        if (bindingResult.hasErrors()) {
            // Re-render the dashboard with an error message
            model.addAttribute("provider", provider);
            model.addAttribute("bookings", bookingService.findForProvider(provider.getId()));
            model.addAttribute("otpError", "Invalid OTP format. Enter exactly 6 digits.");
            return "provider/dashboard";
        }

        bookingService.completeWithOtp(otpDto.getBookingId(), otpDto.getOtp(), provider.getId());
        return "redirect:/providers/dashboard";
    }
}
