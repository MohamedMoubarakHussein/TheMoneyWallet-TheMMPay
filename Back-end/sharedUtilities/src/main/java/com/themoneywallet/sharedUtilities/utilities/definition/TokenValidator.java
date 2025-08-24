package com.themoneywallet.sharedUtilities.utilities.definition;

import java.util.UUID;
import io.jsonwebtoken.Claims;


public interface TokenValidator {
    boolean isTokenValid(String token);
    Claims extractInfoFromToken(String token);
    UUID getUserId(String token);
}
