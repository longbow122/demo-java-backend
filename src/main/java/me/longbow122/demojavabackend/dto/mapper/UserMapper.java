package me.longbow122.demojavabackend.dto.mapper;

import me.longbow122.demojavabackend.dto.UserDTO;
import me.longbow122.demojavabackend.repository.entities.User;

public class UserMapper {

	private UserMapper() { throw new IllegalStateException("This is a mapper class!"); }

	public static User toUser(UserDTO userDTO) {
		return new User(userDTO.getUsername(), userDTO.getPassword(), userDTO.getAge());
	}

	public static UserDTO toUserDTO(User user) {
		return new UserDTO(user.getUsername(), user.getPassword(), user.getAge());
	}
}
