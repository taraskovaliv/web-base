package dev.kovaliv.config;

import lombok.Getter;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.net.URI;

import static dev.kovaliv.config.ContextConfig.R2_PROFILE;
import static java.lang.System.getenv;

@Getter
@Profile(R2_PROFILE)
@Component
public class R2Client {
    private final S3Client s3Client;

    public R2Client() {
        this.s3Client = buildS3Client();
    }

    private S3Client buildS3Client() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(
                getenv("R2_ACCESS_KEY"),
                getenv("R2_SECRET_KEY")
        );

        S3Configuration serviceConfiguration = S3Configuration.builder()
                .pathStyleAccessEnabled(true)
                .build();

        String endpoint = String.format("https://%s.r2.cloudflarestorage.com", getenv("R2_ACCOUNT_ID"));
        return S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .region(Region.of("auto"))
                .serviceConfiguration(serviceConfiguration)
                .build();
    }
}
