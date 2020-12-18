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
import net.developerpass.polimi.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin
@RestController
@RequestMapping("api/student")
@SecurityRequirement(name = "JWT_Student")
@PreAuthorize("hasAnyAuthority('Student')")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Tag(name = "Student", description = "The Student API")
public class StudentApiController {

	private final StudentService studentService;

	@GetMapping(path = "/profile", produces = "application/json")
	@Operation(summary = "Get student data")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation"),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	Student getProfile() {
		return studentService.getProfile();
	}


	@GetMapping(path = "/list", produces = "application/json")
	@Operation(summary = "Get students")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation"),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	Page<Student> getStudents(@RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
							  @RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize) {
		return studentService.getStudents(page, pageSize);
	}

	@GetMapping(path = "/id/{studentId}", produces = "application/json")
	@Operation(summary = "Get student")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation"),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	Student getStudent(@PathVariable("studentId") Long studentId) {
		return studentService.getStudent(studentId);
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
		return studentService.getGroups(page, pageSize);
	}

	@GetMapping(path = "/group/id/{groupId}", produces = "application/json")
	@Operation(summary = "Get group")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation"),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	Group getGroup(@PathVariable("groupId") Long groupId) {
		return studentService.getGroup(groupId);
	}

	@PostMapping(path = "/group", produces = "application/json")
	@Operation(summary = "Create group")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation"),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	Group createGroup(@RequestParam("professorId") Long professorId, @RequestParam("name") String name) {
		return studentService.createGroup(professorId, name);
	}

	@PutMapping(path = "/group/id/{groupId}", produces = "application/json")
	@Operation(summary = "Update group")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation"),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	Group updateGroup(@PathVariable("groupId") Long groupId, @RequestParam("name") String name) {
		return studentService.updateGroup(groupId, name);
	}

	@DeleteMapping(path = "/group/id/{groupId}")
	@Operation(summary = "Delete Group")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation"),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
	})
	void deleteGroup(@PathVariable("groupId") Long groupId) {
		studentService.deleteGroup(groupId);
	}

	@PutMapping(path = "/group/id/{groupId}/join", produces = "application/json")
	@Operation(summary = "Join group")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation"),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	Group joinGroup(@PathVariable("groupId") Long groupId) {
		return studentService.joinGroup(groupId);
	}

	@PutMapping(path = "/group/id/{groupId}/leave")
	@Operation(summary = "Leave group")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation"),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
	})
	void leaveGroup(@PathVariable("groupId") Long groupId) {
		studentService.leaveGroup(groupId);
	}

	@PutMapping(path = "/group/id/{groupId}/remove/{studentId}")
	@Operation(summary = "Remove student from group")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation"),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
	})
	void removeStudentFromGroup(@PathVariable("studentId") Long studentId, @PathVariable("groupId") Long groupId) {
		studentService.removeStudentFromGroup(studentId, groupId);
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
		return studentService.getFiles(groupId, page, pageSize);
	}

	@GetMapping(path = "/file/id/{fileId}", produces = "application/json")
	@Operation(summary = "Get file")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation"),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	File getFile(@PathVariable("fileId") Long fileId) {
		return studentService.getFile(fileId);
	}

	@PostMapping(path = "/group/id/{groupId}/file", consumes = {"multipart/form-data"}, produces = "application/json")
	@Operation(summary = "Upload file")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation"),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	File putFile(@PathVariable("groupId") Long groupId, @RequestParam("file") MultipartFile file) {
		return studentService.putFile(groupId, file);
	}

	@DeleteMapping(path = "/file/id/{fileId}")
	@Operation(summary = "Delete file")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation"),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
	})
	void deleteFile(@PathVariable("fileId") Long fileId) {
		studentService.deleteFile(fileId);
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
		return studentService.getProfessors(page, pageSize);
	}

	@GetMapping(path = "/professor/id/{professorId}", produces = "application/json")
	@Operation(summary = "Get professor")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation"),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	Professor getProfessor(@PathVariable("professorId") Long professorId) {
		return studentService.getProfessor(professorId);
	}


	@GetMapping(path = "/professor/id/{professorId}/discussions", produces = "application/json")
	@Operation(summary = "Get discussions")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation"),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	Page<Discussion> getDiscussions(@PathVariable("professorId") Long professorId,
									@RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
									@RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize) {
		return studentService.getDiscussions(professorId, page, pageSize);
	}

	@GetMapping(path = "/discussion/id/{discussionId}", produces = "application/json")
	@Operation(summary = "Get discussion")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation"),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	Discussion getDiscussion(@PathVariable("discussionId") Long discussionId) {
		return studentService.getDiscussion(discussionId);
	}


	@GetMapping(path = "/group/id/{groupId}/reservations", produces = "application/json")
	@Operation(summary = "Get discussion reservations")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation"),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	Page<Reservation> getReservations(@PathVariable("groupId") Long groupId,
									  @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
									  @RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize) {
		return studentService.getReservations(groupId, page, pageSize);
	}

	@GetMapping(path = "/reservation/id/{reservationId}", produces = "application/json")
	@Operation(summary = "Get discussion reservation")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation"),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	Reservation getReservation(@PathVariable("reservationId") Long reservationId) {
		return studentService.getReservation(reservationId);
	}

	@PostMapping(path = "/group/id/{groupId}/reservation/{discussionId}", produces = "application/json")
	@Operation(summary = "Create discussion reservation")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation"),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	Reservation createReservation(@PathVariable("groupId") Long groupId, @PathVariable("discussionId") Long discussionId) {
		return studentService.createReservation(groupId, discussionId);
	}

	@PutMapping(path = "/reservation/id/{reservationId}/{discussionId}", produces = "application/json")
	@Operation(summary = "Update discussion reservation")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation"),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	Reservation updateReservation(@PathVariable("reservationId") Long reservationId, @PathVariable("discussionId") Long discussionId) {
		return studentService.updateReservation(reservationId, discussionId);
	}

	@DeleteMapping(path = "/reservation/id/{reservationId}")
	@Operation(summary = "Delete a discussion reservation")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful Operation"),
			@ApiResponse(responseCode = "400", description = "Operation failed", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Authentication Failure", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
	})
	void deleteReservation(@PathVariable("reservationId") Long reservationId) {
		studentService.deleteReservation(reservationId);
	}

}
