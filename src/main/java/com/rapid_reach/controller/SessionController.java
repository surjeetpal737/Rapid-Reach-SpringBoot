package com.rapid_reach.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SessionController {

    @GetMapping({"/logout", "/Logout.jsp"})
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
