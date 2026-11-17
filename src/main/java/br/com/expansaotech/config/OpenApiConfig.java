package br.com.expansaotech.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI expansaoTechApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Expansão Tech API")
                        .version("1.0.0")
                        .description("""
                                API de ONGs, usuários, professores, cursos e eventos da Associação Expansão Cultural.
                                Rotas de consulta dos cursos e eventos são públicas. Os demais cadastros exigem HTTP Basic
                                com uma conta Administracao. Em produção, use somente HTTPS.
                                """))
                .components(new Components().addSecuritySchemes("adminBasic",
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("basic")
                                .description("Acesso restrito a usuários do tipo Administracao.")));
    }
}
