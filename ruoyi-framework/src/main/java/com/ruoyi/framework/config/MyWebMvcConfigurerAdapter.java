package com.ruoyi.framework.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.ruoyi.common.config.RuoYiConfig;


@Configuration
public class MyWebMvcConfigurerAdapter extends WebMvcConfigurerAdapter {
    /**
     * 配置静态访问资源
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/tmpFile/**").addResourceLocations("file:"+RuoYiConfig.getTmpFile());
        registry.addResourceHandler("/appStoreFile/**").addResourceLocations("file:"+RuoYiConfig.getAppStoreFile());
        super.addResourceHandlers(registry);
    }
}