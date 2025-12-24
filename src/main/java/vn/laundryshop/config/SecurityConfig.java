package vn.laundryshop.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.core.authority.AuthorityUtils;
import java.util.Set;

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
		// Sử dụng NoOpPasswordEncoder để không mã hóa mật khẩu
		// (Chỉ dùng cho test/học tập, không dùng cho sản phẩm thật)
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
		http.csrf(csrf -> csrf.disable()).authorizeHttpRequests(auth -> auth
				// THÊM "/uploads/**" VÀO DÒNG DƯỚI ĐÂY 👇
				.requestMatchers("/css/**", "/js/**", "/images/**", "/assets/**", "/uploads/**").permitAll()
				.requestMatchers("/register", "/login", "/home", "/forgot-password", "/reset_password").permitAll()
				.requestMatchers("/logout").permitAll().anyRequest().authenticated())
				.formLogin(
						form -> form.loginPage("/login").loginProcessingUrl("/do-login").usernameParameter("username")
								// --- BẮT ĐẦU SỬA ĐỔI ---
								// Xóa dòng .defaultSuccessUrl("/", true) cũ đi và thay bằng đoạn này:
								.successHandler((request, response, authentication) -> {
									Set<String> roles = AuthorityUtils
											.authorityListToSet(authentication.getAuthorities());

									if (roles.contains("ROLE_ADMIN")) {
										response.sendRedirect("/admin/dashboard");
									} else if (roles.contains("ROLE_STAFF")) {
										response.sendRedirect("/staff/home");
									} else {
										response.sendRedirect("/"); // Mặc định về trang chủ Client
									}
								})
								// --- KẾT THÚC SỬA ĐỔI ---
								.permitAll())
				.rememberMe(remember -> remember.key("uniqueAndSecretKey").tokenValiditySeconds(86400)
						.rememberMeParameter("remember-me").userDetailsService(userDetailsService))
				.logout(logout -> logout.logoutUrl("/security-logout").permitAll());

		return http.build();
	}
}