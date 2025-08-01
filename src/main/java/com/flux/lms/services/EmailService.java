package com.flux.lms.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    @Value("${app.name:Sigmat LMS}")
    private String appName;

    @Value("${app.support-email:support@sigmat.com}")
    private String supportEmail;

    private static final Logger LOGGER = Logger.getLogger(EmailService.class.getName());

    public void sendVerificationEmail(String to, String verificationToken) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setTo(to);
            helper.setSubject("Welcome to " + appName + " - Please Verify Your Email");
            helper.setFrom(supportEmail);
            
            String verificationUrl = baseUrl + "/api/public/verify-email?token=" + verificationToken;
            String htmlContent = createVerificationEmailTemplate(to, verificationUrl);
            
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
            LOGGER.info("Verification email sent successfully to: " + to);
            
        } catch (MessagingException e) {
            LOGGER.severe("Failed to send verification email to " + to + ": " + e.getMessage());
            throw new RuntimeException("Failed to send verification email", e);
        }
    }

    public void sendPasswordResetEmail(String to, String token) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setTo(to);
            helper.setSubject("Password Reset Request - " + appName);
            helper.setFrom(supportEmail);
            
            String htmlContent = createPasswordResetEmailTemplate(to, token);
            
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
            LOGGER.info("Password reset email sent successfully to: " + to);
            
        } catch (MessagingException e) {
            LOGGER.severe("Failed to send password reset email to " + to + ": " + e.getMessage());
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }

    public void sendBatchRegistrationSummaryEmail(String to, String adminName, int successCount, int errorCount, String filename) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setTo(to);
            helper.setSubject("Batch Registration Summary - " + appName);
            helper.setFrom(supportEmail);
            
            String htmlContent = createBatchSummaryEmailTemplate(adminName, successCount, errorCount, filename);
            
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
            LOGGER.info("Batch registration summary email sent successfully to: " + to);
            
        } catch (MessagingException e) {
            LOGGER.severe("Failed to send batch summary email to " + to + ": " + e.getMessage());
            throw new RuntimeException("Failed to send batch summary email", e);
        }
    }

    public void sendPaymentSuccessEmail(String to, String customerName, double amount, String currency, String invoiceId) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setTo(to);
            helper.setSubject("Payment Successful - " + appName);
            helper.setFrom(supportEmail);
            
            String htmlContent = createPaymentSuccessEmailTemplate(customerName, amount, currency, invoiceId);
            
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
            LOGGER.info("Payment success email sent successfully to: " + to);
            
        } catch (MessagingException e) {
            LOGGER.severe("Failed to send payment success email to " + to + ": " + e.getMessage());
            throw new RuntimeException("Failed to send payment success email", e);
        }
    }

    private String createVerificationEmailTemplate(String email, String verificationUrl) {
        return "<!DOCTYPE html>" +
            "<html lang=\"en\">" +
            "<head>" +
                "<meta charset=\"UTF-8\">" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                "<title>Email Verification</title>" +
                "<style>" +
                    "body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; padding: 20px; background-color: #f4f4f4; }" +
                    ".email-container { background-color: #ffffff; border-radius: 10px; box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1); overflow: hidden; }" +
                    ".header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 30px; text-align: center; }" +
                    ".header h1 { margin: 0; font-size: 28px; font-weight: 300; }" +
                    ".content { padding: 40px 30px; }" +
                    ".welcome-message { font-size: 18px; color: #2c3e50; margin-bottom: 20px; }" +
                    ".verification-button { display: inline-block; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 15px 30px; text-decoration: none; border-radius: 50px; font-weight: 600; font-size: 16px; margin: 20px 0; }" +
                    ".info-box { background-color: #f8f9fa; border-left: 4px solid #667eea; padding: 20px; margin: 20px 0; border-radius: 0 5px 5px 0; }" +
                    ".footer { background-color: #2c3e50; color: #ecf0f1; padding: 20px 30px; text-align: center; font-size: 14px; }" +
                    ".footer a { color: #3498db; text-decoration: none; }" +
                    ".security-note { font-size: 12px; color: #7f8c8d; margin-top: 20px; padding: 15px; background-color: #ecf0f1; border-radius: 5px; }" +
                "</style>" +
            "</head>" +
            "<body>" +
                "<div class=\"email-container\">" +
                    "<div class=\"header\">" +
                        "<h1>Welcome to " + appName + "</h1>" +
                        "<p>Your Learning Journey Starts Here</p>" +
                    "</div>" +
                    "<div class=\"content\">" +
                        "<div class=\"welcome-message\"><strong>Hello and Welcome!</strong></div>" +
                        "<p>Thank you for joining " + appName + "! We're excited to have you as part of our learning community.</p>" +
                        "<p>To get started and secure your account, please verify your email address by clicking the button below:</p>" +
                        "<div style=\"text-align: center; margin: 30px 0;\">" +
                            "<a href=\"" + verificationUrl + "\" class=\"verification-button\">Verify My Email Address</a>" +
                        "</div>" +
                        "<div class=\"info-box\">" +
                            "<strong>What happens next?</strong>" +
                            "<ul style=\"margin: 10px 0; padding-left: 20px;\">" +
                                "<li>Click the verification button above</li>" +
                                "<li>Your email will be confirmed instantly</li>" +
                                "<li>You'll gain full access to all platform features</li>" +
                                "<li>Start exploring courses and learning materials</li>" +
                            "</ul>" +
                        "</div>" +
                        "<p>If the button doesn't work, you can copy and paste this link into your browser:</p>" +
                        "<p style=\"word-break: break-all; color: #667eea; font-size: 14px;\">" + verificationUrl + "</p>" +
                        "<div class=\"security-note\">" +
                            "<strong>Security Note:</strong> This verification link will expire in 24 hours for your security. " +
                            "If you didn't create an account with " + appName + ", please ignore this email or contact our support team." +
                        "</div>" +
                    "</div>" +
                    "<div class=\"footer\">" +
                        "<p><strong>" + appName + "</strong></p>" +
                        "<p>Need help? Contact us at <a href=\"mailto:" + supportEmail + "\">" + supportEmail + "</a></p>" +
                        "<p style=\"margin-top: 15px; font-size: 12px;\">This is an automated message. Please do not reply to this email.</p>" +
                    "</div>" +
                "</div>" +
            "</body>" +
            "</html>";
    }

    private String createPasswordResetEmailTemplate(String email, String token) {
        return "<!DOCTYPE html>" +
            "<html lang=\"en\">" +
            "<head>" +
                "<meta charset=\"UTF-8\">" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                "<title>Password Reset Request</title>" +
                "<style>" +
                    "body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; padding: 20px; background-color: #f4f4f4; }" +
                    ".email-container { background-color: #ffffff; border-radius: 10px; box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1); overflow: hidden; }" +
                    ".header { background: linear-gradient(135deg, #e74c3c 0%, #c0392b 100%); color: white; padding: 30px; text-align: center; }" +
                    ".header h1 { margin: 0; font-size: 28px; font-weight: 300; }" +
                    ".content { padding: 40px 30px; }" +
                    ".token-box { background-color: #f8f9fa; border: 2px dashed #e74c3c; padding: 20px; margin: 20px 0; border-radius: 5px; text-align: center; font-family: 'Courier New', monospace; font-size: 18px; font-weight: bold; color: #e74c3c; letter-spacing: 2px; }" +
                    ".warning-box { background-color: #fff3cd; border-left: 4px solid #ffc107; padding: 20px; margin: 20px 0; border-radius: 0 5px 5px 0; }" +
                    ".footer { background-color: #2c3e50; color: #ecf0f1; padding: 20px 30px; text-align: center; font-size: 14px; }" +
                    ".footer a { color: #3498db; text-decoration: none; }" +
                "</style>" +
            "</head>" +
            "<body>" +
                "<div class=\"email-container\">" +
                    "<div class=\"header\">" +
                        "<h1>Password Reset Request</h1>" +
                        "<p>" + appName + "</p>" +
                    "</div>" +
                    "<div class=\"content\">" +
                        "<p><strong>Hello,</strong></p>" +
                        "<p>We received a request to reset the password for your " + appName + " account associated with this email address.</p>" +
                        "<p>Please use the following token to reset your password:</p>" +
                        "<div class=\"token-box\">" + token + "</div>" +
                        "<div class=\"warning-box\">" +
                            "<strong>Important Security Information:</strong>" +
                            "<ul style=\"margin: 10px 0; padding-left: 20px;\">" +
                                "<li>This token will expire in 1 hour for your security</li>" +
                                "<li>If you didn't request this reset, please ignore this email</li>" +
                                "<li>Never share this token with anyone</li>" +
                                "<li>Contact support if you have concerns about your account security</li>" +
                            "</ul>" +
                        "</div>" +
                        "<p>If you didn't request a password reset, you can safely ignore this email. Your password will remain unchanged.</p>" +
                    "</div>" +
                    "<div class=\"footer\">" +
                        "<p><strong>" + appName + "</strong></p>" +
                        "<p>Need help? Contact us at <a href=\"mailto:" + supportEmail + "\">" + supportEmail + "</a></p>" +
                        "<p style=\"margin-top: 15px; font-size: 12px;\">This is an automated message. Please do not reply to this email.</p>" +
                    "</div>" +
                "</div>" +
            "</body>" +
            "</html>";
    }

    private String createBatchSummaryEmailTemplate(String adminName, int successCount, int errorCount, String filename) {
        int totalProcessed = successCount + errorCount;
        String statusColor = errorCount == 0 ? "#27ae60" : (successCount > 0 ? "#f39c12" : "#e74c3c");
        String statusText = errorCount == 0 ? "Completed Successfully" : (successCount > 0 ? "Completed with Issues" : "Failed");
        
        return "<!DOCTYPE html>" +
            "<html lang=\"en\">" +
            "<head>" +
                "<meta charset=\"UTF-8\">" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                "<title>Batch Registration Summary</title>" +
                "<style>" +
                    "body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; padding: 20px; background-color: #f4f4f4; }" +
                    ".email-container { background-color: #ffffff; border-radius: 10px; box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1); overflow: hidden; }" +
                    ".header { background: linear-gradient(135deg, #3498db 0%, #2980b9 100%); color: white; padding: 30px; text-align: center; }" +
                    ".stats-container { display: flex; justify-content: space-around; margin: 20px 0; flex-wrap: wrap; }" +
                    ".stat-box { background-color: #f8f9fa; padding: 20px; border-radius: 8px; text-align: center; margin: 10px; flex: 1; min-width: 120px; }" +
                    ".stat-number { font-size: 32px; font-weight: bold; margin-bottom: 5px; }" +
                    ".success { color: #27ae60; }" +
                    ".error { color: #e74c3c; }" +
                    ".total { color: #3498db; }" +
                    ".status-badge { display: inline-block; padding: 8px 16px; border-radius: 20px; color: white; font-weight: bold; margin: 10px 0; background-color: " + statusColor + "; }" +
                    ".content { padding: 40px 30px; }" +
                    ".footer { background-color: #2c3e50; color: #ecf0f1; padding: 20px 30px; text-align: center; font-size: 14px; }" +
                "</style>" +
            "</head>" +
            "<body>" +
                "<div class=\"email-container\">" +
                    "<div class=\"header\">" +
                        "<h1>Batch Registration Summary</h1>" +
                        "<p>" + appName + "</p>" +
                    "</div>" +
                    "<div class=\"content\">" +
                        "<p><strong>Hello " + adminName + ",</strong></p>" +
                        "<p>Your batch user registration process has been completed. Here's a summary of the results:</p>" +
                        "<div style=\"text-align: center;\">" +
                            "<div class=\"status-badge\">" + statusText + "</div>" +
                        "</div>" +
                        "<div class=\"stats-container\">" +
                            "<div class=\"stat-box\">" +
                                "<div class=\"stat-number total\">" + totalProcessed + "</div>" +
                                "<div>Total Processed</div>" +
                            "</div>" +
                            "<div class=\"stat-box\">" +
                                "<div class=\"stat-number success\">" + successCount + "</div>" +
                                "<div>Successful</div>" +
                            "</div>" +
                            "<div class=\"stat-box\">" +
                                "<div class=\"stat-number error\">" + errorCount + "</div>" +
                                "<div>Failed</div>" +
                            "</div>" +
                        "</div>" +
                        "<p><strong>File Processed:</strong> " + filename + "</p>" +
                        (successCount > 0 ? 
                        "<p>✅ <strong>" + successCount + " users</strong> have been successfully registered and will receive verification emails.</p>" : "") +
                        (errorCount > 0 ? 
                        "<p>⚠️ <strong>" + errorCount + " users</strong> could not be registered due to validation errors or duplicate data.</p>" : "") +
                        "<p>All successfully registered users will receive email verification links and can begin using the platform once they verify their accounts.</p>" +
                    "</div>" +
                    "<div class=\"footer\">" +
                        "<p><strong>" + appName + "</strong></p>" +
                        "<p>This is an automated summary report.</p>" +
                    "</div>" +
                "</div>" +
            "</body>" +
            "</html>";
    }

    private String createPaymentSuccessEmailTemplate(String customerName, double amount, String currency, String invoiceId) {
        return "<!DOCTYPE html>" +
            "<html lang=\"en\">" +
            "<head>" +
                "<meta charset=\"UTF-8\">" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                "<title>Payment Successful</title>" +
                "<style>" +
                    "body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; padding: 20px; background-color: #f4f4f4; }" +
                    ".email-container { background-color: #ffffff; border-radius: 10px; box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1); overflow: hidden; }" +
                    ".header { background: linear-gradient(135deg, #27ae60 0%, #2ecc71 100%); color: white; padding: 30px; text-align: center; }" +
                    ".header h1 { margin: 0; font-size: 28px; font-weight: 300; }" +
                    ".content { padding: 40px 30px; }" +
                    ".success-icon { font-size: 48px; color: #27ae60; text-align: center; margin: 20px 0; }" +
                    ".payment-details { background-color: #f8f9fa; border-left: 4px solid #27ae60; padding: 20px; margin: 20px 0; border-radius: 0 5px 5px 0; }" +
                    ".amount { font-size: 24px; font-weight: bold; color: #27ae60; text-align: center; margin: 20px 0; }" +
                    ".footer { background-color: #2c3e50; color: #ecf0f1; padding: 20px 30px; text-align: center; font-size: 14px; }" +
                    ".footer a { color: #3498db; text-decoration: none; }" +
                "</style>" +
            "</head>" +
            "<body>" +
                "<div class=\"email-container\">" +
                    "<div class=\"header\">" +
                        "<h1>Payment Successful!</h1>" +
                        "<p>" + appName + "</p>" +
                    "</div>" +
                    "<div class=\"content\">" +
                        "<div class=\"success-icon\">✅</div>" +
                        "<p><strong>Hello " + customerName + ",</strong></p>" +
                        "<p>Great news! Your payment has been successfully processed.</p>" +
                        "<div class=\"amount\">" + currency + " " + String.format("%.2f", amount) + "</div>" +
                        "<div class=\"payment-details\">" +
                            "<strong>Payment Details:</strong>" +
                            "<ul style=\"margin: 10px 0; padding-left: 20px;\">" +
                                "<li>Amount: " + currency + " " + String.format("%.2f", amount) + "</li>" +
                                "<li>Invoice ID: " + invoiceId + "</li>" +
                                "<li>Date: " + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' HH:mm")) + "</li>" +
                                "<li>Status: Paid</li>" +
                            "</ul>" +
                        "</div>" +
                        "<p>Your subscription is now active and you have full access to all premium features.</p>" +
                        "<p>If you have any questions about your payment or subscription, please don't hesitate to contact our support team.</p>" +
                        "<p>Thank you for choosing " + appName + "!</p>" +
                    "</div>" +
                    "<div class=\"footer\">" +
                        "<p><strong>" + appName + "</strong></p>" +
                        "<p>Need help? Contact us at <a href=\"mailto:" + supportEmail + "\">" + supportEmail + "</a></p>" +
                        "<p style=\"margin-top: 15px; font-size: 12px;\">This is an automated message. Please do not reply to this email.</p>" +
                    "</div>" +
                "</div>" +
            "</body>" +
            "</html>";
    }
}