package net.developerpass.polimi;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.security.SecuritySchemes;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.TimeZone;

@EnableWebMvc
@EnableConfigurationProperties
@OpenAPIDefinition(
		info = @Info(
				title="Polimi API",
				version = "1.0.0"
		)
)
@SecuritySchemes({
		@SecurityScheme(
				name = "JWT_Student",
				description = "JWT authentication for Student with bearer token",
				type = SecuritySchemeType.HTTP,
				scheme = "bearer",
				bearerFormat = "Bearer [token]"),
		@SecurityScheme(
				name = "JWT_Professor",
				description = "JWT authentication for Professor with bearer token",
				type = SecuritySchemeType.HTTP,
				scheme = "bearer",
				bearerFormat = "Bearer [token]")
})
@SpringBootApplication(exclude = ErrorMvcAutoConfiguration.class)
public class PolimiApplication {

	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone("Europe/Rome"));
		SpringApplication.run(PolimiApplication.class, args);
	}

}
