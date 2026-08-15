package com.app.viajes.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

/**
 * Configuracion del cliente S3 de AWS SDK v2 apuntando a un endpoint
 * Cloudflare R2 (compatible con la API S3). Las credenciales y el endpoint
 * se inyectan desde {@code application.properties} (cloudflare.r2.*).
 */
@Configuration
public class CloudflareR2Config {

    private final String accessKey;
    private final String secretKey;
    private final String endpoint;

    public CloudflareR2Config(@Value("${cloudflare.r2.access-key}") String accessKey,
                              @Value("${cloudflare.r2.secret-key}") String secretKey,
                              @Value("${cloudflare.r2.endpoint}") String endpoint) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.endpoint = endpoint;
    }

    /**
     * Cliente S3 configurado para Cloudflare R2:
     * usa el endpoint personalizado de la cuenta y la region "auto"
     * (la que espera Cloudflare para R2).
     */
    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .region(Region.of("auto"))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)))
                .build();
    }
}