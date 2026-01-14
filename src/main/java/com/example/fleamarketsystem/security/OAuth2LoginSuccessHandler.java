package com.example.fleamarketsystem.security;

import com.example.fleamarketsystem.entity.User;
import com.example.fleamarketsystem.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        
        if (email == null) {
            email = oAuth2User.getAttribute("login") + "@github.com"; // GitHub fallback
        }
        if (name == null) {
            name = oAuth2User.getAttribute("login"); // GitHub fallback
        }

        // ユーザーが存在しない場合は作成
        User user = userRepository.findByEmail(email)
            .orElseGet(() -> {
                User newUser = new User();
                newUser.setEmail(email);
                newUser.setName(name);
                newUser.setPassword(passwordEncoder.encode(UUID.randomUUID().toString())); // ランダムパスワード
                newUser.setRole("USER");
                newUser.setRank("bronze");
                newUser.setEnabled(true);
                newUser.setBanned(false);
                return userRepository.save(newUser);
            });

        // JWT生成（UserPrincipalを使用）
        UserPrincipal userPrincipal = UserPrincipal.create(user);
        org.springframework.security.authentication.UsernamePasswordAuthenticationToken authToken = 
            new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                userPrincipal, null, userPrincipal.getAuthorities());
        
        String jwt = tokenProvider.generateToken(authToken);

        // JWTをクエリパラメータとしてフロントエンドにリダイレクト
        // 本番環境では、より安全な方法（例：HttpOnlyクッキー）を使用することを推奨
        getRedirectStrategy().sendRedirect(request, response, 
            "http://localhost:3000/oauth2/redirect?token=" + jwt);
    }
}
