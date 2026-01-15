package com.example.pizza_backend.config;

import com.example.pizza_backend.auth.interceptor.AdminInterceptor;
import com.example.pizza_backend.auth.interceptor.CustomerInterceptor;
import io.micrometer.common.lang.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final CustomerInterceptor customerInterceptor;
    private final AdminInterceptor adminInterceptor;

    @Autowired
    public WebConfig(@NonNull CustomerInterceptor customerInterceptor, @NonNull AdminInterceptor adminInterceptor) {
        this.customerInterceptor = customerInterceptor;
        this.adminInterceptor = adminInterceptor;
    }

    //Interceptor
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(customerInterceptor)
                .addPathPatterns("/cart/**", "/order/**", "/address/**", "/profile/update", "/profile/list", "/profile/me",
                        "/code/**")
                .excludePathPatterns("/login/**");

        registry.addInterceptor(adminInterceptor)
                .addPathPatterns("/admin/**", "/product/**", "/order/**", "/category/**", "/recommend/**")
                .excludePathPatterns("/product/list","/product/search",
                        "/order/list","/order/create", "/order/","/order/reorder",
                        "/category/list", "/category/");
    }
}

