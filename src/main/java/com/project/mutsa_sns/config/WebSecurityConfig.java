package com.project.mutsa_sns.config;

import com.project.mutsa_sns.jwt.JwtTokenFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;

@Configuration
public class WebSecurityConfig {
    private final JwtTokenFilter jwtTokenFilter;

    public WebSecurityConfig(JwtTokenFilter jwtTokenFilter) {
        this.jwtTokenFilter = jwtTokenFilter;
    }

    // 애플리케이션의 보안 필터와 인증 설정을 구성
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CSRF (Cross-Site Request Forgery) 보호를 비활성화하여 stateless 인증에 맞게 설정
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        authHttp -> authHttp
                                // 인증을 필요로 하지 않는 공개 엔드포인트를 정의
                                .requestMatchers(
                                        "/users/login",
                                        "/users/register",
                                        "/token/issue"
                                )
                                .permitAll()
                                .requestMatchers(
                                        HttpMethod.GET, "/articles"
                                ).permitAll()
                                .requestMatchers(
                                        HttpMethod.GET, "/articles/**"
                                ).permitAll()
                                .requestMatchers(
                                        "/users/update-image",
                                        "/articles",
                                        "/articles/**",
                                        "/comments",
                                        "/comments/**",
                                        "/likes/**"
                                )
                                .authenticated()
                                .anyRequest()
                                .authenticated()
                )
                // 세션 관리를 stateless (JWT 인증)으로 설정
                .sessionManagement(
                        sessionManagement -> sessionManagement
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // 커스텀한 JWT 토큰 필터를 AuthorizationFilter 클래스 앞에 추가
                .addFilterBefore(
                        jwtTokenFilter,
                        AuthorizationFilter.class
                );
        return http.build();
    }

    // 비밀번호 암호화.. 해싱 및 검증에 사용
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
