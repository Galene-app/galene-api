package br.com.galene.api.config.shared;

import br.com.galene.api.common.constants.ApiPaths;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.SpecVersion;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDocConfig {

    public static final String BEARER_KEY = "bearer-key";

    private static final String BEARER_SCHEME = "bearer";
    private static final String BEARER_FORMAT = "JWT";
    private static final String TITLE = "Galene API";
    private static final String DESCRIPTION = "Galene Api Rest: bem-estar sonoro com medição aproximada de ruído "
            + "(não substitui equipamento calibrado).";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes(BEARER_KEY,
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme(BEARER_SCHEME)
                                        .bearerFormat(BEARER_FORMAT)))
                .specVersion(SpecVersion.V31)
                .info(new Info()
                        .title(TITLE)
                        .version(ApiPaths.VERSION)
                        .description(DESCRIPTION));
    }
}
