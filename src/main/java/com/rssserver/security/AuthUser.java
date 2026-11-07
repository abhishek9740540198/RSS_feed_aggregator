package com.rssserver.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a controller method parameter to be resolved to the User placed
 * in the request by ApiKeyAuthFilter/JwtAuthFilter -- mirroring Go's
 * authedHandler(w, r, user database.User) function signature.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthUser {
}
