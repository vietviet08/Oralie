package com.oralie.carts;

import com.oralie.carts.dto.CartContactDto;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableConfigurationProperties(CartContactDto.class)
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@OpenAPIDefinition(
        info = @Info(
                title = "Product microservice REST API Documentation",
                description = "Oralie Carts microservice REST API Documentation",
                version = "v1",
                contact = @Contact(
                        name = "Viet Quoc",
                        email = "vietnq23ceb@vku.udn.vn",
                        url = "github.com/vietviet08"
                ),
                license = @License(
                        name = "Apache 2.0",
                        url = "github.com/vietviet08"
                )
        ),
        externalDocs = @ExternalDocumentation(
                description = "Oralie Carts microservice REST API Documentation",
                url = "https://www.oralie.com.vn/swagger-ui.html"
        )
)
public class CartsApplication {

    public static void main(String[] args) {
        SpringApplication.run(CartsApplication.class, args);
    }

}
