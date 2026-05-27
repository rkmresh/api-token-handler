package com.api.token.handler;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

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
                httpPost.setEntity(new StringEntity(requestBody));
            }

            logger.info("Making API call to endpoint: {}", tokenEndpoint);
            HttpResponse response = httpClient.execute(httpPost);

            int statusCode = response.getStatusLine().getStatusCode();
            String responseBody = EntityUtils.toString(response.getEntity());

            logger.debug("Received response with status code: {}", statusCode);

            if (statusCode == 200) {
                return extractTokenFromResponse(responseBody);
            } else {
                logger.error("Failed to retrieve token. Status code: {}. Response: {}", statusCode, responseBody);
                throw new IOException("Failed to retrieve token. HTTP Status: " + statusCode);
            }

        } finally {
            httpPost.releaseConnection();
        }
    }

    /**
     * Extracts the token from the API response.
     * Assumes the response is in JSON format with a "token" or "access_token" field.
     *
     * @param responseBody The response body from the API
     * @return The extracted token string
     */
    private String extractTokenFromResponse(String responseBody) {
        try {
            JsonNode jsonResponse = objectMapper.readTree(responseBody);

            // Try common token field names
            if (jsonResponse.has("token")) {
                return jsonResponse.get("token").asText();
            } else if (jsonResponse.has("access_token")) {
                return jsonResponse.get("access_token").asText();
            } else if (jsonResponse.has("apiToken")) {
                return jsonResponse.get("apiToken").asText();
            } else {
                logger.warn("No token field found in response. Available fields: {}", jsonResponse.fieldNames());
                throw new IllegalArgumentException("No token field found in API response");
            }
        } catch (Exception e) {
            logger.error("Error parsing token from response: {}", responseBody, e);
            throw new RuntimeException("Failed to extract token from response", e);
        }
    }
}
