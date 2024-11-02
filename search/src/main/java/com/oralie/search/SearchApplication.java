package com.oralie.search;

import com.oralie.search.dto.SearchContactDto;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication

@EnableConfigurationProperties(SearchContactDto.class)
@OpenAPIDefinition(
        info = @Info(
                title = "Search microservice REST API Documentation",
                description = "Oralie Search microservice REST API Documentation",
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
                description = "Oralie Search microservice REST API Documentation",
                url = "https://www.oralie.com.vn/swagger-ui.html"
        )
)
public class SearchApplication {

    public static void main(String[] args) {
        SpringApplication.run(SearchApplication.class, args);
    }

}
