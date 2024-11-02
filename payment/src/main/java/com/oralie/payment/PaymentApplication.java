package com.oralie.payment;

import com.oralie.payment.dto.PaymentContactDto;
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
@EnableConfigurationProperties(PaymentContactDto.class)
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@OpenAPIDefinition(
        info = @Info(
                title = "Payment microservice REST API Documentation",
                description = "Oralie Payment microservice REST API Documentation",
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
                description = "Oralie Payment microservice REST API Documentation",
                url = "https://www.oralie.com.vn/swagger-ui.html"
        )
)
public class PaymentApplication {
    public static void main(String[] args) {
        SpringApplication.run(PaymentApplication.class, args);
    }

}
