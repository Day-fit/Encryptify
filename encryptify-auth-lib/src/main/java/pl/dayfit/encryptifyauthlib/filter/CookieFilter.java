package pl.dayfit.encryptifyauthlib.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import pl.dayfit.encryptifyauthlib.authenticationprovider.JwtAuthenticationProvider;
import pl.dayfit.encryptifyauthlib.configuration.CookieConfigurationProperties;
import pl.dayfit.encryptifyauthlib.token.JwtAuthenticationTokenCandidate;

import java.io.IOException;
import java.util.Arrays;

@Slf4j
@Component
@RequiredArgsConstructor
@EnableConfigurationProperties({CookieConfigurationProperties.class})
public class CookieFilter extends OncePerRequestFilter {
    private final JwtAuthenticationProvider jwtAuthenticationProvider;
    private final CookieConfigurationProperties cookieConfigurationProperties;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException
    {
        SecurityContext context = SecurityContextHolder.getContext();

        Cookie[] cookies = request.getCookies();

        if(cookies == null || cookies.length == 0)
        {
            filterChain.doFilter(request, response);
            return;
        }

        Cookie accessToken = Arrays
                .stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals(cookieConfigurationProperties.getAccessTokenName()))
                .findFirst()
                .orElse(null);

        if (accessToken == null)
        {
            filterChain.doFilter(request, response);
            return;
        }

        String accessTokenValue = accessToken.getValue();

        if (accessTokenValue.isBlank())
        {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            Authentication candidate = new JwtAuthenticationTokenCandidate(accessTokenValue);
            Authentication authentication = jwtAuthenticationProvider.authenticate(candidate);
            context.setAuthentication(authentication);
        } catch (AuthenticationException e) {
            log.debug("Authentication failed: {}", e.getMessage());
        } finally {
            filterChain.doFilter(request,response);
        }
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken))
                || request.getMethod().equals("OPTIONS");
    }
}
