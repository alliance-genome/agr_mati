package org.alliancegenome.mati.configuration;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import org.eclipse.microprofile.openapi.annotations.Components;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;

/** Main class with openapi specification */
@ApplicationPath("/api")
@OpenAPIDefinition(
    info = @Info(
        title = "Alliance of Genome Resources MaTI API",
        description = "Minting and Tracking Identifiers - MaTI",
        version = ""
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
