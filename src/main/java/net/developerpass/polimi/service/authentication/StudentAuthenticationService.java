package net.developerpass.polimi.service.authentication;

import lombok.RequiredArgsConstructor;
import net.developerpass.polimi.entity.Account;
import net.developerpass.polimi.entity.Student;
import net.developerpass.polimi.repository.AccountRepository;
import net.developerpass.polimi.repository.StudentRepository;
import net.developerpass.polimi.service.jwt.JWTAuthenticationService;
import net.developerpass.polimi.service.jwt.JWTService;
import net.developerpass.polimi.utils.object.RegisterGeneric;
import net.developerpass.polimi.utils.object.Role;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class StudentAuthenticationService {
	private final JWTService jwtService;
	private final AccountRepository accountRepository;
	private final StudentRepository studentRepository;
	private final JWTAuthenticationService authenticationService;

	public String login(String username, String password) {
		return accountRepository
				.findByRoleAndUsernameAndPassword(Role.Student, username, password)
				.map(user -> jwtService.create(Role.Student, username, password))
				.orElseThrow(() -> new BadCredentialsException("Invalid username or password"));
	}

	public void reset(String username) {
		authenticationService.reset(Role.Student, username);
	}

	public String register(RegisterGeneric data) {
		// All compiled

		if (data.hasNull()) {
			throwIllegal("Missing records");
		}

		// Email validate

		String pattern = "^[_a-z0-9-]+(\\.[_a-z0-9-]+)*@[a-z0-9-]+(\\.[a-z0-9-]+)*(\\.[a-z]{2,})$";
		String email = data.getUsername().toLowerCase();
		if (!email.matches(pattern) || email.length() > 120) {
			throwIllegal("Invalid email");
		}

		// Existing account validate

		accountRepository.findByUsername(email).ifPresent((a) -> {
			throw throwIllegalReturn("Used email");
		});

		// Password validate

		pattern = "^(?=.*?[0-9])(?=.*?[a-z])(?=.*?[A-Z])(.{8,30})$";
		if (!data.getPassword().matches(pattern) || data.getPassword().length() > 30) {
			throwIllegal("Invalid password");
		}

		// Length validate

		if (!data.validLength()) {
			throwIllegal("Invalid records length");
		}

		// Save all

		Role role = Role.Student;
		String shaPassword = DigestUtils.sha3_256Hex(data.getPassword());

		Account account = accountRepository.save(
				new Account(email, shaPassword, role));
		studentRepository.save(
				new Student(account.getId(), data.getName(), data.getSurname()));

		return authenticationService.login(role, email, shaPassword);
	}

	private void throwIllegal(String msg) throws IllegalArgumentException {
		throw throwIllegalReturn(msg);
	}

	private IllegalArgumentException throwIllegalReturn(String msg) {
		return new IllegalArgumentException(msg);
	}
}
