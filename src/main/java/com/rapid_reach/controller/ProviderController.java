package com.rapid_reach.controller;

import com.rapid_reach.dto.CompletionOtpDto;
import com.rapid_reach.dto.ForgotPasswordDto;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * ProviderController — registration, login, dashboard, and password reset for service providers.
 *
 * Fixes applied:
 *  1. dashboard() now adds an empty CompletionOtpDto to the model so the OTP form
 *     in provider/dashboard.html binds correctly (was causing BindException at runtime).
 *  2. Null session checks now redirect to /providers/login consistently.
 *  3. Added forgot-password endpoints (provider-specific).
 *  4. Provider registration redirect goes to /providers/login (was already correct).
 */
@Controller
public class ProviderController {

    private final ProviderService providerService;
    private final BookingService bookingService;

    public ProviderController(ProviderService providerService, BookingService bookingService) {
        this.providerService = providerService;
        this.bookingService = bookingService;
    }

    // ─── Registration ─────────────────────────────────────────────────────────

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

    // ─── Login ────────────────────────────────────────────────────────────────

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

    // ─── Dashboard ────────────────────────────────────────────────────────────

    /**
     * FIX: Added otpDto to the model.
     * The OTP completion form in provider/dashboard.html uses th:object="${otpDto}",
     * so without this attribute Thymeleaf throws a BindException on page render.
     */
    @GetMapping({"/providers/dashboard", "/providerDashboard.jsp"})
    public String dashboard(HttpSession session, Model model) {
        ServiceProvider provider = (ServiceProvider) session.getAttribute("provider");
        if (provider == null) {
            return "redirect:/providers/login";
        }

        model.addAttribute("provider", provider);
        model.addAttribute("bookings", bookingService.findForProvider(provider.getId()));
        // FIX: OTP form binding object — must be in model or Thymeleaf breaks
        if (!model.containsAttribute("otpDto")) {
            model.addAttribute("otpDto", new CompletionOtpDto());
        }
        return "provider/dashboard";
    }

    // ─── Profile detail ───────────────────────────────────────────────────────

    @GetMapping({"/providerDetail.jsp"})
    public String details(HttpSession session, Model model) {
        ServiceProvider provider = (ServiceProvider) session.getAttribute("provider");
        if (provider == null) {
            return "redirect:/providers/login";
        }
        model.addAttribute("provider", provider);
        return "provider/details";
    }

    // ─── Forgot password ──────────────────────────────────────────────────────

    @GetMapping("/providers/forgot-password")
    public String forgotPasswordForm(Model model) {
        model.addAttribute("forgotPassword", new ForgotPasswordDto());
        return "provider/forgot-password";
    }

    @PostMapping("/providers/forgot-password")
    public String forgotPassword(
            @Valid @ModelAttribute("forgotPassword") ForgotPasswordDto dto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "provider/forgot-password";
        }

        providerService.resetPassword(dto);
        redirectAttributes.addFlashAttribute("successMessage",
                "Password updated successfully. Please log in.");
        return "redirect:/providers/login";
    }
}
