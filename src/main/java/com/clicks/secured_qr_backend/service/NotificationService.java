package com.clicks.secured_qr_backend.service;

import com.clicks.secured_qr_backend.utils.AppUtils;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final JavaMailSender mailSender;

    public void sendActivationEmail(String name, String email, String token) {
        String activationMessage = AppUtils.getActivationMessage(name, token);
        System.out.println("activationMessage = " + activationMessage);
        sendHtmlEmail(email, "no_reply@clicks.it", "Activate your account", activationMessage);
    }

    private void sendHtmlEmail(String to, String from, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true); // Set the HTML content

            mailSender.send(message);
        } catch (MessagingException e) {
            // Handle the exception (e.g., log or throw custom exception)
            e.printStackTrace();
        }
    }

    private void sendSms(String to, String from, String subject, String message) {

    }
}
