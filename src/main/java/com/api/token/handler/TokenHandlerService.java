package com.api.token.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * TokenHandlerService handles API token retrieval using Apache HTTP Client.
 * This service makes HTTP calls to fetch API tokens and returns them as strings.
 */
public class TokenHandlerService {
    private static final Logger logger = LoggerFactory.getLogger(TokenHandlerService.class);
    private static final HttpClient httpClient = HttpClients.createDefault();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Retrieves an API token from the specified endpoint.
     *
     * @param tokenEndpoint The API endpoint URL for token retrieval
     * @param requestBody   The request body containing credentials or required parameters
     * @return The API token string
     * @throws IOException if the HTTP request fails
     */
    public String getApiToken(String tokenEndpoint, String requestBody) throws IOException {
        if (tokenEndpoint == null || tokenEndpoint.trim().isEmpty()) {
            throw new IllegalArgumentException("Token endpoint cannot be null or empty");
        }

        HttpPost httpPost = new HttpPost(tokenEndpoint);
        httpPost.setHeader("Content-Type", "application/json");

        try {
            if (requestBody != null && !requestBody.trim().isEmpty()) {
                httpPost.setEntity(new StringEntity(
                        requestBody,
                        ContentType.APPLICATION_JSON.withCharset(StandardCharsets.UTF_8)
                ));
            }

            logger.info("Making API call to endpoint: {}", tokenEndpoint);
            HttpResponse response = httpClient.execute(httpPost);

            int statusCode = response.getStatusLine().getStatusCode();
            String responseBody = response.getEntity() == null
                    ? ""
                    : EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

            logger.debug("Received response with status code: {}", statusCode);

            if (statusCode == 200) {
                return extractTokenFromResponse(responseBody);
            }

            logger.error("Failed to retrieve token. Status code: {}. Response: {}", statusCode, responseBody);
            throw new IOException("Failed to retrieve token. HTTP Status: " + statusCode);
        } finally {
            httpPost.releaseConnection();
        }
    }

    /**
     * Extracts the token from the API response using Jackson data binding.
     *
     * @param responseBody The response body from the API
     * @return The extracted token string
     */
    private String extractTokenFromResponse(String responseBody) {
        try {
            TokenResponse tokenResponse = objectMapper.readValue(responseBody, TokenResponse.class);

            if (tokenResponse.getToken() != null && !tokenResponse.getToken().isBlank()) {
                return tokenResponse.getToken();
            }

            if (tokenResponse.getAccessToken() != null && !tokenResponse.getAccessToken().isBlank()) {
                return tokenResponse.getAccessToken();
            }

            if (tokenResponse.getApiToken() != null && !tokenResponse.getApiToken().isBlank()) {
                return tokenResponse.getApiToken();
            }

            logger.warn("No token field found in response: {}", responseBody);
            throw new IllegalArgumentException("No token field found in API response");
        } catch (Exception e) {
            logger.error("Error parsing token from response: {}", responseBody, e);
            throw new RuntimeException("Failed to extract token from response", e);
        }
    }
}