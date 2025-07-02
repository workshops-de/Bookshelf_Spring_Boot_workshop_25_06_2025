package de.workshops.bookshelf.config;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("application")
@Getter
@Setter
public class BookshelfApplicationProperties {

  private String title;

  private String version;

  private CustomOpenApiConfig customOpenApiConfig;

  private Map<String, BookshelfUser> credentials;

  @Setter
  @Getter
  public static class CustomOpenApiConfig {

    private boolean enabled;
  }
}
