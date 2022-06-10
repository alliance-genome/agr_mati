package org.alliancegenome.mati.configuration;

import org.eclipse.microprofile.openapi.annotations.Components;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/api")
@OpenAPIDefinition(
    info = @Info(
        title = "Alliance of Genome Resources MaTI API",
        description = "Minting and Tracking Identifiers - MaTI",
        version = "1.0 Alpha"
    ),
    security = {
        @SecurityRequirement(name = "apiKey")
    },
    components = @Components(
        securitySchemes = {
          @SecurityScheme(securitySchemeName = "apiKey",
            type = SecuritySchemeType.HTTP,
            scheme = "Bearer")
        }
    )
)
public class RestApplication extends Application {
}
