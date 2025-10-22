package com.example.backend.config;

/**
 * A centralized place to store security-related constant values.
 */
public final class SecurityConstants {

    private SecurityConstants() {
        // This class should not be instantiated.
    }

    public static final String API_V1_PREFIX = "/api/v1";
    public static final String AUTH_ENDPOINT = API_V1_PREFIX + "/auth";
    public static final String OAUTH2_ENDPOINT = "/login/oauth2/code/*";
    public static final String JWKS_ENDPOINT = "/.well-known/jwks.json";

    /**
     * An array of URL patterns that should be publicly accessible without authentication.
     */
    public static final String[] PUBLIC_MATCHERS = {
            "/error",
            "/login/**",
            AUTH_ENDPOINT + "/**",
            OAUTH2_ENDPOINT,
            JWKS_ENDPOINT
    };

    public static final String ACCESS_TOKEN_COOKIE_NAME = "access_token";
    public static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";

    public static final boolean COOKIE_HTTP_ONLY = true;
    public static final boolean COOKIE_SECURE = true;
    public static final String COOKIE_PATH = "/";
    public static final String REFRESH_COOKIE_PATH = AUTH_ENDPOINT + "/refresh";

    public static final String ROLES_CLAIM = "roles";

}
