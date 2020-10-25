package cn.ilubov.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

@Configuration
public class WebViewConfigurer implements WebMvcConfigurer {

    @Bean
    public FreeMarkerViewResolver viewResolver() {
        FreeMarkerViewResolver resolver = new FreeMarkerViewResolver();
        resolver.setAllowRequestOverride(true);
        resolver.setCache(false);
        resolver.setContentType("text/html; charset=UTF-8");
        resolver.setExposeRequestAttributes(true);
        resolver.setExposeSessionAttributes(true);
        resolver.setExposeSpringMacroHelpers(true);
        resolver.setAllowSessionOverride(true);
        resolver.setSuffix(".html");
        resolver.getAttributesMap().put("classic_compatible", true);
        resolver.setRequestContextAttribute("rc");
        return resolver;
    }
}
