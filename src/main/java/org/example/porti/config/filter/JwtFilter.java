package org.example.porti.config.filter;

import io.jsonwebtoken.ExpiredJwtException;
import org.example.porti.common.exception.BaseException;
import org.example.porti.common.exception.GlobalExceptionHandler;
import org.example.porti.common.model.BaseResponseStatus;
import org.example.porti.user.model.AuthUserDetails;
import org.example.porti.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Component
public class JwtFilter extends OncePerRequestFilter {
    private final HandlerExceptionResolver handlerExceptionResolver;
    private final JwtUtil jwtUtil;
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();

        return path.startsWith("/user/login") ||
                path.startsWith("/user/signup") ||
                path.startsWith("/user/verify");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals("ATOKEN")) {
                    try {
                        // JwtUtil에서 토큰 생성 및 확인하도록 리팩토링
                        Long idx = jwtUtil.getUserIdx(cookie.getValue());
                        String username = jwtUtil.getUsername(cookie.getValue());
                        String role = jwtUtil.getRole(cookie.getValue());

                        AuthUserDetails user = AuthUserDetails.builder()
                                .idx(idx)
                                .username(username)
                                .role(role)
                                .build();

                        Authentication authentication = new UsernamePasswordAuthenticationToken(
                                user,
                                null,
                                List.of(new SimpleGrantedAuthority(role))
                        );
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                    catch (ExpiredJwtException e) {
                        handlerExceptionResolver.resolveException(request, response, null,
                                new BaseException(BaseResponseStatus.JWT_EXPIRED));
                        return;
                    }catch (Exception e) {
                        handlerExceptionResolver.resolveException(request, response, null, e);
                        return;
                    }
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}