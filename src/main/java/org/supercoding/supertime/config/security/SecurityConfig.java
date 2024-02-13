package org.supercoding.supertime.config.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.supercoding.supertime.config.security.filter.JwtAuthorizationFilter;
import org.supercoding.supertime.service.user.CustomOAuth2UserService;

import java.util.List;
import java.util.Locale;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {
    /*
     * 시큐리티 설정 파일
     */
    private final TokenProvider tokenProvider;

    private final CustomOAuth2UserService customOAuth2UserService;

    // 모든 유저가 접근 가능(인증X)
    private final String[] PERMIT_URL = {
            "/",
            "/auth/**",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/api-docs/**",
            "/v3/**",
            "/login/**",
            "/oauth2/**",
            "/semester/**"
    };

    // 관리자만 접근 가능
    private final String[] ADMIN_URL = {
            "/admin/**"
    };

    private final String[] AUTHENTICATION_URL = {
            "/api/**",
            "/board/**",
            "/semester/**",
            "/user/**"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // cors
        http
                .cors((cors) -> cors.configurationSource(corsConfigurationSource()));

        // csrf 비활
        http
                .csrf((auth) -> auth.disable());

        // formLogin 비활
        http
                .formLogin((auth) -> auth.disable());

        // basic인증 방식 비활
        http
                .httpBasic((auth) -> auth.disable());

        // RemeberMe 비활
        http
                .rememberMe((remember) -> remember.disable());

        // 요청 인가
        http
                .authorizeHttpRequests((auth)->
                        auth
                                .requestMatchers(PERMIT_URL).permitAll()
                                .requestMatchers(ADMIN_URL).hasRole("ADMIN")
                                .requestMatchers(AUTHENTICATION_URL).hasRole("ADMIN")
                                .requestMatchers(AUTHENTICATION_URL).hasRole("USER")
                                .anyRequest().hasRole("ADMIN")
                );

        http
                .oauth2Login(oauth2Login ->
                        oauth2Login.userInfoEndpoint(userInfoEndpointConfig ->
                                userInfoEndpointConfig.userService(customOAuth2UserService)));

        // 세션 설정
        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http
                .exceptionHandling((exception) -> exception
                        .authenticationEntryPoint(new CustomAuthenticationEntryPoint(tokenProvider)));

        http
                .exceptionHandling((exception) -> exception
                        .accessDeniedHandler(new CustomAccessDeniedHandler()));


        // jwt filter
        http
                .addFilterBefore(new JwtAuthorizationFilter(tokenProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    @Bean
    //모든 CORS Block 방지 커스텀
    public CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration configuration = new CorsConfiguration();

        //추가
//        configuration.setAllowCredentials(true);
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
