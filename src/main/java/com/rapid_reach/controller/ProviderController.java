package com.rapid_reach.controller;


import com.rapid_reach.dto.ProviderLoginDto;
import com.rapid_reach.dto.ProviderRegistrationDto;
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

@Controller
public class ProviderController {

    private final ProviderService providerService;
    private final BookingService bookingService;

    public ProviderController(ProviderService providerService, BookingService bookingService) {
        this.providerService = providerService;
        this.bookingService = bookingService;
    }

    @GetMapping({"/providers/register", "/serviceProviderLogin.jsp"})
    public String registrationForm(Model model) {
        model.addAttribute("provider", new ProviderRegistrationDto());
        return "provider/register";
    }

    @PostMapping({"/providers/register", "/providerRegistration"})
    public String register(
            @Valid @ModelAttribute("provider") ProviderRegistrationDto dto,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "provider/register";
        }
        providerService.register(dto);
        return "redirect:/providers/login";
    }

    @GetMapping({"/providers/login", "/providerLogin.jsp"})
    public String loginForm(Model model) {
        model.addAttribute("login", new ProviderLoginDto());
        return "provider/login";
    }

    @PostMapping({"/providers/login", "/providerLoginChecker"})
    public String login(
            @Valid @ModelAttribute("login") ProviderLoginDto dto,
            BindingResult bindingResult,
            HttpSession session,
            Model model) {
        if (bindingResult.hasErrors()) {
            return "provider/login";
        }

        return providerService.login(dto)
                .map(provider -> {
                    session.setAttribute("provider", provider);
                    return "redirect:/providers/dashboard";
                })
                .orElseGet(() -> {
                    model.addAttribute("loginError", "Invalid email or password.");
                    return "provider/login";
                });
    }

    @GetMapping({"/providers/dashboard", "/providerDashboard.jsp"})
    public String dashboard(HttpSession session, Model model) {
        ServiceProvider provider = (ServiceProvider) session.getAttribute("provider");
        if (provider == null) {
            return "redirect:/providers/login";
        }

        model.addAttribute("provider", provider);
        model.addAttribute("bookings", bookingService.findForProvider(provider.getId()));
        return "provider/dashboard";
    }

    @GetMapping({"/providerDetail.jsp"})
    public String details(HttpSession session, Model model) {
        ServiceProvider provider = (ServiceProvider) session.getAttribute("provider");
        if (provider == null) {
            return "redirect:/providers/register";
        }

        model.addAttribute("provider", provider);
        return "provider/details";
    }
}
