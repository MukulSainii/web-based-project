package com.smart.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.helper.Message;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    // Home page mapping
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "Home-page manager");
        return "home";
    }

    // About page mapping
    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("title", "About-page manager");
        return "about";
    }

    // Signup page mapping
    @GetMapping("/signup")
    public String signup(Model model) {
        model.addAttribute("title", "signup-page manager");
        model.addAttribute("user", new User());
        return "signup";
    }

    // Handling user registration
    @RequestMapping(value = "/do_register", method = RequestMethod.POST)
    public String registerUser(@ModelAttribute("user") User user, @RequestParam(value = "agreement", defaultValue = "false") boolean agreement, Model model, HttpSession session) {
        try {
            if (!agreement) {
                System.out.println("You have not agreed to the terms and conditions");
                throw new Exception("You have not agreed to the terms and conditions");
            }

            user.setRole("ROLE_USER");
            user.setEnabled(true);
            user.setImageURL("Default.png");
            // Encode user password with BCryptPasswordEncoder
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            User result = this.userRepository.save(user);

            System.out.println("Agreement: " + agreement);
            System.out.println("User: " + user);

            model.addAttribute("user", new User());
            session.setAttribute("message", new Message("Successfully registered!", "alert-success"));

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("user", user);
            session.setAttribute("message", new Message("Something went wrong!! " + e.getMessage(), "alert-danger"));
        }
        return "signup";
    }

    // Custom login handler
    @GetMapping("/signin")
    public String customLogin(Model model) {
        model.addAttribute("title", "Login page");
        return "login";
    }
}
