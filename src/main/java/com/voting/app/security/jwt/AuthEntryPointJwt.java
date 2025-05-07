
package com.voting.app.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.voting.app.security.error.ErrorDetails;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {
    private static final Logger logger = LoggerFactory.getLogger(AuthEntryPointJwt.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        logger.error("Authentication failed for request {}: {}", request.getServletPath(), authException.getMessage());

        ErrorDetails errorDetails = createErrorDetails(authException, request);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        try {
            objectMapper.writeValue(response.getOutputStream(), errorDetails);
        } catch (IOException e) {
            logger.error("Error writing error response", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error processing authentication failure");
        }
    }

    private ErrorDetails createErrorDetails(AuthenticationException exception, HttpServletRequest request) {
        ErrorDetails errorDetails = new ErrorDetails();
        errorDetails.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        errorDetails.setError("Unauthorized");
        errorDetails.setMessage(getSpecificMessage(exception));
        errorDetails.setPath(request.getServletPath());
        errorDetails.setTimestamp(System.currentTimeMillis());
        return errorDetails;
    }

    private String getSpecificMessage(AuthenticationException exception) {
        if (exception instanceof BadCredentialsException) {
            return "Invalid credentials provided";
        } else if (exception instanceof InsufficientAuthenticationException) {
            return "Full authentication is required to access this resource";
        }
        return exception.getMessage();
    }
}