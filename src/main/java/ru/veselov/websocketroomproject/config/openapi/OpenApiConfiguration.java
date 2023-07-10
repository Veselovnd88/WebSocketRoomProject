package ru.veselov.websocketroomproject.config.openapi;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfiguration {
    @Bean
    public OpenAPI openAPI() {
        Server localServer = new Server();
        localServer.url("http://localhost:9990").description("Local ENV");
        return new OpenAPI().addSecurityItem(
                        new SecurityRequirement()
                                .addList("Bearer Authentication"))
                .components(new Components().addSecuritySchemes("Bearer Authentication", createAPIKeyScheme()))
                .info(new Info().title("Harvex Room API")
                        .description("Room API")
                        .version("1.0").contact(new Contact().name("Veselov Nikolay").email("veselovnd@gmail.com"))
                        .license(new License().name("Apache 2.0").url("www.springdoc.com")))
                .servers(List.of(localServer));
    }

    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme().type(SecurityScheme.Type.HTTP)
                .description("Bearer jwt authentication")
                .bearerFormat("Jwt")
                .scheme("Bearer")
                .in(SecurityScheme.In.HEADER);
    }

}
