package com.example.football_manager.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
		info = @Info(
				title = "Football Manager API",
				version = "v1",
				description = "REST API for managing teams, matches, competitions, countries, and user authentication."
		),
		tags = {
				@Tag(name = "Auth", description = "User registration and login"),
				@Tag(name = "Teams", description = "Create, update, search, and delete teams"),
				@Tag(name = "Matches", description = "Schedule matches and register results"),
				@Tag(name = "Competitions", description = "Manage competitions"),
				@Tag(name = "Countries", description = "Manage countries"),
				@Tag(name = "Favourites", description = "Manage the current user's favourite teams")
		}
)
@Configuration
public class OpenApiConfig {
}

