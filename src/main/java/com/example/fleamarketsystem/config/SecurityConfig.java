package com.example.fleamarketsystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

	@Bean
	public PasswordEncoder passwordEncoder() {
		// {bcrypt},{noop} など委譲エンコーダ
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.authorizeHttpRequests(auth -> auth
						.requestMatchers(
								"/login",
								"/css/**", "/js/**", "/images/**", "/webjars/**")
						.permitAll()
						
						/* API v1 */
						.requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
						.requestMatchers("/api/v1/**").authenticated()
						
						/* Thymeleaf */
						.requestMatchers("/admin/**").hasRole("ADMIN")
						.anyRequest().authenticated())
				.formLogin(form -> form
						.loginPage("/login")
						.defaultSuccessUrl("/items", true) // ログイン成功後
						.permitAll())
				.logout(logout -> logout
						.logoutUrl("/logout") // POST /logout
						.logoutSuccessUrl("/login?logout")
						.permitAll())
				// CSRF の設定（基本有効、Stripe Webhook のみ除外）
		        .csrf(csrf -> csrf
		            // Ant パターンで Webhook を除外
		            .ignoringRequestMatchers("/orders/stripe-webhook"));

		return http.build();
	}
}


