package com.example.fleamarketsystem.config;

import com.example.fleamarketsystem.security.JwtAuthenticationFilter;
import com.example.fleamarketsystem.security.OAuth2LoginSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

	@Autowired
	@Lazy
	private OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

	@Bean
	public JwtAuthenticationFilter jwtAuthenticationFilter() {
		return new JwtAuthenticationFilter();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
		return authConfig.getAuthenticationManager();
	}

	/*
	 * @Bean
	 * public PasswordEncoder passwordEncoder() {
	 * // {bcrypt},{noop} など委譲エンコーダ
	 * return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	 * }
	 */

	// REST APIのセキュリティフィルタチェーン
	@Bean
	@Order(1)
	public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
		http
				.securityMatcher("/api/**")
		// .authorizeHttpRequests(auth -> auth
		// .requestMatchers("/api/auth/**").permitAll()
		// .requestMatchers("/api/admin/**").hasRole("ADMIN")
		// .anyRequest().authenticated())
		// .sessionManagement(session -> session
		// .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
		// .csrf(AbstractHttpConfigurer::disable)
		// .cors(withDefaults())
		// .addFilterBefore(jwtAuthenticationFilter(),
		// UsernamePasswordAuthenticationFilter.class)
		;

		return http.build();
	}

	// 従来のウェブUIのセキュリティフィルタチェーン（OAuth2対応）
	@Bean
	@Order(2)
	public SecurityFilterChain webFilterChain(HttpSecurity http) throws Exception {
		http
				.securityMatcher("/**", "!/api/**")

				// REST なので CSRF 無効
				.csrf(csrf -> csrf.disable())

				// セッションを使わない
				.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

				.authorizeHttpRequests(auth -> auth
						/*
						 * .requestMatchers(
						 * "/login",
						 * "/login/oauth2/code/**",
						 * "/oauth2/**",
						 * "/css/**", "/js/**", "/images/**", "/webjars/**")
						 * .permitAll()
						 */

						/* API v1 テスト用JWT無視 */
						.requestMatchers("/api/v1/items/**").permitAll()
						.requestMatchers("/api/v1/auth/**").permitAll()

						.requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
						.requestMatchers("/api/v1/**").authenticated())
				// Basic 認証を Lambda で全体に適用
				.httpBasic(httpBasic -> httpBasic
						.authenticationEntryPoint((request, response, authException) -> {
							response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
							response.setContentType("application/json");
							response.getWriter().write("{\"error\":\"unauthorized\"}");
						}))

				/* Thymeleaf */
				/*
				 * .requestMatchers("/admin/**").hasRole("ADMIN")
				 * .anyRequest().authenticated())
				 */
				.formLogin(form -> form.disable())
				.logout(logout -> logout.disable())

				/*
				 * .formLogin(form -> form
				 * .loginPage("/login")
				 * .defaultSuccessUrl("/items", true)
				 * .permitAll())
				 * .oauth2Login(oauth2 -> oauth2
				 * .loginPage("/login")
				 * .successHandler(oAuth2LoginSuccessHandler))
				 * .logout(logout -> logout
				 * .logoutUrl("/logout")
				 * .logoutSuccessUrl("/login?logout")
				 * .permitAll())
				 * 
				 * .csrf(Customizer.withDefaults())
				 * ;
				 */

				/*
				 * // CSRF の設定（基本有効、Stripe Webhook のみ除外）
				 * .csrf(csrf -> csrf
				 * // Ant パターンで Webhook を除外
				 * .ignoringRequestMatchers("/orders/stripe-webhook"));
				 */
				.exceptionHandling(ex -> ex
						.authenticationEntryPoint((req, res, e) -> {
							res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
							res.setContentType("application/json");
							res.getWriter().write("""
									{"error":"unauthorized"}
									   """);
						}));

		return http.build();
	}
}
