package net.developerpass.polimi.service;

import lombok.extern.slf4j.Slf4j;
import net.developerpass.polimi.configuration.mail.CustomMailSender;
import net.developerpass.polimi.entity.Account;
import net.developerpass.polimi.entity.Professor;
import net.developerpass.polimi.entity.Student;
import net.developerpass.polimi.repository.AccountRepository;
import net.developerpass.polimi.repository.ProfessorRepository;
import net.developerpass.polimi.repository.StudentRepository;
import net.developerpass.polimi.utils.object.RegisterGeneric;
import net.developerpass.polimi.utils.object.Role;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@Slf4j
@Transactional
@SpringJUnitConfig
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(replace = NONE)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ComponentScan({"net.developerpass.polimi.service", "net.developerpass.polimi.configuration.mail"})
public class PublicServiceTest {
	@Autowired
	public PublicService publicService;
	@Autowired
	private AccountRepository accountRepository;
	@Autowired
	private StudentRepository studentRepository;
	@Autowired
	private ProfessorRepository professorRepository;

	@MockBean
	private CustomMailSender customMailSender;

	private final String studentMail = "student@mail.com";
	private final String professorMail = "professor@mail.com";
	private final String defaultPassword = "TestPassword123";

	@BeforeEach
	public void beforeEach() {
		accountRepository.deleteAll();

		Account studentAccount = accountRepository.save(new Account(studentMail, DigestUtils.sha3_256Hex(defaultPassword), Role.Student));
		studentRepository.save(new Student(studentAccount.getId(), "Student mame", "Student surname"));
		Account professorAccount = accountRepository.save(new Account(professorMail, DigestUtils.sha3_256Hex(defaultPassword), Role.Professor));
		professorRepository.save(new Professor(professorAccount.getId(), "Professor mame", "Professor surname"));

		given(customMailSender.sendResetStudent(any(), any())).willReturn(true);
		given(customMailSender.sendResetProfessor(any(), any())).willReturn(true);
	}

	@Test
	void register() {
		String customMail = "mario.bros@mail.com";
		RegisterGeneric registerGeneric = new RegisterGeneric(customMail, defaultPassword, "Mario", "Bros");

		publicService.register(registerGeneric);

		assertEquals(3, accountRepository.findAll().size());
		HashSet<String> accountEmails = accountRepository.findAll().stream().map(Account::getUsername).collect(Collectors.toCollection(HashSet::new));

		assertEquals(Set.of(customMail, studentMail, professorMail), accountEmails);
	}

	@Test
	void loginStudent() {
		String token = publicService.loginStudent(studentMail, defaultPassword);
		assertNotNull(token);
	}

	@Test
	void loginProfessor() {
		String token = publicService.loginProfessor(professorMail, defaultPassword);
		assertNotNull(token);
	}

	@Test
	void resetStudent() {
		publicService.resetStudent(studentMail);

		Optional<Account> account = accountRepository.findByUsername(studentMail);
		assertTrue(account.isPresent());
		assertNotEquals(defaultPassword, account.get().getPassword());
	}

	@Test
	void resetProfessor() {
		publicService.resetProfessor(professorMail);

		Optional<Account> account = accountRepository.findByUsername(professorMail);
		assertTrue(account.isPresent());
		assertNotEquals(defaultPassword, account.get().getPassword());
	}
}
