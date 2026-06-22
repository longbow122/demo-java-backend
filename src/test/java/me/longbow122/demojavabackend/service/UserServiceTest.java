package me.longbow122.demojavabackend.service;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import me.longbow122.demojavabackend.dto.UserDTO;
import me.longbow122.demojavabackend.dto.UserPatchDTO;
import me.longbow122.demojavabackend.repository.UserRepository;
import me.longbow122.demojavabackend.repository.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private UserService userService;

	private User testUser;
	private UserDTO testUserDTO;

	@BeforeEach
	void setUp() {
		testUser = new User("testuser", "password123", 25);
		testUserDTO = new UserDTO("testuser", "password123", 25);
	}

	@Nested
	@DisplayName("createUser tests")
	class CreateUserTests {

		@Test
		@DisplayName("Should successfully create a new user")
		void createUser_shouldCreateNewUser() {
			when(userRepository.existsById("testuser")).thenReturn(false);
			when(userRepository.save(any(User.class))).thenReturn(testUser);

			User result = userService.createUser(testUserDTO);

			assertNotNull(result);
			assertEquals("testuser", result.getUsername());
			verify(userRepository).existsById("testuser");
			verify(userRepository).save(any(User.class));
		}

		@Test
		@DisplayName("Should throw EntityExistsException when user already exists")
		void createUser_shouldThrowWhenUserExists() {
			when(userRepository.existsById("testuser")).thenReturn(true);

			EntityExistsException exception = assertThrows(
				EntityExistsException.class,
				() -> userService.createUser(testUserDTO)
			);

			assertEquals("This user already exists!", exception.getMessage());
			verify(userRepository, never()).save(any(User.class));
		}
	}

	@Nested
	@DisplayName("deleteUser tests")
	class DeleteUserTests {

		@Test
		@DisplayName("Should successfully delete an existing user")
		void deleteUser_shouldDeleteExistingUser() {
			when(userRepository.existsById("testuser")).thenReturn(true);
			doNothing().when(userRepository).deleteById("testuser");

			assertDoesNotThrow(() -> userService.deleteUser("testuser"));

			verify(userRepository).existsById("testuser");
			verify(userRepository).deleteById("testuser");
		}

		@Test
		@DisplayName("Should throw EntityNotFoundException when user does not exist")
		void deleteUser_shouldThrowWhenUserNotFound() {
			when(userRepository.existsById("nonexistent")).thenReturn(false);

			EntityNotFoundException exception = assertThrows(
				EntityNotFoundException.class,
				() -> userService.deleteUser("nonexistent")
			);

			assertEquals("This user does not exist!", exception.getMessage());
			verify(userRepository, never()).deleteById(anyString());
		}
	}

	@Nested
	@DisplayName("findAllUsers tests")
	class FindAllUsersTests {

		@Test
		@DisplayName("Should return all users")
		void findAllUsers_shouldReturnAllUsers() {
			User user2 = new User("user2", "pass2", 30);
			List<User> users = Arrays.asList(testUser, user2);
			when(userRepository.findAll()).thenReturn(users);

			List<User> result = userService.findAllUsers();

			assertEquals(2, result.size());
			verify(userRepository).findAll();
		}

		@Test
		@DisplayName("Should return empty list when no users exist")
		void findAllUsers_shouldReturnEmptyList() {
			when(userRepository.findAll()).thenReturn(List.of());

			List<User> result = userService.findAllUsers();

			assertTrue(result.isEmpty());
		}
	}

	@Nested
	@DisplayName("findUserByUsername tests")
	class FindUserByUsernameTests {

		@Test
		@DisplayName("Should return user when found")
		void findUserByUsername_shouldReturnUser() {
			when(userRepository.findUserByUsername("testuser")).thenReturn(Optional.of(testUser));

			Optional<User> result = userService.findUserByUsername("testuser");

			assertTrue(result.isPresent());
			assertEquals("testuser", result.get().getUsername());
		}

		@Test
		@DisplayName("Should return empty Optional when user not found")
		void findUserByUsername_shouldReturnEmptyOptional() {
			when(userRepository.findUserByUsername("nonexistent")).thenReturn(Optional.empty());

			Optional<User> result = userService.findUserByUsername("nonexistent");

			assertTrue(result.isEmpty());
		}
	}

	@Nested
	@DisplayName("updateUser tests")
	class UpdateUserTests {

		@Test
		@DisplayName("Should update user password only")
		void updateUser_shouldUpdatePassword() {
			UserPatchDTO patchDTO = new UserPatchDTO(Optional.empty(), Optional.of("newpassword"), Optional.empty());
			when(userRepository.findById("testuser")).thenReturn(Optional.of(testUser));
			when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

			User result = userService.updateUser("testuser", patchDTO);

			assertEquals("newpassword", result.getPassword());
			assertEquals(25, result.getAge());
			verify(userRepository).save(any(User.class));
		}

		@Test
		@DisplayName("Should update user age only")
		void updateUser_shouldUpdateAge() {
			UserPatchDTO patchDTO = new UserPatchDTO(Optional.empty(), Optional.empty(), Optional.of(30));
			when(userRepository.findById("testuser")).thenReturn(Optional.of(testUser));
			when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

			User result = userService.updateUser("testuser", patchDTO);

			assertEquals(30, result.getAge());
			assertEquals("password123", result.getPassword());
		}

		@Test
		@DisplayName("Should update username with new entity")
		void updateUser_shouldUpdateUsername() {
			UserPatchDTO patchDTO = new UserPatchDTO(Optional.of("newusername"), Optional.empty(), Optional.empty());
			when(userRepository.findById("testuser")).thenReturn(Optional.of(testUser));
			when(userRepository.existsById("newusername")).thenReturn(false);
			doNothing().when(userRepository).deleteById("testuser");
			when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

			User result = userService.updateUser("testuser", patchDTO);

			assertEquals("newusername", result.getUsername());
			verify(userRepository).deleteById("testuser");
		}

		@Test
		@DisplayName("Should throw EntityNotFoundException when user not found")
		void updateUser_shouldThrowWhenUserNotFound() {
			UserPatchDTO patchDTO = new UserPatchDTO(Optional.empty(), Optional.of("newpass"), Optional.empty());
			when(userRepository.findById("nonexistent")).thenReturn(Optional.empty());

			assertThrows(EntityNotFoundException.class, () ->
				userService.updateUser("nonexistent", patchDTO)
			);
		}

		@Test
		@DisplayName("Should throw EntityExistsException when new username already exists")
		void updateUser_shouldThrowWhenNewUsernameExists() {
			UserPatchDTO patchDTO = new UserPatchDTO(Optional.of("existinguser"), Optional.empty(), Optional.empty());
			when(userRepository.findById("testuser")).thenReturn(Optional.of(testUser));
			when(userRepository.existsById("existinguser")).thenReturn(true);

			EntityExistsException exception = assertThrows(
				EntityExistsException.class,
				() -> userService.updateUser("testuser", patchDTO)
			);

			assertTrue(exception.getMessage().contains("already exists"));
		}

		@Test
		@DisplayName("Should update all fields at once")
		void updateUser_shouldUpdateAllFields() {
			UserPatchDTO patchDTO = new UserPatchDTO(Optional.of("newuser"), Optional.of("newpass"), Optional.of(35));
			when(userRepository.findById("testuser")).thenReturn(Optional.of(testUser));
			when(userRepository.existsById("newuser")).thenReturn(false);
			doNothing().when(userRepository).deleteById("testuser");
			when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

			User result = userService.updateUser("testuser", patchDTO);

			assertEquals("newuser", result.getUsername());
			assertEquals("newpass", result.getPassword());
			assertEquals(35, result.getAge());
		}
	}
}
