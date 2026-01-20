package com.example.fleamarketsystem.config;

import com.example.fleamarketsystem.security.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

	@Bean
	public JwtAuthenticationFilter jwtAuthenticationFilter() {
		return new JwtAuthenticationFilter();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
		return authConfig.getAuthenticationManager();
	}

	// REST APIのセキュリティフィルタチェーン
	@Bean
	@Order(1)
	public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
		http
				.securityMatcher("/api/**")
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/api/auth/**").permitAll()
						.requestMatchers("/api/admin/**").hasRole("ADMIN")
						.anyRequest().authenticated())
				.sessionManagement(session -> session
						.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.csrf(AbstractHttpConfigurer::disable)
				.cors(withDefaults())
				.addFilterBefore(jwtAuthenticationFilter(),
						UsernamePasswordAuthenticationFilter.class)
				.exceptionHandling(ex -> ex
						.authenticationEntryPoint((req, res, e) -> {
							res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
							res.setContentType("application/json");
							res.getWriter().write("{\"error\":\"unauthorized\"}");
						}));

		return http.build();
	}

	// 従来のウェブUIのセキュリティフィルタチェーン（OAuth2対応）
	@Bean
	@Order(2)
	public SecurityFilterChain webFilterChain(HttpSecurity http) throws Exception {
		http
				.securityMatcher("/**", "!/api/**")
				.csrf(csrf -> csrf.disable())
				.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/api/v1/items/**").permitAll()
						.requestMatchers("/api/v1/auth/**").permitAll()
						.requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
						.requestMatchers("/api/v1/**").authenticated()
						.anyRequest().permitAll())
				.httpBasic(httpBasic -> httpBasic
						.authenticationEntryPoint((request, response, authException) -> {
							response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
							response.setContentType("application/json");
							response.getWriter().write("{\"error\":\"unauthorized\"}");
						}))
				.formLogin(form -> form.disable())
				.logout(logout -> logout.disable())
				.exceptionHandling(ex -> ex
						.authenticationEntryPoint((req, res, e) -> {
							res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
							res.setContentType("application/json");
							res.getWriter().write("{\"error\":\"unauthorized\"}");
						}));

		return http.build();
	}
}
