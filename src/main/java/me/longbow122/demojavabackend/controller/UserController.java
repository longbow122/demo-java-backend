package me.longbow122.demojavabackend.controller;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import me.longbow122.demojavabackend.dto.UserDTO;
import me.longbow122.demojavabackend.dto.UserPatchDTO;
import me.longbow122.demojavabackend.dto.mapper.UserMapper;
import me.longbow122.demojavabackend.repository.entities.User;
import me.longbow122.demojavabackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/users")
@ComponentScan(basePackages = {
	"me.longbow122.demojavabackend.exception"
})
public class UserController {

	private final UserService userService;

	@Autowired
	public UserController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping
	public Map<String, List<UserDTO>> getAllUsers() {
		List<UserDTO> users = new ArrayList<>();
		userService.findAllUsers().forEach(user -> users.add(UserMapper.toUserDTO(user)));
		return Collections.singletonMap("users", users);
	}


	@GetMapping("/{name}")
	public UserDTO getUserByUsername(@PathVariable String name) {
		Optional<User> user = userService.findUserByUsername(name);
		if (user.isPresent()) {
			return UserMapper.toUserDTO(user.get());
		}
		throw new EntityNotFoundException("This user does not exist!");
	}

	@PostMapping
	public UserDTO createUser(@RequestBody @Valid UserDTO userDTO) {
		return UserMapper.toUserDTO(userService.createUser(userDTO));
	}

	@DeleteMapping(value = "/{name}", produces = "application/json")
	public Map<String, String> deleteUserByUsername(@PathVariable String name) {
		userService.deleteUser(name);
		return Collections.singletonMap("message", "This user was successfully deleted!");
	}

	@PatchMapping("/{name}")
	public UserDTO updateUser(@PathVariable String name, @RequestBody @Valid UserPatchDTO userPatchDTO) {
		return UserMapper.toUserDTO(userService.updateUser(name, userPatchDTO));
	}


}
