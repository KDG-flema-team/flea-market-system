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

        final String finalEmail = email;
        final String finalName = name;

        // ユーザーが存在しない場合は作成
        User user = userRepository.findByEmail(email)
            .orElseGet(() -> {
                User newUser = new User();
                newUser.setAuth0Id(oAuth2User.getName()); // Auth0 ID (sub claim) を設定
                newUser.setEmail(finalEmail);
                newUser.setName(finalName);
                newUser.setPassword(passwordEncoder.encode(UUID.randomUUID().toString())); // ランダムパスワード
                newUser.setRole("USER");
                newUser.setRank("bronze");
                newUser.setEnabled(true);
                newUser.setBanned(false);
                return userRepository.save(newUser);
            });

        // OAuth2認証成功後は、商品一覧ページにリダイレクト
        // Spring Securityのセッション管理により、認証情報は自動的に管理される
        getRedirectStrategy().sendRedirect(request, response, "/items");
    }
}
