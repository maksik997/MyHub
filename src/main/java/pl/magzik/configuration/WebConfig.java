package pl.magzik.configuration;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${game-dir}")
    private String gameDirectory;

    @Override
    public void addResourceHandlers(@NotNull ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/games/**")
            .addResourceLocations("file:" + gameDirectory);

        registry.addResourceHandler("/static/**")
            .addResourceLocations("classpath:/static/");
    }
}
