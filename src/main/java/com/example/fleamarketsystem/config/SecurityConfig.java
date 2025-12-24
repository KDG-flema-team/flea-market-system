package com.example.fleamarketsystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		/*
		 * This is where we configure the security required for our endpoints and setup
		 * our app to serve as
		 * an OAuth2 Resource Server, using JWT validation.
		 */
		return http
				.authorizeHttpRequests((authorize) -> authorize
						.requestMatchers("/api/public").permitAll()
						.requestMatchers("/api/private").authenticated()
						.requestMatchers("/api/private-scoped").hasAuthority("SCOPE_read:messages"))
						// ここをいじって認可設定を行う
				.cors(withDefaults())
				.oauth2ResourceServer(oauth2 -> oauth2
						.jwt(withDefaults()))
				.build();
	}

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
				.csrf(Customizer.withDefaults());

		return http.build();
	}
}
