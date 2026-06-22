package me.longbow122.demojavabackend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class UserDTO {

	@NotBlank(message = "Username cannot be blank!")
	@NotNull(message = "Username cannot be null!")
	@Size(min = 1, max = 50, message = "Usernames must be between 1 and 50 characters long!")
	private String username;

	@NotNull(message = "Passwords cannot be null!")
	@NotBlank(message = "Passwords cannot be blank!")
	@Size(min = 1, max = 150, message = "Passwords must be between 1 and 150 characters long!")
	private String password;

	@NotNull(message = "Ages cannot be null!")
	@Min(value = 1, message = "Ages must be at least 1 year old!")
	private int age;

	public UserDTO(String username, String password, int age) {
		this.username = username;
		this.password = password;
		this.age = age;
	}

}
