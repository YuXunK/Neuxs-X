package org.yuxun.x.nexusx.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.yuxun.x.nexusx.Interceptor.RequestRateLimiterInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final RequestRateLimiterInterceptor rateLimiterInterceptor;

    public WebConfig(RequestRateLimiterInterceptor rateLimiterInterceptor) {
        this.rateLimiterInterceptor = rateLimiterInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(rateLimiterInterceptor)
                .addPathPatterns("/**")  // 拦截所有路径
                .excludePathPatterns(
                        "/api/user/**", // 放行所有 /api/user/ 下的路径
                        "/static/**", // 如果有静态资源路径，可以一并排除
                        "/public/**"  // 如果有其他公共路径，也可以排除
                )
                .excludePathPatterns(
                        "/api/user/register",
                        "/api/user/login",
                        "/api/user/logout"
                );
    }
}

