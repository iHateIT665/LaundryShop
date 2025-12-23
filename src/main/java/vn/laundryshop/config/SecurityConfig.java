package vn.laundryshop.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import vn.laundryshop.service.impl.CustomUserDetailsService; 

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // 1. Tiêm Service xịn vào đây (Thay vì tự new)
    @Autowired
    @Lazy // Thêm @Lazy để tránh lỗi vòng lặp dependency (nếu có)
    private CustomUserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Tạm thời chưa mã hóa pass (để test cho dễ)
        return NoOpPasswordEncoder.getInstance(); 
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        
        // 2. Dùng biến userDetailsService đã được Spring inject ở trên
        authProvider.setUserDetailsService(userDetailsService); 
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/css/**", "/js/**", "/images/**", "/assets/**").permitAll()
                .requestMatchers("/register", "/login", "/home", "/forgot-password", "/").permitAll()
                .requestMatchers("/logout").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/do-login")
                .defaultSuccessUrl("/waiting", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .rememberMe(remember -> remember
                .key("uniqueAndSecretKey")
                .tokenValiditySeconds(86400)
                .rememberMeParameter("remember-me")
                // 3. Chỗ này cũng sửa lại dùng biến inject
                .userDetailsService(userDetailsService)
            )
            .logout(logout -> logout
                .logoutUrl("/security-logout") 
                .permitAll()
            );

        return http.build();
    }
}