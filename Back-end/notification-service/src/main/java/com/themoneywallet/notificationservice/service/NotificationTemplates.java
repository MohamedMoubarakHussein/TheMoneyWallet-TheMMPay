package com.themoneywallet.notificationservice.service;

import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class NotificationTemplates {

    private static final Map<String, String> TEMPLATES = Map.of(
        "PAYMENT_RECEIVED",
        "You received ${amount} from ${sender}",
        "PASSWORD_CHANGED",
        "Your password was updated",
        "WALLET_LOW",
        "Low balance warning: ${balance}",
        " USER_PROFILE_CREATED",
        "Wlcome ${name} to our service"
    );

   
}
