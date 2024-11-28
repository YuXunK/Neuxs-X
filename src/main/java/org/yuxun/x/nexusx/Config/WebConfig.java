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
                .addPathPatterns("/**")  // 所有路径都进行拦截
                .excludePathPatterns("/login", "/register"); // 登录、注册等无需验证的接口
    }
}
