package pl.dayfit.encryptifyauthlib.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import pl.dayfit.encryptifyauthlib.entrypoint.EncryptifyAuthenticationEntrypoint;
import pl.dayfit.encryptifyauthlib.filter.CookieFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableConfigurationProperties({SecurityConfigurationProperties.class})
public class SecurityConfiguration {
    private final SecurityConfigurationProperties securityConfigurationProperties;
    private final CorsConfigurationSource corsConfigurationSource;
    private final EncryptifyAuthenticationEntrypoint authenticationEntrypoint;
    private final CookieFilter cookieFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception
    {
        List<String> securedEndpoints = securityConfigurationProperties.getSecuredEndpoints();
        securedEndpoints = securedEndpoints == null ? List.of() : securedEndpoints;

        List<String> finalSecuredEndpoints = securedEndpoints;
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .authorizeHttpRequests(request ->
                        request
                                .requestMatchers(finalSecuredEndpoints.toArray(new String[0])).authenticated()
                                .anyRequest().permitAll()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(handler -> handler.authenticationEntryPoint(authenticationEntrypoint))
                .addFilterBefore(cookieFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource()
    {
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        corsConfiguration.setAllowedOriginPatterns(securityConfigurationProperties.getAllowedOriginsPatterns());
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");

        corsConfiguration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);

        return source;
    }
}
