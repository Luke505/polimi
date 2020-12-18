package net.developerpass.polimi.utils.object;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterGeneric {
	private String username;
	private String password;
	private String name;
	private String surname;

	public boolean hasNull() {
		return username == null ||
				username.strip().isEmpty() ||
				password == null ||
				password.strip().isEmpty() ||
				name == null ||
				name.strip().isEmpty() ||
				surname == null ||
				surname.strip().isEmpty();
	}

	public boolean validLength() {
		return name.length() <= 20 &&
				surname.length() <= 20;
	}
}
