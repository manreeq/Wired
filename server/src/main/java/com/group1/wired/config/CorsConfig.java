// When the frontend (running on port 5173) makes a request to the backend (running on port 8080),
// the browser blocks it by default because they are on different ports. this is the CORS policy.
// Without this config, the browser throws: "Access to fetch has been blocked by CORS policy: No 'Access-Control-Allow-Origin' header is present"
// This file tells SpringBoot to  allow requests from our frontend, including cookies (jwt token) so that sessions persist across tabs.

package com.group1.wired.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

//mark this as a configuration class/file that launches on startup
@Configuration
public class CorsConfig {
	
	@Value("${allowed.origins}")
    private String allowedOrigins;
	
	//registers this function as a spring boot managed component that runs on every request
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration configuration = new CorsConfiguration(); 								//new set of CORS rules
        configuration.setAllowCredentials(true);												//allows cookies to be sent with requests
        configuration.setAllowedOrigins(List.of(allowedOrigins));						//only accepts requests from local frontend
        configuration.setAllowedHeaders(List.of("*"));											//allows any headers in the request
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));	//allows only these http methods

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();			//mapper that links url patterns to CORS rules
        source.registerCorsConfiguration("/**", configuration);									//applies cors rules to every endpoint
        return new CorsFilter(source);
    }
}