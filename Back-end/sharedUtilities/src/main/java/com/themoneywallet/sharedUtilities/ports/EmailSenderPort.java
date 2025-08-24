package com.themoneywallet.sharedUtilities.ports;

public interface EmailSenderPort {

    void sendHtmlEmail(String to, String subject, String htmlContent);
}
