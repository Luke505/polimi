package net.developerpass.polimi.controller;

import lombok.extern.slf4j.Slf4j;
import net.developerpass.polimi.service.PublicService;
import net.developerpass.polimi.utils.object.RegisterGeneric;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PublicApiControllerTest {

	@Autowired private TestRestTemplate restTemplate;

	@MockBean private PublicService publicService;

	private final String studentMail = "student@mail.com";
	private final String professorMail = "professor@mail.com";
	private final String defaultPassword = "TestPassword123";

	@Test
	void register() {
		String token = "test";
		given(publicService.register(any())).willReturn(token);
		RegisterGeneric registerGeneric = new RegisterGeneric();

		ResponseEntity<String> response =
				restTemplate.exchange(
						"/api/register/student", HttpMethod.POST, new HttpEntity<>(registerGeneric), String.class);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(token, response.getBody());
	}

	@Test
	void loginStudent() {
		String token = "test";
		given(publicService.loginStudent(any(), any())).willReturn(token);

		ResponseEntity<String> response =
				restTemplate.exchange(
						"/api/login/student?username={username}&password={password}", HttpMethod.POST, null,
						String.class, studentMail, defaultPassword);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(token, response.getBody());
	}

	@Test
	void loginProfessor() {
		String token = "test";
		given(publicService.loginProfessor(any(), any())).willReturn(token);

		ResponseEntity<String> response =
				restTemplate.exchange(
						"/api/login/professor?username={username}&password={password}", HttpMethod.POST, null,
						String.class, professorMail, defaultPassword);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(token, response.getBody());
	}

	@Test
	void resetStudent() {
		ResponseEntity<String> response =
				restTemplate.exchange(
						"/api/reset/student?username={username}", HttpMethod.POST, null,
						String.class, studentMail);

		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	@Test
	void resetProfessor() {
		ResponseEntity<String> response =
				restTemplate.exchange(
						"/api/reset/professor?username={username}", HttpMethod.POST, null,
						String.class, professorMail);

		assertEquals(HttpStatus.OK, response.getStatusCode());
	}
}
