package com.compete.mis.configurers;

import com.compete.mis.interceptors.AuthorizationInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@Configuration
public class SystemConfiguration extends WebMvcConfigurationSupport {

    @Bean
    protected AuthorizationInterceptor createAuthorizationInterceptor() {
        return new AuthorizationInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(createAuthorizationInterceptor()) // 添加权限拦截器。
                .addPathPatterns("/**") // 全部路径全部类型添加。
                .excludePathPatterns("/api/Account/**");    // .excludePathPatterns("/api/versionControl/**").excludePathPatterns("/api/plugin/**").excludePathPatterns("/error*"); // 排除账户目录与错误目录。
        super.addInterceptors(registry);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("**")
                .allowCredentials(true)
                .allowedMethods("GET", "POST", "DELETE", "PUT")
                .maxAge(3600);
    }
}
