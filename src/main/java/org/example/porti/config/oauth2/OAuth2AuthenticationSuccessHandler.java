package org.example.porti.config.oauth2;


import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.porti.user.model.AuthUserDetails;
import org.example.porti.utils.JwtUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        AuthUserDetails userDetails = (AuthUserDetails) authentication.getPrincipal();

        String jwt = jwtUtil.createToken(userDetails.getIdx(), userDetails.getName(), userDetails.getRole(), userDetails.getNickname());
        response.addHeader("Set-cookie", "ATOKEN="+ jwt+"; Path=/");
        String redirectUrl = "http://localhost:5173/";
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}
