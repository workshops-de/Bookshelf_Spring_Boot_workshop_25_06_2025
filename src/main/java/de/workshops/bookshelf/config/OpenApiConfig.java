package de.workshops.bookshelf.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class OpenApiConfig {

  @Bean
  @ConditionalOnBooleanProperty("application.custom-open-api-config.enabled")
  public OpenAPI api() {
    return new OpenAPI()
        .info(
            new Info()
                .title("Bookshelf API")
                .version("v0.0.1")
                .license(new License()
                    .name("MIT License")
                    .url("https://opensource.org/licenses/MIT")
                )
        );
  }
}
