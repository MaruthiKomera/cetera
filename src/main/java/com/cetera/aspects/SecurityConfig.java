package com.cetera.aspects;

import com.cetera.exceptions.PfmException;
import com.cetera.exceptions.PfmExceptionCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * cross cutting security concerns
 */
@Component
public class SecurityConfig extends OncePerRequestFilter {

    @Autowired
    private RequestValidator requestValidator;

    private static Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {

        // CORS
        if (request.getMethod().equals(HttpMethod.OPTIONS.toString())) {
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, PATCH, OPTIONS, DELETE");
            response.setHeader("Access-Control-Max-Age", "3600");
            response.setHeader("Access-Control-Allow-Headers", "X-CS-Auth, Content-Type");
        } else {
            response.setHeader("Access-Control-Allow-Origin", "*");
        }

        // Validate
        try {
            requestValidator.validate(
                request.getRequestURI(),
                request.getHeader("X-CS-Auth"),
                request.getHeader("X-CS-Session"),
                request.getHeader("X-CS-Passkey"));
        } catch (RuntimeException e) {
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getOutputStream().println("{ \"error\": \"request invalid\",  \"message\": \"" + e.getMessage() + "\"}");

            if (e instanceof PfmException) {
                PfmException ex = (PfmException) e;
                if (ex.getCode().equals(PfmExceptionCode.SESSION_EXPIRED)
                    || ex.getCode().equals(PfmExceptionCode.SESSION_INVALID)) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                }
            }
            return;
        }

        filterChain.doFilter(request, response);
    }
}