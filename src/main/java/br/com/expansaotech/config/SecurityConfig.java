package br.com.expansaotech.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import br.com.expansaotech.service.LoginAttemptService;
import br.com.expansaotech.domain.Usuario;

@Configuration
public class SecurityConfig {

    @Bean
    @Order(1)
    SecurityFilterChain apiSecurity(HttpSecurity http) throws Exception {
        return http
                .securityMatcher("/api", "/api/**")
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.POST,
                                "/api/contas", "/api/contas/ativacao",
                                "/api/dados/contas", "/api/dados/contas/ativacao")
                        .permitAll()
                        .requestMatchers(HttpMethod.PUT,
                                "/api/contas/perfil", "/api/dados/contas/perfil")
                        .authenticated()
                        .requestMatchers(HttpMethod.GET,
                                "/api", "/api/dados", "/api/cursos", "/api/eventos",
                                "/api/dados/cursos", "/api/dados/eventos")
                        .permitAll()
                        .requestMatchers(HttpMethod.POST,
                                "/api/cursos/*/inscricao", "/api/eventos/*/inscricao",
                                "/api/dados/cursos/*/inscricao", "/api/dados/eventos/*/inscricao")
                        .authenticated()
                        .anyRequest().hasRole("ADMIN"))
                .httpBasic(Customizer.withDefaults())
                .headers(headers -> headers
                        .contentSecurityPolicy(csp -> csp.policyDirectives(
                                "default-src 'none'; frame-ancestors 'none'; base-uri 'none'")))
                .build();
    }

    @Bean
    @Order(2)
    SecurityFilterChain webSecurity(HttpSecurity http, LoginAttemptService loginAttemptService) throws Exception {
        AuthenticationFailureHandler failureHandler = (request, response, exception) -> {
            if (!(exception instanceof LockedException)) {
                loginAttemptService.registrarFalha(request.getParameter("username"));
            }
            response.sendRedirect("/login?" + (exception instanceof LockedException ? "locked" : "error"));
        };
        AuthenticationSuccessHandler successHandler = (request, response, authentication) -> {
            String email = authentication.getName();
            loginAttemptService.registrarSucesso(email);
            Object principal = authentication.getPrincipal();
            if (principal instanceof Usuario usuario && !usuario.isPerfilCompleto()) {
                response.sendRedirect("/perfil");
            } else if (authentication.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"))) {
                response.sendRedirect("/admin");
            } else {
                response.sendRedirect("/");
            }
        };
        return http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/", "/cursos", "/eventos", "/login",
                                "/cadastro", "/ativar-conta", "/esqueci-senha", "/redefinir-senha",
                                "/css/**", "/img/**")
                        .permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/v3/api-docs.yaml").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler(successHandler)
                        .failureHandler(failureHandler)
                        .permitAll())
                .logout(logout -> logout.logoutSuccessUrl("/"))
                .headers(headers -> headers
                        .referrerPolicy(referrer -> referrer.policy(
                                ReferrerPolicyHeaderWriter.ReferrerPolicy.NO_REFERRER))
                        .contentSecurityPolicy(csp -> csp.policyDirectives(
                                "default-src 'self'; img-src 'self' https: data:; style-src 'self'; "
                                        + "form-action 'self'; frame-ancestors 'none'; base-uri 'self'"))
                        .frameOptions(frame -> frame.deny()))
                .build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
