package net.developerpass.polimi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.developerpass.polimi.configuration.error.ErrorResponse;
import net.developerpass.polimi.entity.Discussion;
import net.developerpass.polimi.entity.File;
import net.developerpass.polimi.entity.Group;
import net.developerpass.polimi.entity.Professor;
import net.developerpass.polimi.entity.Reservation;
import net.developerpass.polimi.entity.Student;
import net.developerpass.polimi.service.ProfessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@CrossOrigin
@RestController
@RequestMapping("api/professor")
@SecurityRequirement(name = "JWT_Professor")
@PreAuthorize("hasAnyAuthority('Professor')")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Tag(name = "Professor", description = "The Professor API")
public class ProfessorApiController {

	private final ProfessorService professorService;

	@GetMapping(path = "/profile", produces = "application/json")
	@Operation(summary = "Get professor data")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation"),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	Professor getProfile() {
		return professorService.getProfile();
	}


	@GetMapping(path = "/professor/list", produces = "application/json")
	@Operation(summary = "Get professors")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation"),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	Page<Professor> getProfessors(@RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
								  @RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize) {
		return professorService.getProfessors(page, pageSize);
	}

	@GetMapping(path = "/professor/id/{professorId}", produces = "application/json")
	@Operation(summary = "Get professor")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation"),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	Professor getProfessor(@PathVariable("professorId") Long professorId) {
		return professorService.getProfessor(professorId);
	}


	@GetMapping(path = "/group/list", produces = "application/json")
	@Operation(summary = "Get groups")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation"),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	Page<Group> getGroups(@RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
						  @RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize) {
		return professorService.getGroups(page, pageSize);
	}

	@GetMapping(path = "/group/id/{groupId}", produces = "application/json")
	@Operation(summary = "Get group")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation"),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	Group getGroup(@PathVariable("groupId") Long groupId) {
		return professorService.getGroup(groupId);
	}


	@GetMapping(path = "/student/list", produces = "application/json")
	@Operation(summary = "Get students")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation"),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	Page<Student> getStudents(@RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
							  @RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize) {
		return professorService.getStudents(page, pageSize);
	}

	@GetMapping(path = "/student/id/{studentId}", produces = "application/json")
	@Operation(summary = "Get student")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation"),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	Student getStudent(@PathVariable("studentId") Long studentId) {
		return professorService.getStudent(studentId);
	}


	@GetMapping(path = "/file/list/{groupId}", produces = "application/json")
	@Operation(summary = "Get files")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation"),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	Page<File> getFiles(@PathVariable("groupId") Long groupId,
						@RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
						@RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize) {
		return professorService.getFiles(groupId, page, pageSize);
	}

	@GetMapping(path = "/file/id/{fileId}", produces = "application/json")
	@Operation(summary = "Get file")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation"),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	File getFile(@PathVariable("fileId") Long fileId) {
		return professorService.getFile(fileId);
	}


	@GetMapping(path = "/discussion/list", produces = "application/json")
	@Operation(summary = "Get discussions")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation"),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	Page<Discussion> getDiscussions(@RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
									@RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize) {
		return professorService.getDiscussions(page, pageSize);
	}

	@GetMapping(path = "/discussion/id/{discussionId}", produces = "application/json")
	@Operation(summary = "Get discussion")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation"),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	Discussion getDiscussion(@PathVariable("discussionId") Long discussionId) {
		return professorService.getDiscussion(discussionId);
	}

	@PostMapping(path = "/discussion", produces = "application/json")
	@Operation(summary = "Create discussion")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation"),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	Discussion createDiscussion(@RequestParam(value = "name") String name,
								@RequestParam(value = "date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {
		return professorService.createDiscussion(name, date);
	}

	@PutMapping(path = "/discussion/id/{discussionId}", produces = "application/json")
	@Operation(summary = "Update discussion")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation"),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	Discussion updateDiscussion(@PathVariable("discussionId") Long discussionId,
								@RequestParam(value = "name") String name,
								@RequestParam(value = "date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {
		return professorService.updateDiscussion(discussionId, name, date);
	}

	@DeleteMapping(path = "/discussion/id/{discussionId}")
	@Operation(summary = "Delete a discussion")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation"),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
	})
	void deleteDiscussion(@PathVariable("discussionId") Long discussionId) {
		professorService.deleteDiscussion(discussionId);
	}


	@GetMapping(path = "/reservation/list", produces = "application/json")
	@Operation(summary = "Get discussion reservations")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation"),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	Page<Reservation> getReservations(@RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
									  @RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize) {
		return professorService.getReservations(page, pageSize);
	}

	@GetMapping(path = "/reservation/id/{reservationId}", produces = "application/json")
	@Operation(summary = "Get discussion reservation")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation"),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	Reservation getReservation(@PathVariable("reservationId") Long reservationId) {
		return professorService.getReservation(reservationId);
	}

	@DeleteMapping(path = "/reservation/id/{reservationId}")
	@Operation(summary = "Delete a discussion reservation")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation"),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
	})
	void deleteReservation(@PathVariable("reservationId") Long reservationId) {
		professorService.deleteReservation(reservationId);
	}

}
