package net.developerpass.polimi.service.jwt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.utility.RandomString;
import net.developerpass.polimi.configuration.mail.CustomMailSender;
import net.developerpass.polimi.entity.Account;
import net.developerpass.polimi.repository.AccountRepository;
import net.developerpass.polimi.utils.object.Role;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class JWTAuthenticationService {
	private final JWTService jwtService;
	private final CustomMailSender customMailSender;
	private final AccountRepository accountRepository;

	public String login(Role role, String username, String password) throws BadCredentialsException {
		return accountRepository
				.findByRoleAndUsernameAndPassword(role, username, password)
				.map(user -> jwtService.create(role, username, password))
				.orElseThrow(() -> new BadCredentialsException("Invalid username or password"));
	}

	public void reset(Role role, String username) throws BadCredentialsException {
		accountRepository
				.findByRoleAndUsername(role, username)
				.ifPresentOrElse(
						account -> {
							String newPassword = RandomString.make(10);
							account.setPassword(DigestUtils.sha3_256Hex(newPassword));
							accountRepository.save(account);
							switch (role) {
								case Student: {
									if (!customMailSender.sendResetStudent(username, Map.of("password", newPassword))) {
										throw new RuntimeException("Something went wrong with email");
									}
									break;
								}
								case Professor: {
									if (!customMailSender.sendResetProfessor(username, Map.of("password", newPassword))) {
										throw new RuntimeException("Something went wrong with email");
									}
									break;
								}
								default:
									throw new RuntimeException("Something went wrong with reset");
							}
						},
						() -> {
							throw new BadCredentialsException("Invalid email");
						});
	}

	public Account authenticateByToken(String token) {
		try {
			Map<String, Object> data = jwtService.verify(token);
			Role role = Role.valueOf(String.valueOf(data.get("role")));
			String username = String.valueOf(data.get("username"));
			String password = String.valueOf(data.get("password"));
			return accountRepository.findByRoleAndUsernameAndPassword(role, username, password)
					.orElseThrow(() -> new UsernameNotFoundException("Authentication fail"));
		} catch (Exception e) {
			throw new BadCredentialsException("Invalid token");
		}
	}
}
