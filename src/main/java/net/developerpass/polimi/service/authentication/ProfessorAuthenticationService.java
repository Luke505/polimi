package net.developerpass.polimi.service.authentication;

import lombok.RequiredArgsConstructor;
import net.developerpass.polimi.repository.AccountRepository;
import net.developerpass.polimi.service.jwt.JWTAuthenticationService;
import net.developerpass.polimi.service.jwt.JWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import static net.developerpass.polimi.utils.object.Role.Professor;

@Service
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ProfessorAuthenticationService {
	private final JWTService jwtService;
	private final AccountRepository accountRepository;
	private final JWTAuthenticationService authenticationService;

	public String login(String username, String password) {
		return accountRepository
				.findByRoleAndUsernameAndPassword(Professor, username, password)
				.map(user -> jwtService.create(Professor, username, password))
				.orElseThrow(() -> new BadCredentialsException("Invalid username or password"));
	}

	public void reset(String username) {
		authenticationService.reset(Professor, username);
	}

}
