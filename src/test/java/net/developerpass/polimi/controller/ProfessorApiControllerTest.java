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
import net.developerpass.polimi.repository.ProfessorRepository;
import net.developerpass.polimi.service.ProfessorService;
import net.developerpass.polimi.service.PublicService;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

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
public class ProfessorApiControllerTest {

	@Autowired
	private PublicService publicService;
	@Autowired
	private TestRestTemplate restTemplate;
	@Autowired
	private AccountRepository accountRepository;
	@Autowired
	private ProfessorRepository professorRepository;

	@MockBean
	private ProfessorService professorService;

	private final HttpHeaders headers = new HttpHeaders();

	@BeforeEach
	public void beforeEach() {
		professorRepository.deleteAll();
		accountRepository.deleteAll();

		String professorMail = "professor@mail.com";
		String defaultPassword = "TestPassword123";
		Account account = accountRepository.save(new Account(null, professorMail, DigestUtils.sha3_256Hex(defaultPassword), Role.Professor));
		professorRepository.save(new Professor(null, account.getId(), "Professor name", "Professor surname", account));

		headers.clear();
		String token = publicService.loginProfessor(professorMail, defaultPassword);
		headers.add("Authorization", "Bearer " + token);
	}

	@Test
	public void getProfile() {
		// Test 403

		ResponseEntity<Professor> response403 =
				restTemplate.exchange(
						"/api/professor/profile", HttpMethod.GET, null, Professor.class);

		assertEquals(HttpStatus.FORBIDDEN, response403.getStatusCode());

		// Test 200

		Professor professor = new Professor(1L, 1L, "Professor name", "Professor surname", null);
		given(professorService.getProfile()).willReturn(professor);

		ResponseEntity<Professor> response200 =
				restTemplate.exchange(
						"/api/professor/profile", HttpMethod.GET, new HttpEntity<>(null, headers), Professor.class);

		assertEquals(HttpStatus.OK, response200.getStatusCode());
		assertNotNull(response200.getBody());
		assertEquals(professor, response200.getBody());
	}

	@Test
	public void getProfessors() {
		Professor professor = new Professor(1L, 1L, "Professor name", "Professor surname", null);
		Page<Professor> professorPage = new PageImpl<>(List.of(professor), PageRequest.of(0, 10), 1);
		given(professorService.getProfessors(any(), any())).willReturn(professorPage);

		ParameterizedTypeReference<RestResponsePage<Professor>> type = new ParameterizedTypeReference<>() {};
		ResponseEntity<RestResponsePage<Professor>> response =
				restTemplate.exchange(
						"/api/professor/professor/list", HttpMethod.GET, new HttpEntity<>(null, headers), type);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(professorPage, response.getBody().getPage());
	}

	@Test
	public void getProfessor() {
		Professor professor = new Professor(1L, 1L, "Professor name", "Professor surname", null);
		given(professorService.getProfessor(any())).willReturn(professor);

		ResponseEntity<Professor> response =
				restTemplate.exchange(
						"/api/professor/professor/id/{professorId}", HttpMethod.GET, new HttpEntity<>(null, headers),
						Professor.class, professor.getId());

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(professor, response.getBody());
	}

	@Test
	public void getGroups() {
		Group group = new Group(1L, 1L, 1L, "Test Group", false, null, null, List.of());
		Page<Group> groupPage = new PageImpl<>(List.of(group), PageRequest.of(0, 10), 1);
		given(professorService.getGroups(any(), any())).willReturn(groupPage);

		ParameterizedTypeReference<RestResponsePage<Group>> type = new ParameterizedTypeReference<>() {};
		ResponseEntity<RestResponsePage<Group>> response =
				restTemplate.exchange(
						"/api/professor/group/list", HttpMethod.GET, new HttpEntity<>(null, headers), type);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(groupPage, response.getBody().getPage());
	}

	@Test
	public void getGroup() {
		Group group = new Group(1L, 1L, 1L, "Test Group", false, null, null, List.of());
		given(professorService.getGroup(any())).willReturn(group);

		ResponseEntity<Group> response =
				restTemplate.exchange(
						"/api/professor/group/id/{groupId}", HttpMethod.GET, new HttpEntity<>(null, headers),
						Group.class, group.getId());

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(group, response.getBody());
	}

	@Test
	public void getStudents() {
		Student student = new Student(1L, 1L, "Student name", "Student surname", null);
		Page<Student> studentPage = new PageImpl<>(List.of(student), PageRequest.of(0, 10), 1);
		given(professorService.getStudents(any(), any())).willReturn(studentPage);

		ParameterizedTypeReference<RestResponsePage<Student>> type = new ParameterizedTypeReference<>() {};
		ResponseEntity<RestResponsePage<Student>> response =
				restTemplate.exchange(
						"/api/professor/student/list", HttpMethod.GET, new HttpEntity<>(null, headers), type);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(studentPage, response.getBody().getPage());
	}

	@Test
	public void getStudent() {
		Student student = new Student(1L, 1L, "Student name", "Student surname", null);
		given(professorService.getStudent(any())).willReturn(student);

		ResponseEntity<Student> response =
				restTemplate.exchange(
						"/api/professor/student/id/{studentId}", HttpMethod.GET, new HttpEntity<>(null, headers),
						Student.class, student.getId());

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(student, response.getBody());
	}

	@Test
	public void getFiles() {
		Group group = new Group(1L, 1L, 1L, "Test Group", false, null, null, List.of());
		File file = new File(1L, 1L, "Exam 03/10", UUID.randomUUID().toString(), false, null);
		Page<File> filePage = new PageImpl<>(List.of(file), PageRequest.of(0, 10), 1);
		given(professorService.getFiles(any(), any(), any())).willReturn(filePage);

		ParameterizedTypeReference<RestResponsePage<File>> type = new ParameterizedTypeReference<>() {};
		ResponseEntity<RestResponsePage<File>> response =
				restTemplate.exchange(
						"/api/professor/file/list/{groupId}", HttpMethod.GET, new HttpEntity<>(null, headers),
						type, group.getId());

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(filePage, response.getBody().getPage());
	}

	@Test
	public void getFile() {
		File file = new File(1L, 1L, "Exam 03/10", UUID.randomUUID().toString(), false, null);
		given(professorService.getFile(any())).willReturn(file);

		ResponseEntity<File> response =
				restTemplate.exchange(
						"/api/professor/file/id/{fileId}", HttpMethod.GET, new HttpEntity<>(null, headers),
						File.class, file.getId());

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(file, response.getBody());
	}

	@Test
	public void getDiscussions() {
		Discussion discussion = new Discussion(1L, 1L, "Exam 03/10", LocalDateTime.now().withNano(0), false, null);
		Page<Discussion> discussionPage = new PageImpl<>(List.of(discussion), PageRequest.of(0, 10), 1);
		given(professorService.getDiscussions(any(), any())).willReturn(discussionPage);

		ParameterizedTypeReference<RestResponsePage<Discussion>> type = new ParameterizedTypeReference<>() {};
		ResponseEntity<RestResponsePage<Discussion>> response =
				restTemplate.exchange(
						"/api/professor/discussion/list", HttpMethod.GET, new HttpEntity<>(null, headers), type);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(discussionPage, response.getBody().getPage());
	}

	@Test
	public void getDiscussion() {
		Discussion discussion = new Discussion(1L, 1L, "Exam 03/10", LocalDateTime.now().withNano(0), false, null);
		given(professorService.getDiscussion(any())).willReturn(discussion);

		ResponseEntity<Discussion> response =
				restTemplate.exchange(
						"/api/professor/discussion/id/{discussionId}", HttpMethod.GET, new HttpEntity<>(null, headers),
						Discussion.class, discussion.getId());

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(discussion, response.getBody());
	}

	@Test
	public void createDiscussion() {
		Discussion discussion = new Discussion(1L, 1L, "Exam 03/10", LocalDateTime.now().withNano(0), false, null);
		given(professorService.createDiscussion(any(), any())).willReturn(discussion);

		ResponseEntity<Discussion> response =
				restTemplate.exchange(
						"/api/professor/discussion?name={name}&date={date}", HttpMethod.POST, new HttpEntity<>(null, headers),
						Discussion.class, discussion.getName(), discussion.getDate());

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(discussion, response.getBody());
	}

	@Test
	public void updateDiscussion() {
		Discussion discussion = new Discussion(1L, 1L, "Exam 03/10", LocalDateTime.now().withNano(0), false, null);
		given(professorService.updateDiscussion(any(), any(), any())).willReturn(discussion);

		ResponseEntity<Discussion> response =
				restTemplate.exchange(
						"/api/professor/discussion/id/{discussionId}?name={name}&date={date}", HttpMethod.PUT, new HttpEntity<>(null, headers),
						Discussion.class, discussion.getId(), discussion.getName(), discussion.getDate());

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(discussion, response.getBody());
	}

	@Test
	public void deleteDiscussion() {
		Discussion discussion = new Discussion(1L, 1L, "Exam 03/10", LocalDateTime.now().withNano(0), false, null);

		ResponseEntity<String> response =
				restTemplate.exchange(
						"/api/professor/discussion/id/{discussionId}", HttpMethod.DELETE, new HttpEntity<>(null, headers),
						String.class, discussion.getId());

		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	@Test
	public void getReservations() {
		Reservation reservation = new Reservation(1L, 1L, 1L, false, null, null);
		Page<Reservation> reservationPage = new PageImpl<>(List.of(reservation), PageRequest.of(0, 10), 1);
		given(professorService.getReservations(any(), any())).willReturn(reservationPage);

		ParameterizedTypeReference<RestResponsePage<Reservation>> type = new ParameterizedTypeReference<>() {};
		ResponseEntity<RestResponsePage<Reservation>> response =
				restTemplate.exchange(
						"/api/professor/reservation/list", HttpMethod.GET, new HttpEntity<>(null, headers), type);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(reservationPage, response.getBody().getPage());
	}

	@Test
	public void getReservation() {
		Reservation reservation = new Reservation(1L, 1L, 1L, false, null, null);
		given(professorService.getReservation(any())).willReturn(reservation);

		ResponseEntity<Reservation> response =
				restTemplate.exchange(
						"/api/professor/reservation/id/{reservationId}", HttpMethod.GET, new HttpEntity<>(null, headers),
						Reservation.class, reservation.getId());

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(reservation, response.getBody());
	}

	@Test
	public void deleteReservation() {
		Reservation reservation = new Reservation(1L, 1L, 1L, false, null, null);

		ResponseEntity<String> response =
				restTemplate.exchange(
						"/api/professor/reservation/id/{reservationId}", HttpMethod.DELETE, new HttpEntity<>(null, headers),
						String.class, reservation.getId());

		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

}
