package com.rapid_reach.controller;

import com.rapid_reach.dto.AdminLoginDto;
import com.rapid_reach.entity.Admin;
import com.rapid_reach.entity.ServiceProvider;
import com.rapid_reach.service.AdminService;
import com.rapid_reach.service.BookingService;
import com.rapid_reach.service.CustomerService;
import com.rapid_reach.service.FeedbackService;
import com.rapid_reach.service.ProviderService;
import com.rapid_reach.service.ServiceCategoryService;
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
public class AdminController {

    private final AdminService adminService;
    private final ProviderService providerService;
    private final BookingService bookingService;
    private final ServiceCategoryService serviceCategoryService;

    public AdminController(AdminService adminService,
                           ProviderService providerService,
                           BookingService bookingService,
                           ServiceCategoryService serviceCategoryService) {
        this.adminService = adminService;
        this.providerService = providerService;
        this.bookingService = bookingService;
        this.serviceCategoryService = serviceCategoryService;
    }

    @GetMapping({"/admin/login", "/adminLogin.jsp"})
    public String loginForm(Model model) {
        model.addAttribute("login", new AdminLoginDto());
        return "admin/login";
    }

    @PostMapping({"/admin/login", "/adminLoginChecker"})
    public String login(@Valid @ModelAttribute("login") AdminLoginDto dto,
                        BindingResult bindingResult,
                        HttpSession session,
                        Model model) {
        if (bindingResult.hasErrors()) {
            return "admin/login";
        }

        return adminService.login(dto)
                .map(admin -> {
                    session.setAttribute("admin", admin);
                    return "redirect:/admin/dashboard";
                })
                .orElseGet(() -> {
                    model.addAttribute("loginError", "Invalid email or password.");
                    return "admin/login";
                });
    }

    @GetMapping({"/admin/dashboard", "/adminDashboard.jsp"})
    public String dashboard(HttpSession session, Model model) {
        if (session.getAttribute("admin") == null) {
            return "redirect:/admin/login";
        }

        model.addAttribute("pendingProviders", providerService.findByStatus("Pending"));
        model.addAttribute("allBookings", bookingService.findAll());
        model.addAttribute("services", serviceCategoryService.activeServices());
        return "admin/dashboard";
    }


    @GetMapping("/admin/providers")
    public String listProviders(HttpSession session, Model model) {
        if (session.getAttribute("admin") == null) {
            return "redirect:/admin/login";
        }
        model.addAttribute("providers", providerService.findAll());
        return "admin/providers";
    }

    @PostMapping("/admin/providers/approve")
    public String approve(@RequestParam Long providerId,
                          HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return "redirect:/admin/login";
        }
        providerService.updateStatus(providerId, "Approved", null);
        return "redirect:/admin/providers";
    }

    @PostMapping("/admin/providers/reject")
    public String reject(@RequestParam Long providerId,
                         @RequestParam(required = false) String reason,
                         HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return "redirect:/admin/login";
        }
        providerService.updateStatus(providerId, "Rejected",
                (reason == null || reason.isBlank()) ? "Does not meet requirements." : reason);
        return "redirect:/admin/providers";
    }
}
