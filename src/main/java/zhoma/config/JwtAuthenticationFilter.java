package zhoma.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import zhoma.service.JwtService;
import zhoma.service.UserService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final HandlerExceptionResolver handlerExceptionResolver;
    private final JwtService jwtService;
    private final UserService userDetailsService;

    private static final String[] PUBLIC_URIS = {
            "/swagger-ui/**", "/v3/api-docs/**", "/auth/**", "/products/**", "/categories/**", "/brands/**"
    };

    public JwtAuthenticationFilter(
            JwtService jwtService,
            UserService userDetailsService,
            HandlerExceptionResolver handlerExceptionResolver
    ) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        if (isPublicUri(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");
        final String refreshHeader = request.getHeader("Refresh-Token"); // Для рефреш токена

        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                handleAccessToken(request, response, filterChain, authHeader);
            } else if (refreshHeader != null) {
                handleRefreshToken(request, response, refreshHeader);
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Unauthorized: Token is missing");
            }
        } catch (Exception exception) {
            handlerExceptionResolver.resolveException(request, response, null, exception);
        }
    }

    private void handleAccessToken(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain, String authHeader) throws ServletException, IOException {
        final String jwt = authHeader.substring(7);
        final String username = jwtService.extractUsername(jwt);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (username != null && authentication == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtService.isTokenValid(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }

    private void handleRefreshToken(HttpServletRequest request, HttpServletResponse response, String refreshHeader) throws IOException {
        final String refreshToken = refreshHeader;

        String username = jwtService.extractUsername(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (jwtService.isRefreshTokenValid(refreshToken, userDetails)) {
            String newAccessToken = jwtService.generateToken(userDetails);

            Map<String, String> responseBody = new HashMap<>();
            responseBody.put("accessToken", newAccessToken);
            responseBody.put("expiresIn", String.valueOf(jwtService.getExpirationTime()));

            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_OK);

            String jsonResponse = new ObjectMapper().writeValueAsString(responseBody);
            response.getWriter().write(jsonResponse);
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid refresh token");
        }

    }

    private boolean isPublicUri(HttpServletRequest request) {
        String uri = request.getRequestURI();
        AntPathMatcher pathMatcher = new AntPathMatcher();

        for (String publicUri : PUBLIC_URIS) {
            if (pathMatcher.match(publicUri, uri)) {
                return true;
            }
        }
        return false;
    }
}
