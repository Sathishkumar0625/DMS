package com.proflaut.dms.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Component
public class Serviceinterceptor extends WebMvcConfigurerAdapter{
	
	 @Autowired
     LogInterceptor logInterceptor;

     @Override
     public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(logInterceptor);
     }

}
