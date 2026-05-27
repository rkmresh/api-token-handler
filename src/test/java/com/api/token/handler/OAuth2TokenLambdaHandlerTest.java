package com.api.token.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for OAuth2TokenLambdaHandler.
 */
public class OAuth2TokenLambdaHandlerTest {
    private OAuth2TokenLambdaHandler handler;
    private ObjectMapper objectMapper;

    @Mock
    private Context context;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        handler = new OAuth2TokenLambdaHandler();
        objectMapper = new ObjectMapper();
        
        when(context.getAwsRequestId()).thenReturn("test-request-id");
    }

    @Test
    public void testMissingTokenEndpointHeader() {
        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent();
        request.setHttpMethod("GET");
        request.setPath("/oauth2/token");
        request.setHeaders(new HashMap<>());

        APIGatewayProxyResponseEvent response = handler.handleRequest(request, context);

        assertEquals(400, response.getStatusCode().intValue());
        assertTrue(response.getBody().contains("Missing required header"));
    }

    @Test
    public void testInvalidHttpMethod() {
        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent();
        request.setHttpMethod("POST");
        request.setPath("/oauth2/token");
        
        Map<String, String> headers = new HashMap<>();
        headers.put("X-Token-Endpoint", "http://example.com/token");
        request.setHeaders(headers);

        APIGatewayProxyResponseEvent response = handler.handleRequest(request, context);

        assertEquals(400, response.getStatusCode().intValue());
        assertTrue(response.getBody().contains("Invalid request"));
    }

    @Test
    public void testInvalidPath() {
        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent();
        request.setHttpMethod("GET");
        request.setPath("/invalid/path");
        
        Map<String, String> headers = new HashMap<>();
        headers.put("X-Token-Endpoint", "http://example.com/token");
        request.setHeaders(headers);

        APIGatewayProxyResponseEvent response = handler.handleRequest(request, context);

        assertEquals(400, response.getStatusCode().intValue());
    }

    @Test
    public void testValidPathVariations() {
        // Test /oauth2/token
        APIGatewayProxyRequestEvent request1 = new APIGatewayProxyRequestEvent();
        request1.setHttpMethod("GET");
        request1.setPath("/oauth2/token");
        
        Map<String, String> headers = new HashMap<>();
        headers.put("X-Token-Endpoint", "http://example.com/token");
        request1.setHeaders(headers);

        // This should not return 400 for invalid path
        APIGatewayProxyResponseEvent response1 = handler.handleRequest(request1, context);
        assertNotEquals(400, response1.getStatusCode().intValue());

        // Test /oauth2/token/
        APIGatewayProxyRequestEvent request2 = new APIGatewayProxyRequestEvent();
        request2.setHttpMethod("GET");
        request2.setPath("/oauth2/token/");
        request2.setHeaders(headers);

        APIGatewayProxyResponseEvent response2 = handler.handleRequest(request2, context);
        assertNotEquals(400, response2.getStatusCode().intValue());
    }

    @Test
    public void testCaseInsensitiveHeaderExtraction() {
        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent();
        request.setHttpMethod("GET");
        request.setPath("/oauth2/token");
        
        Map<String, String> headers = new HashMap<>();
        headers.put("x-token-endpoint", "http://example.com/token");
        request.setHeaders(headers);

        // Should handle case-insensitive header lookup
        APIGatewayProxyResponseEvent response = handler.handleRequest(request, context);
        assertNotEquals(400, response.getStatusCode().intValue());
    }
}
