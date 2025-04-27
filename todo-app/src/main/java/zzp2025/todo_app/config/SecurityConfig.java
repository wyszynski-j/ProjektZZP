package zzp2025.todo_app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain h2ConsoleSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .securityMatcher(AntPathRequestMatcher.antMatcher("/h2-console/**"))
                .authorizeHttpRequests( auth -> {
                    auth.requestMatchers(AntPathRequestMatcher.antMatcher("/h2-console/**")).permitAll();
                })
                .csrf(csrf -> csrf.ignoringRequestMatchers(AntPathRequestMatcher.antMatcher("/h2-console/**")))
                .headers(headers -> headers.disable())
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain allowRegistration(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())  // Wyłączenie CSRF
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/api/auth/register").permitAll()  // Pozwolenie na dostęp do rejestracji
                        .anyRequest().authenticated()  // Pozostałe żądania wymagają autoryzacji
                )
                .formLogin(form -> form.disable())  // Wyłączenie formularza logowania
                .httpBasic(basic -> basic.disable());  // Wyłączenie HTTP Basic Authentication

        return http.build();
    }
}
