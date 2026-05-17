package ecommerse.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    // @Autowired
    // private JavaMailSender mailSender;

    public void sendOrderConfirmation(String toEmail, String orderId, Double total) {
        System.out.println("Email confirmation simulated for: " + toEmail);
    }
}
