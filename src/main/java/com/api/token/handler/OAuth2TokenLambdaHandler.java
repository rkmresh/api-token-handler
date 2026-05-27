package com.api.token.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import java.util.Locale;
import java.util.Map;

public class OAuth2TokenLambdaHandler implements
        RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();

        if (request == null || !"GET".equalsIgnoreCase(request.getHttpMethod()) || !isValidPath(request.getPath())) {
            return response.withStatusCode(400).withBody("Invalid request");
        }

        String tokenEndpoint = getHeaderIgnoreCase(request.getHeaders(), "X-Token-Endpoint");
        if (tokenEndpoint == null || tokenEndpoint.isBlank()) {
            return response.withStatusCode(400).withBody("Missing required header: X-Token-Endpoint");
        }

        return response.withStatusCode(200).withBody("Request accepted");
    }

    private boolean isValidPath(String path) {
        return "/oauth2/token".equals(path) || "/oauth2/token/".equals(path);
    }

    private String getHeaderIgnoreCase(Map<String, String> headers, String expectedHeader) {
        if (headers == null || expectedHeader == null) {
            return null;
        }

        String expected = expectedHeader.toLowerCase(Locale.ROOT);
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            if (entry.getKey() != null && entry.getKey().toLowerCase(Locale.ROOT).equals(expected)) {
                return entry.getValue();
            }
        }
        return null;
    }
}