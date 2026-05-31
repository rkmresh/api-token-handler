package com.api.token.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.ScheduledEvent;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParameterRequest;
import software.amazon.awssdk.services.ssm.model.GetParameterResponse;
import software.amazon.awssdk.services.ssm.model.ParameterNotFoundException;
import software.amazon.awssdk.services.ssm.model.PutParameterRequest;

import java.time.Instant;
import java.util.Date;

public class TokenRefreshLambdaHandler implements RequestHandler<ScheduledEvent, String> {
    private static final Logger log = LoggerFactory.getLogger(TokenRefreshLambdaHandler.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final String PARAM_NAME = System.getenv("TOKEN_PARAMETER_NAME");
    private static final String TOKEN_ENDPOINT = System.getenv("TOKEN_ENDPOINT");
    private static final String TOKEN_REQUEST_BODY = System.getenv("TOKEN_REQUEST_BODY");
    private static final long DEFAULT_EXPIRES_IN_SEC = Long.parseLong(System.getenv().getOrDefault("DEFAULT_EXPIRES_IN_SEC", "3600"));
    private static final long SAFETY_WINDOW_SEC = Long.parseLong(System.getenv().getOrDefault("EXPIRY_SAFETY_WINDOW_SEC", "60"));

    private final SsmClient ssm = SsmClient.builder()
            .region(Region.of(System.getenv().getOrDefault("AWS_REGION", "us-east-1")))
            .build();

    private final TokenHandlerService tokenService = new TokenHandlerService();

    @Override
    public String handleRequest(ScheduledEvent input, Context context) {
        try {
            validateEnv();

            long now = Instant.now().toEpochMilli();
            String stored = readStoredToken();

            if (stored != null && !checkExpiry(stored)) {
                log.info("Token still valid, no refresh needed");
                return "Token still valid, no refresh needed";
            }

            log.info("Token missing or expired. Refreshing from API...");
            String newToken = tokenService.getApiToken(TOKEN_ENDPOINT, TOKEN_REQUEST_BODY);


            writeStoredToken(newToken);
            log.info("Token refreshed and stored in SSM parameter {}", PARAM_NAME);
            return "Token refreshed";
        } catch (Exception e) {
            log.error("Token refresh Lambda failed", e);
            throw new RuntimeException("Token refresh failed", e);
        }
    }

    private boolean checkExpiry(String token){
        DecodedJWT decodedJWT = JWT.decode(token);
        Date expiresAt = decodedJWT.getExpiresAt();
        if (expiresAt == null || expiresAt.before(new Date())) {
            log.warn("Token expired!");
            return true;
        }

        return false;
    }

    private void validateEnv() {
        if (PARAM_NAME == null || PARAM_NAME.isBlank()) {
            throw new IllegalArgumentException("Missing env TOKEN_PARAMETER_NAME");
        }
        if (TOKEN_ENDPOINT == null || TOKEN_ENDPOINT.isBlank()) {
            throw new IllegalArgumentException("Missing env TOKEN_ENDPOINT");
        }
    }

    private String readStoredToken() throws Exception {
        try {
            GetParameterResponse resp = ssm.getParameter(GetParameterRequest.builder()
                    .name(PARAM_NAME)
                    .withDecryption(true)
                    .build());

            String token = resp.parameter().value();
            return token; //MAPPER.readValue(json, StoredToken.class);
        } catch (ParameterNotFoundException notFound) {
            log.info("Parameter {} not found; first run will create it", PARAM_NAME);
            return null;
        }
    }

    private void writeStoredToken(String token) throws Exception {
        //String json = MAPPER.writeValueAsString(token);
        ssm.putParameter(PutParameterRequest.builder()
                .name(PARAM_NAME)
                .value(token)
                .type("SecureString")
                .overwrite(true)
                .build());
    }
}