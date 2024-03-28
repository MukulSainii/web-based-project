package com.smart.controller;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.helper.Message;
import com.smart.service.EmailService;

import jakarta.mail.Session;
import jakarta.servlet.http.HttpSession;

@Controller
public class ForgetController {

    Random random = new Random(1000);
    @Autowired
    private EmailService emailService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    // Displays the form to enter email for password reset
    @GetMapping("/forgot")
    public String openEmailForm() {
        return "forgetEmail";
    }

    // Sends OTP to the provided email
    @PostMapping("/send-OTP")
    public String sendOTP(@RequestParam("email") String email, HttpSession session) {
        System.out.println("Email: " + email);
        // Generating OTP of 4 digits
        int OTP = random.nextInt(99999);
        System.out.println("OTP:-" + OTP);
        String subject = "OTP from SCM";
        String message = "" +
                "<div style='border:1px solid #e2e2e2; padding:20px'>" +
                "<h1>" +
                "OPT is :" +
                "<b>" +
                "</b>" + OTP +
                "</h1>" +
                "</div>";
        String to = email;
        boolean flag = this.emailService.sendEmail(subject, message, to);
        if (flag) {
            session.setAttribute("MyOTP", OTP);
            session.setAttribute("email", email);
            return "verify_otp";
        } else {
            session.setAttribute("message", "Check your email id");
            return "forgetEmail";
        }

    }

    // Handles verified OTP
    @PostMapping("/verified_otp")
    public String verifiedOtp(@RequestParam("otp") int otp, HttpSession session) {
        // Store OTP and email in session
        int MyOtp = (int) session.getAttribute("MyOTP");
        String email = (String) session.getAttribute("email");
        // If OTP matches
        if (MyOtp == otp) {
            // Show password change view
            User user = this.userRepository.getUserByUserName(email);

            // Database se user aana chahiye
            if (user == null) {
                // If null, send error message
                session.setAttribute("message", "User does not exist with this email");
                return "forgetEmail";
            } else {
                // Redirect to change password form
                return "passwordChangeForm";
            }
        } else {
            session.setAttribute("message", "You have entered wrong OTP");
            return "verify_otp";
        }
    }

    // Handles password change
    @PostMapping("/changePassword")
    public String changePassword(@RequestParam("newpassword") String newPassword, HttpSession session) {
        String email = (String) session.getAttribute("email");
        User user = this.userRepository.getUserByUserName(email);
        user.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
        this.userRepository.save(user);
        return "redirect:/signin?change=password change successfully";
    }
}
