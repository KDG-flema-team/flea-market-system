package com.example.fleamarketsystem.config;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
	
	/*
	@Bean
	public PasswordEncoder passwordEncoder() {
		// {bcrypt},{noop} など委譲エンコーダ
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}
	*/

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				// REST なので CSRF 無効
        		.csrf(csrf -> csrf.disable())
        		
        		// セッションを使わない
                .sessionManagement(sm ->
                    sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
        		
				.authorizeHttpRequests(auth -> auth
						/*
						.requestMatchers(
								"/login",
								"/css/**", "/js/**", "/images/**", "/webjars/**")
						.permitAll()
						*/
						
						/* API v1 テスト用JWT無視 */
						.requestMatchers("/error").permitAll()
						.requestMatchers("/api/v1/items/**").permitAll()
						.requestMatchers("/api/v1/auth/**").permitAll()
						.requestMatchers("/api/v1/admin/users/**").permitAll()
						
						.requestMatchers("/api/v1/orders/**").authenticated()
						
						.requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
						.requestMatchers("/api/v1/**").authenticated()
						)
				// Basic 認証を Lambda で全体に適用
		        .httpBasic(httpBasic -> httpBasic
		            .authenticationEntryPoint((request, response, authException) -> {
		                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		                response.setContentType("application/json");
		                response.getWriter().write("{\"error\":\"unauthorized\"}");
		            })
		        )
						
						/* Thymeleaf */
						/*
						.requestMatchers("/admin/**").hasRole("ADMIN")
						.anyRequest().authenticated())
						*/
				.formLogin(form -> form.disable())
				.logout(logout -> logout.disable())
				
				/*
				.formLogin(form -> form
						.loginPage("/login")
						.defaultSuccessUrl("/items", true) // ログイン成功後
						.permitAll())
				.logout(logout -> logout
						.logoutUrl("/logout") // POST /logout
						.logoutSuccessUrl("/login?logout")
						.permitAll())
				*/
				
				/*
				// CSRF の設定（基本有効、Stripe Webhook のみ除外）
		        .csrf(csrf -> csrf
		            // Ant パターンで Webhook を除外
		            .ignoringRequestMatchers("/orders/stripe-webhook"));
		        */
				.exceptionHandling(ex -> ex
						.authenticationEntryPoint((req, res, e) -> {
							res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
							res.setContentType("application/json");
							res.getWriter().write("""
									    {"error":"unauthorized"}
									       """);
						})
				);

		return http.build();
	}
}


