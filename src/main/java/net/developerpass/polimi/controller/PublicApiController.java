package net.developerpass.polimi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.developerpass.polimi.configuration.error.ErrorResponse;
import net.developerpass.polimi.service.PublicService;
import net.developerpass.polimi.utils.object.RegisterGeneric;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@RequestMapping("api")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Tag(name = "Public", description = "The Public API")
public class PublicApiController {

	private final PublicService publicService;

	@PostMapping(path = "/register/student")
	@Operation(summary = "Student Register")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation", content = @Content(mediaType = "text/plain")),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
	})
	public String register(
			@RequestBody RegisterGeneric data) {
		return publicService.register(data);
	}


	@PostMapping(path = "/login/student")
	@Operation(summary = "Student Login")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation", content = @Content(mediaType = "text/plain")),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
	})
	public String loginStudent(
			@RequestParam("username") String username,
			@RequestParam("password") String password) {
		return publicService.loginStudent(username, password);
	}

	@PostMapping(path = "/login/professor")
	@Operation(summary = "Professor Login")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation", content = @Content(mediaType = "text/plain")),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
	})
	public String loginProfessor(
			@RequestParam("username") String username,
			@RequestParam("password") String password) {
		return publicService.loginProfessor(username, password);
	}


	@PostMapping(path = "/reset/student")
	@Operation(summary = "Student Password Reset")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation"),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
	})
	public void resetStudent(
			@RequestParam("username") String username) {
		publicService.resetStudent(username);
	}

	@PostMapping(path = "/reset/professor")
	@Operation(summary = "Professor Password Reset")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation"),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
	})
	public void resetProfessor(
			@RequestParam("username") String username) {
		publicService.resetProfessor(username);
	}

}
