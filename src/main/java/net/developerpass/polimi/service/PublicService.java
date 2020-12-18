package net.developerpass.polimi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.developerpass.polimi.service.authentication.ProfessorAuthenticationService;
import net.developerpass.polimi.service.authentication.StudentAuthenticationService;
import net.developerpass.polimi.utils.object.RegisterGeneric;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class PublicService {
	private final StudentAuthenticationService studentAuthenticationService;
	private final ProfessorAuthenticationService professorAuthenticationService;

	public String register(RegisterGeneric data) {
		return studentAuthenticationService.register(data);
	}


	public String loginStudent(String username, String password) {
		return studentAuthenticationService.login(username, DigestUtils.sha3_256Hex(password));
	}

	public String loginProfessor(String username, String password) {
		return professorAuthenticationService.login(username, DigestUtils.sha3_256Hex(password));
	}


	public void resetStudent(String username) {
		studentAuthenticationService.reset(username);
	}

	public void resetProfessor(String username) {
		professorAuthenticationService.reset(username);
	}
}
