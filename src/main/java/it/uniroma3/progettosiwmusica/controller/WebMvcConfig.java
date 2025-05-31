package it.uniroma3.progettosiwmusica.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${upload.audio.dir}")
    private String uploadDir;

    @Value("${upload.audio.url}")
    private String uploadUrl;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Mappa l'URL configurato alla directory fisica
        registry.addResourceHandler("/audio-files/**")
                .addResourceLocations("file:C:/Users/Gabriele/Desktop/uploads/audio-files/");
    }
}