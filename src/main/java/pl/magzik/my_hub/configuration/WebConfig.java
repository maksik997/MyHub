package pl.magzik.my_hub.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author Maksymilian Strzelczak
 * */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${game-dir}")
    private String gameDirectory;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/games/**")
            .addResourceLocations("file:" + gameDirectory);

        registry.addResourceHandler("/static/**")
            .addResourceLocations("classpath:/static/");
    }
}
