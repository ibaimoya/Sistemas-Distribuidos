package es.ubu.lsi.web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración de recursos estáticos para la aplicación web.
 * Permite servir archivos estáticos desde el directorio 'static' en el classpath.
 * 
 * @author Ibai Moya Aroz
 * 
 * @version 1.0
 * @since 1.0
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Configura los manejadores de recursos para servir archivos estáticos.
     * 
     * @param registry el registro de manejadores de recursos
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .setCachePeriod(0);
    }
}