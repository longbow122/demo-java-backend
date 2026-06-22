package me.longbow122.demojavabackend.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.util.Optional;

@Getter
public class UserPatchDTO {

	Optional<@Size(min = 1, max = 50, message = "Usernames must be between 1 and 50 characters!") String> username;

	Optional<@Size(min = 1, max = 150, message = "Passwords must be betwween 1 and 150 characters!") String> password;

	Optional<@Positive(message = "Ages must be at least 1 year old!") Integer> age;

	public UserPatchDTO(Optional<String> username, Optional<String> password, Optional<Integer> age) {
		this.username = username;
		this.password = password;
		this.age = age;
	}
}
