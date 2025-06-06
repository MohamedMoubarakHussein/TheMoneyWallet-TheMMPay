package com.themoneywallet.notificationservice.service;

import com.themoneywallet.notificationservice.event.Event;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class NotificationTemplateResolver {

    private static final Map<String, String> TEMPLATES = Map.of(
        "PAYMENT_RECEIVED",
        "You received ${amount} from ${sender}",
        "PASSWORD_CHANGED",
        "Your password was updated",
        "WALLET_LOW",
        "Low balance warning: ${balance}",
        " USER_PROFILE_CREATED",
        "Wlcome ${name} to our service"
        // Add your predefined event types
    );

    public String resolveTemplate(Event event) {
        String template = TEMPLATES.getOrDefault(
            event.getEventType(),
            "New notification"
        );
        return replacePlaceholders(template, event.getAdditionalData());
    }

    private String replacePlaceholders(
        String template,
        Map<String, Map<String, String>> data
    ) {
        return (
            template = template.replace(
                "${" + "name" + "}",
                data.get("data").get("name")
            )
        );
    }
}
