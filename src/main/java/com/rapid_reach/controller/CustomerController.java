package com.rapid_reach.controller;

import com.rapid_reach.dto.CustomerLoginDto;
import com.rapid_reach.dto.CustomerRegistrationDto;
import com.rapid_reach.dto.ForgotPasswordDto;
import com.rapid_reach.dto.ProviderSearchDto;
import com.rapid_reach.entity.Customer;
import com.rapid_reach.service.CustomerService;
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

@Controller
public class CustomerController {

    private final CustomerService customerService;
    private final ProviderService providerService;

    public CustomerController(CustomerService customerService, ProviderService providerService) {
        this.customerService = customerService;
        this.providerService = providerService;
    }

    // ─── Registration ─────────────────────────────────────────────────────────

    @GetMapping({"/customers/register", "/userLogin.jsp"})
    public String registrationForm(Model model) {
        model.addAttribute("customer", new CustomerRegistrationDto());
        return "customer/register";
    }

    @PostMapping({"/customers/register", "/userRegistration"})
    public String register(
            @Valid @ModelAttribute("customer") CustomerRegistrationDto dto,
            BindingResult bindingResult,
            HttpSession session) {

        if (bindingResult.hasErrors()) {
            return "customer/register";
        }

        Customer customer = customerService.register(dto);
        // FIX: store in session then redirect to dashboard
        session.setAttribute("customer", customer);
        return "redirect:/customers/dashboard";
    }

    // ─── Login ────────────────────────────────────────────────────────────────

    @GetMapping({"/customers/login", "/Login.html"})
    public String loginForm(Model model) {
        model.addAttribute("login", new CustomerLoginDto());
        return "customer/login";
    }

    @PostMapping("/customers/login")
    public String login(
            @Valid @ModelAttribute("login") CustomerLoginDto dto,
            BindingResult bindingResult,
            HttpSession session,
            Model model) {

        if (bindingResult.hasErrors()) {
            return "customer/login";
        }

        return customerService.login(dto)
                .map(customer -> {
                    session.setAttribute("customer", customer);
                    return "redirect:/customers/dashboard";
                })
                .orElseGet(() -> {
                    model.addAttribute("loginError", "Invalid email or password.");
                    return "customer/login";
                });
    }

    // ─── Dashboard ────────────────────────────────────────────────────────────

    @GetMapping({"/customers/dashboard", "/userDetail.jsp"})
    public String dashboard(HttpSession session, Model model) {
        Customer customer = (Customer) session.getAttribute("customer");
        if (customer == null) {
            // FIX: redirect to login (not register) — the user just needs to sign in
            return "redirect:/customers/login";
        }
        model.addAttribute("customer", customer);
        model.addAttribute("search", new ProviderSearchDto());
        return "customer/dashboard";
    }

    // ─── Provider search ──────────────────────────────────────────────────────

    @PostMapping({"/providers/search", "/SearchProviderServlet"})
    public String searchProviders(
            @Valid @ModelAttribute("search") ProviderSearchDto dto,
            BindingResult bindingResult,
            HttpSession session,
            Model model) {

        Customer customer = (Customer) session.getAttribute("customer");
        if (customer == null) {
            return "redirect:/customers/login";
        }

        model.addAttribute("customer", customer);
        if (bindingResult.hasErrors()) {
            return "customer/dashboard";
        }

        model.addAttribute("providers", providerService.search(dto));
        model.addAttribute("search", dto);
        return "customer/provider-results";
    }

    // ─── Forgot password ──────────────────────────────────────────────────────

    @GetMapping("/customers/forgot-password")
    public String forgotPasswordForm(Model model) {
        model.addAttribute("forgotPassword", new ForgotPasswordDto());
        return "customer/forgot-password";
    }

    @PostMapping("/customers/forgot-password")
    public String forgotPassword(
            @Valid @ModelAttribute("forgotPassword") ForgotPasswordDto dto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "customer/forgot-password";
        }

        customerService.resetPassword(dto);
        redirectAttributes.addFlashAttribute("successMessage",
                "Password updated successfully. Please log in.");
        return "redirect:/customers/login";
    }
}
