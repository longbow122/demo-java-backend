package me.longbow122.demojavabackend.service;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import me.longbow122.demojavabackend.dto.UserDTO;
import me.longbow122.demojavabackend.dto.UserPatchDTO;
import me.longbow122.demojavabackend.dto.mapper.UserMapper;
import me.longbow122.demojavabackend.repository.UserRepository;
import me.longbow122.demojavabackend.repository.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@ComponentScan(basePackages = "me.longbow122.demojavabackend.repository")
public class UserService {

	private final UserRepository userRepository;

	@Autowired
	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Transactional
	public User createUser(UserDTO userDTO) {
		User newUser = UserMapper.toUser(userDTO);
		if (userRepository.existsById(userDTO.getUsername())) {
			throw new EntityExistsException("This user already exists!");
		}
		return userRepository.save(newUser);
	}

	@Transactional
	public void deleteUser(String username) {
		if (!userRepository.existsById(username)) {
			throw new EntityNotFoundException("This user does not exist!");
		}
		userRepository.deleteById(username);
	}

	@Transactional
	public List<User> findAllUsers() {
		return userRepository.findAll();
	}

	@Transactional
	public Optional<User> findUserByUsername(String username) {
		return userRepository.findUserByUsername(username);
	}

	@Transactional
	public User updateUser(String username, UserPatchDTO userPatchDTO) {
		Optional<User> user = userRepository.findById(username);
		if (user.isEmpty()) {
			throw new EntityNotFoundException("This user does not exist!");
		}
		User userFound = user.get();
		if (userPatchDTO.getUsername().isPresent()) {
			if (userRepository.existsById(userPatchDTO.getUsername().get())) {
				throw new EntityExistsException("This user already exists! You cannot update an entity to an already existing name!");
			}
			//* JPA will not allow for us to update the primary key of an entity! So we make a new one with the same information but with
			//* an updated username, and then remove the old one!
			User updatedUser = new User(userPatchDTO.getUsername().get(), userFound.getPassword(), userFound.getAge());
			userRepository.deleteById(username);
			userFound = updatedUser;
		}
		if (userPatchDTO.getPassword().isPresent()) userFound.setPassword(userPatchDTO.getPassword().get());
		if (userPatchDTO.getAge().isPresent()) userFound.setAge(userPatchDTO.getAge().get());
		userRepository.save(userFound);
		return userFound;
	}

}
