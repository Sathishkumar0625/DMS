//package com.proflaut.dms.util;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.CorsRegistry;
//import org.springframework.web.servlet.config.annotation.EnableWebMvc;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@Configuration
//@EnableWebMvc
//public class CorsConfig implements WebMvcConfigurer {
//	@Override
//	public void addCorsMappings(CorsRegistry registry) {
//		registry.addMapping("/api/**") // Specify the URL pattern for which CORS should be configured
//				.allowedOrigins("http://8080") // Specify your frontend domain
//				.allowedMethods("GET", "POST", "PUT", "DELETE") // Specify allowed HTTP methods
//				.allowCredentials(true).maxAge(3600); // Set the maximum age of the CORS preflight request
//	}
//}
