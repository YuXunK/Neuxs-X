package org.yuxun.x.nexusx.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.yuxun.x.nexusx.Utils.AESUtil;
import org.yuxun.x.nexusx.Utils.CustomAuthenticationProvider;

@Configuration
public class SecurityConfig {

    private final CustomAuthenticationProvider customAuthenticationProvider;

    @Autowired
    public SecurityConfig(CustomAuthenticationProvider customAuthenticationProvider) {
        this.customAuthenticationProvider = customAuthenticationProvider;
    }

    // 配置身份验证
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // 配置 CORS
                .csrf(AbstractHttpConfigurer::disable) // 根据需求禁用 CSRF
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/user/**").permitAll() // 开放资源
                        .requestMatchers("/admins/**").hasRole("ADMINISTRATOR") // 管理员访问路径
                        .requestMatchers("/managers/**").hasRole("MANAGER") // 管理人员访问路径
                        .requestMatchers("/users/**").hasRole("USER") // 普通用户访问路径
                        .anyRequest().authenticated() // 其他请求需认证
                );

        return http.build(); // 返回 SecurityFilterChain
    }

    // 配置 AuthenticationManager 和 UserDetailsService
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(customAuthenticationProvider);

        return authenticationManagerBuilder.build();
    }

    // 配置密码加密
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new AESUtil(); // 可选择使用 BCrypt 或其他加密方式
    }

    // CORS 配置
    private CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("http://localhost:8080");
        configuration.addAllowedOrigin("http://192.168.0.103:8080");
        configuration.addAllowedMethod("GET");
        configuration.addAllowedMethod("POST");
        configuration.addAllowedMethod("PUT");
        configuration.addAllowedMethod("DELETE");
        configuration.addAllowedMethod("OPTIONS");
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}


