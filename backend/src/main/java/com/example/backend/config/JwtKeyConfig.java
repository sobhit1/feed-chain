package com.example.backend.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

/**
 * Configuration class for loading RSA keys for JWT signing and verification.
 */
@Configuration
public class JwtKeyConfig {

    @Value("${app.jwt.private-key-location}")
    private Resource privateKeyResource;

    @Value("${app.jwt.public-key-location}")
    private Resource publicKeyResource;

    /**
     * Creates and provides the RSAPrivateKey bean.
     *
     * @return The RSAPrivateKey instance.
     * @throws IOException if the key file cannot be read from the specified resource location.
     * @throws InvalidKeySpecException if the provided key data is not a valid PKCS8 key spec.
     */
    @Bean
    public RSAPrivateKey jwtSigningKey() throws IOException, InvalidKeySpecException {
        try (InputStream inputStream = privateKeyResource.getInputStream()) {
            String key = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            String privateKeyPEM = key
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

            byte[] decodedKey = Base64.getDecoder().decode(privateKeyPEM);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodedKey);
            KeyFactory keyFactory = createKeyFactory();
            return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
        }
    }

    /**
     * Creates and provides the RSAPublicKey bean.
     *
     * @return The RSAPublicKey instance.
     * @throws IOException if the key file cannot be read from the specified resource location.
     * @throws InvalidKeySpecException if the provided key data is not a valid X.509 key spec.
     */
    @Bean
    public RSAPublicKey jwtValidationKey() throws IOException, InvalidKeySpecException {
        try (InputStream inputStream = publicKeyResource.getInputStream()) {
            String key = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            String publicKeyPEM = key
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");

            byte[] decodedKey = Base64.getDecoder().decode(publicKeyPEM);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedKey);
            KeyFactory keyFactory = createKeyFactory();
            return (RSAPublicKey) keyFactory.generatePublic(keySpec);
        }
    }

    /**
     * Private helper to get a KeyFactory instance for the RSA algorithm.
     *
     * @return A KeyFactory for RSA.
     */
    private KeyFactory createKeyFactory() {
        try {
            return KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Could not load RSA KeyFactory, the JRE is missing support.", e);
        }
    }
}