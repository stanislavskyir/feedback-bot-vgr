package dev.stanislavskyi.feedback_bot_vgr.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;

public class OpenAPIConfig {

    @Bean
    public OpenAPI userServiceAPI(){
        return new OpenAPI()
                .info(new Info().title("User Service API")
                        .description("This is the REST API for User Service")
                        .version("v0.0.1")
                        .license(new License().name("Apache 2.0")))
                .externalDocs(new ExternalDocumentation()
                        .description("description")
                        .url("https://user-service-dummy-url.com/dosc"));
    }
}
