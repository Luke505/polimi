package net.developerpass.polimi.controller;

import lombok.extern.slf4j.Slf4j;
import net.developerpass.polimi.entity.Account;
import net.developerpass.polimi.entity.Discussion;
import net.developerpass.polimi.entity.File;
import net.developerpass.polimi.entity.Group;
import net.developerpass.polimi.entity.Professor;
import net.developerpass.polimi.entity.Reservation;
import net.developerpass.polimi.entity.Student;
import net.developerpass.polimi.repository.AccountRepository;
import net.developerpass.polimi.repository.StudentRepository;
import net.developerpass.polimi.service.PublicService;
import net.developerpass.polimi.service.StudentService;
import net.developerpass.polimi.utils.object.RestResponsePage;
import net.developerpass.polimi.utils.object.Role;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@Slf4j
@SpringJUnitConfig
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(replace = NONE)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class StudentApiControllerTest {

	@Autowired
	private PublicService publicService;
	@Autowired
	private TestRestTemplate restTemplate;
	@Autowired
	private AccountRepository accountRepository;
	@Autowired
	private StudentRepository studentRepository;

	@MockBean
	private StudentService studentService;

	private final HttpHeaders headers = new HttpHeaders();

	@BeforeEach
	public void beforeEach() {
		studentRepository.deleteAll();
		accountRepository.deleteAll();

		String studentMail = "student@mail.com";
		String defaultPassword = "TestPassword123";
		Account account = accountRepository.save(new Account(1L, studentMail, DigestUtils.sha3_256Hex(defaultPassword), Role.Student));
		studentRepository.save(new Student(1L, account.getId(), "Student name", "Student surname", account));

		headers.clear();
		String token = publicService.loginStudent(studentMail, defaultPassword);
		headers.add("Authorization", "Bearer " + token);
	}

	@Test
	void getProfile() {
		// Test 403

		ResponseEntity<Student> response403 =
				restTemplate.exchange(
						"/api/student/profile", HttpMethod.GET, null, Student.class);

		assertEquals(HttpStatus.FORBIDDEN, response403.getStatusCode());

		// Test 200

		Student student = new Student(1L, 1L, "Student name", "Student surname", null);
		given(studentService.getProfile()).willReturn(student);

		ResponseEntity<Student> response200 =
				restTemplate.exchange(
						"/api/student/profile", HttpMethod.GET, new HttpEntity<>(null, headers), Student.class);

		assertEquals(HttpStatus.OK, response200.getStatusCode());
		assertNotNull(response200.getBody());
		assertEquals(student, response200.getBody());
	}

	@Test
	public void getStudents() {
		Student student = new Student(1L, 1L, "Student name", "Student surname", null);
		Page<Student> studentPage = new PageImpl<>(List.of(student), PageRequest.of(0, 10), 1);
		given(studentService.getStudents(any(), any())).willReturn(studentPage);

		ParameterizedTypeReference<RestResponsePage<Student>> type = new ParameterizedTypeReference<>() {};
		ResponseEntity<RestResponsePage<Student>> response =
				restTemplate.exchange(
						"/api/student/list", HttpMethod.GET, new HttpEntity<>(null, headers), type);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(studentPage, response.getBody().getPage());
	}

	@Test
	public void getStudent() {
		Student student = new Student(1L, 1L, "Student name", "Student surname", null);
		given(studentService.getStudent(any())).willReturn(student);

		ResponseEntity<Student> response =
				restTemplate.exchange(
						"/api/student/id/{studentId}", HttpMethod.GET, new HttpEntity<>(null, headers),
						Student.class, student.getId());

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(student, response.getBody());
	}

	@Test
	public void getGroups() {
		Group group = new Group(1L, 1L, 1L, "Test Group", false, null, null, List.of());
		Page<Group> groupPage = new PageImpl<>(List.of(group), PageRequest.of(0, 10), 1);
		given(studentService.getGroups(any(), any())).willReturn(groupPage);

		ParameterizedTypeReference<RestResponsePage<Group>> type = new ParameterizedTypeReference<>() {};
		ResponseEntity<RestResponsePage<Group>> response =
				restTemplate.exchange(
						"/api/student/group/list", HttpMethod.GET, new HttpEntity<>(null, headers), type);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(groupPage, response.getBody().getPage());
	}

	@Test
	public void getGroup() {
		Group group = new Group(1L, 1L, 1L, "Test Group", false, null, null, List.of());
		given(studentService.getGroup(any())).willReturn(group);

		ResponseEntity<Group> response =
				restTemplate.exchange(
						"/api/student/group/id/{groupId}", HttpMethod.GET, new HttpEntity<>(null, headers),
						Group.class, group.getId());

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(group, response.getBody());
	}

	@Test
	public void createGroup() {
		Group group = new Group(1L, 1L, 1L, "Test Group", false, null, null, List.of());
		given(studentService.createGroup(any(), any())).willReturn(group);

		ResponseEntity<Group> response =
				restTemplate.exchange(
						"/api/student/group?professorId={professorId}&name={name}", HttpMethod.POST, new HttpEntity<>(null, headers),
						Group.class, group.getProfessorId(), group.getName());

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(group, response.getBody());
	}

	@Test
	public void updateGroup() {
		Group group = new Group(1L, 1L, 1L, "Test Group", false, null, null, List.of());
		given(studentService.updateGroup(any(), any())).willReturn(group);

		ResponseEntity<Group> response =
				restTemplate.exchange(
						"/api/student/group/id/{groupId}/?name={name}", HttpMethod.PUT, new HttpEntity<>(null, headers),
						Group.class, group.getId(), group.getName());

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(group, response.getBody());
	}

	@Test
	public void deleteGroup() {
		Group group = new Group(1L, 1L, 1L, "Test Group", false, null, null, List.of());
		ResponseEntity<String> response =
				restTemplate.exchange(
						"/api/student/group/id/{groupId}", HttpMethod.DELETE, new HttpEntity<>(null, headers),
						String.class, group.getId());

		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	@Test
	public void joinGroup() {
		Group group = new Group(1L, 1L, 1L, "Test Group", false, null, null, List.of());
		given(studentService.joinGroup(any())).willReturn(group);

		ResponseEntity<Group> response =
				restTemplate.exchange(
						"/api/student/group/id/{groupId}/join", HttpMethod.PUT, new HttpEntity<>(null, headers),
						Group.class, group.getId());

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(group, response.getBody());
	}

	@Test
	public void leaveGroup() {
		Group group = new Group(1L, 1L, 1L, "Test Group", false, null, null, List.of());
		ResponseEntity<String> response =
				restTemplate.exchange(
						"/api/student/group/id/{groupId}/leave", HttpMethod.PUT, new HttpEntity<>(null, headers),
						String.class, group.getId());

		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	@Test
	public void removeStudentFromGroup() {
		Student student = new Student(1L, 1L, "Student name", "Student surname", null);
		Group group = new Group(1L, 1L, 1L, "Test Group", false, null, null, List.of());
		ResponseEntity<String> response =
				restTemplate.exchange(
						"/api/student/group/id/{groupId}/remove/{studentId}", HttpMethod.PUT, new HttpEntity<>(null, headers),
						String.class, group.getId(), student.getId());

		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	@Test
	public void getFiles() {
		Group group = new Group(1L, 1L, 1L, "Test Group", false, null, null, List.of());
		File file = new File(1L, 1L, "Exam 03/10", UUID.randomUUID().toString(), false, null);
		Page<File> filePage = new PageImpl<>(List.of(file), PageRequest.of(0, 10), 1);
		given(studentService.getFiles(any(), any(), any())).willReturn(filePage);

		ParameterizedTypeReference<RestResponsePage<File>> type = new ParameterizedTypeReference<>() {};
		ResponseEntity<RestResponsePage<File>> response =
				restTemplate.exchange(
						"/api/student/file/list/{groupId}", HttpMethod.GET, new HttpEntity<>(null, headers),
						type, group.getId());

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(filePage, response.getBody().getPage());
	}

	@Test
	public void getFile() {
		File file = new File(1L, 1L, "Exam 03/10", UUID.randomUUID().toString(), false, null);
		given(studentService.getFile(any())).willReturn(file);

		ResponseEntity<File> response =
				restTemplate.exchange(
						"/api/student/file/id/{fileId}", HttpMethod.GET, new HttpEntity<>(null, headers),
						File.class, file.getId());

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(file, response.getBody());
	}

	@Test
	public void putFile() {
		Group group = new Group(1L, 1L, 1L, "Test Group", false, null, null, List.of());
		File file = new File(1L, 1L, "Exam 03/10", UUID.randomUUID().toString(), false, null);
		given(studentService.putFile(any(), any())).willReturn(file);

		String path = "db_img.png";
		java.io.File doc = new java.io.File(path);
		MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
		body.add("file", new FileSystemResource(doc));
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);

		ResponseEntity<File> response =
				restTemplate.postForEntity(
						"/api/student/group/id/" + group.getId() + "/file", new HttpEntity<>(body, headers), File.class);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(file, response.getBody());
	}

	@Test
	public void deleteFile() {
		File file = new File(1L, 1L, "Exam 03/10", UUID.randomUUID().toString(), false, null);
		ResponseEntity<String> response =
				restTemplate.exchange(
						"/api/student/file/id/{fileId}", HttpMethod.DELETE, new HttpEntity<>(null, headers),
						String.class, file.getId());

		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	@Test
	public void getProfessors() {
		Professor professor = new Professor(1L, 1L, "Professor name", "Professor surname", null);
		Page<Professor> professorPage = new PageImpl<>(List.of(professor), PageRequest.of(0, 10), 1);
		given(studentService.getProfessors(any(), any())).willReturn(professorPage);

		ParameterizedTypeReference<RestResponsePage<Professor>> type = new ParameterizedTypeReference<>() {};
		ResponseEntity<RestResponsePage<Professor>> response =
				restTemplate.exchange(
						"/api/student/professor/list", HttpMethod.GET, new HttpEntity<>(null, headers), type);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(professorPage, response.getBody().getPage());
	}

	@Test
	public void getProfessor() {
		Professor professor = new Professor(1L, 1L, "Professor name", "Professor surname", null);
		given(studentService.getProfessor(any())).willReturn(professor);

		ResponseEntity<Professor> response =
				restTemplate.exchange(
						"/api/student/professor/id/{professorId}", HttpMethod.GET, new HttpEntity<>(null, headers),
						Professor.class, professor.getId());

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(professor, response.getBody());
	}

	@Test
	public void getDiscussions() {
		Professor professor = new Professor(1L, 1L, "Professor name", "Professor surname", null);
		Discussion discussion = new Discussion(1L, 1L, "Exam 03/10", LocalDateTime.now().withNano(0), false, null);
		Page<Discussion> discussionPage = new PageImpl<>(List.of(discussion), PageRequest.of(0, 10), 1);
		given(studentService.getDiscussions(any(), any(), any())).willReturn(discussionPage);

		ParameterizedTypeReference<RestResponsePage<Discussion>> type = new ParameterizedTypeReference<>() {};
		ResponseEntity<RestResponsePage<Discussion>> response =
				restTemplate.exchange(
						"/api/student/professor/id/{professorId}/discussions", HttpMethod.GET, new HttpEntity<>(null, headers),
						type, professor.getId());

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(discussionPage, response.getBody().getPage());
	}

	@Test
	public void getDiscussion() {
		Discussion discussion = new Discussion(1L, 1L, "Exam 03/10", LocalDateTime.now().withNano(0), false, null);
		given(studentService.getDiscussion(any())).willReturn(discussion);

		ResponseEntity<Discussion> response =
				restTemplate.exchange(
						"/api/student/discussion/id/{discussionId}", HttpMethod.GET, new HttpEntity<>(null, headers),
						Discussion.class, discussion.getId());

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(discussion, response.getBody());
	}

	@Test
	public void getReservations() {
		Group group = new Group(1L, 1L, 1L, "Test Group", false, null, null, List.of());
		Reservation reservation = new Reservation(1L, 1L, 1L, false, null, null);
		Page<Reservation> reservationPage = new PageImpl<>(List.of(reservation), PageRequest.of(0, 10), 1);
		given(studentService.getReservations(any(), any(), any())).willReturn(reservationPage);

		ParameterizedTypeReference<RestResponsePage<Reservation>> type = new ParameterizedTypeReference<>() {};
		ResponseEntity<RestResponsePage<Reservation>> response =
				restTemplate.exchange(
						"/api/student/group/id/{groupId}/reservations", HttpMethod.GET, new HttpEntity<>(null, headers),
						type, group.getId());

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(reservationPage, response.getBody().getPage());
	}

	@Test
	public void getReservation() {
		Reservation reservation = new Reservation(1L, 1L, 1L, false, null, null);
		given(studentService.getReservation(any())).willReturn(reservation);

		ResponseEntity<Reservation> response =
				restTemplate.exchange(
						"/api/student/reservation/id/{reservationId}", HttpMethod.GET, new HttpEntity<>(null, headers),
						Reservation.class, reservation.getId());

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(reservation, response.getBody());
	}

	@Test
	public void createReservation() {
		Group group = new Group(1L, 1L, 1L, "Test Group", false, null, null, List.of());
		Reservation reservation = new Reservation(1L, 1L, 1L, false, null, null);
		Discussion discussion = new Discussion(1L, 1L, "Exam 03/10", LocalDateTime.now().withNano(0), false, null);
		given(studentService.createReservation(any(), any())).willReturn(reservation);

		ResponseEntity<Reservation> response =
				restTemplate.exchange(
						"/api/student/group/id/{groupId}/reservation/{discussionId}", HttpMethod.POST, new HttpEntity<>(null, headers),
						Reservation.class, group.getId(),discussion.getId());

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(reservation, response.getBody());
	}

	@Test
	public void updateReservation() {
		Reservation reservation = new Reservation(1L, 1L, 1L, false, null, null);
		Discussion discussion = new Discussion(1L, 1L, "Exam 03/10", LocalDateTime.now().withNano(0), false, null);
		given(studentService.updateReservation(any(), any())).willReturn(reservation);

		ResponseEntity<Reservation> response =
				restTemplate.exchange(
						"/api/student/reservation/id/{reservationId}/{discussionId}", HttpMethod.PUT, new HttpEntity<>(null, headers),
						Reservation.class, reservation.getId(), discussion.getId());

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(reservation, response.getBody());
	}

	@Test
	public void deleteReservation() {
		Reservation reservation = new Reservation(1L, 1L, 1L, false, null, null);

		ResponseEntity<String> response =
				restTemplate.exchange(
						"/api/student/reservation/id/{reservationId}", HttpMethod.DELETE, new HttpEntity<>(null, headers),
						String.class, reservation.getId());

		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

}
