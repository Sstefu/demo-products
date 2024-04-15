package org.assignment.demoproducts.config;

import org.assignment.demoproducts.filters.CustomAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by sstefan
 * Date: 4/14/2024
 * Project: demo-products
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final AuthenticationEntryPoint authenticationEntryPoint;

    private final AccessDeniedHandler deniedHandler;


    public SecurityConfig(BCryptPasswordEncoder bCryptPasswordEncoder,
                          @Qualifier("customAuthenticationEntryPoint") AuthenticationEntryPoint authenticationEntryPoint,
                          @Qualifier("customDeniedHandler") AccessDeniedHandler deniedHandler) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.deniedHandler = deniedHandler;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizationManagerRequestMatcherRegistry ->{
                    authorizationManagerRequestMatcherRegistry.anyRequest().authenticated();
                })
                .httpBasic(basic -> {
                    basic.authenticationEntryPoint(authenticationEntryPoint);
                })
                .exceptionHandling(ex -> ex.accessDeniedHandler(deniedHandler))
                .sessionManagement( httpSecuritySessionManagementConfigurer ->
                        httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilter(new CustomAuthenticationFilter(userDetailsService(),bCryptPasswordEncoder));
        return http.build();
    }

    @Bean
    public HttpFirewall httpFirewall() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowedHttpMethods(List.of("GET", "PUT", "POST", "PATCH", "DELETE", "OPTION"));
        return firewall;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails adminUser = User.builder()
                .username("admin")
                .password(bCryptPasswordEncoder.encode("admin"))
                .authorities("ROLE_USER","ROLE_ADMIN")
                .build();

        UserDetails normalUser = User.builder()
                .username("user")
                .password(bCryptPasswordEncoder.encode("user"))
                .authorities("ROLE_USER")
                .build();
        return new InMemoryUserDetailsManager(adminUser,normalUser);
    }
}
