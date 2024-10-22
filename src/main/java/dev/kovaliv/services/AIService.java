package dev.kovaliv.services;

import dev.kovaliv.cloudflare.CloudflareClient;
import dev.kovaliv.cloudflare.dtos.TextGenerationRequest;
import dev.kovaliv.cloudflare.exception.CloudflareRequestException;
import dev.kovaliv.cloudflare.models.TextGenerationModel;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import static dev.kovaliv.cloudflare.models.TextGenerationModel.OPENCHAT_3_5_0106;
import static java.lang.System.getenv;

@Service
@Profile("AI_PROFILE")
public class AIService {
    private static final CloudflareClient cloudflareClient = new CloudflareClient(
            getenv("CLOUDFLARE_ACCOUNT_ID"), getenv("CLOUDFLARE_AUTH_TOKEN")
    );

    public String reply(String text) throws CloudflareRequestException {
        return reply(text, OPENCHAT_3_5_0106);
    }

    public String reply(String text, TextGenerationModel model) throws CloudflareRequestException {
        return cloudflareClient.generate(new TextGenerationRequest(text), model).getResult().getResponse();
    }
}
