package me.longbow122.demojavabackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import me.longbow122.demojavabackend.dto.UserDTO;
import me.longbow122.demojavabackend.dto.UserPatchDTO;
import me.longbow122.demojavabackend.exception.ControllerExceptionHandler;
import me.longbow122.demojavabackend.repository.entities.User;
import me.longbow122.demojavabackend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

	private MockMvc mockMvc;

	@Mock
	private UserService userService;

	@InjectMocks
	private UserController userController;

	private ObjectMapper objectMapper;
	private User testUser;
	private UserDTO testUserDTO;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(userController)
			.setControllerAdvice(new ControllerExceptionHandler())
			.build();
		objectMapper = new ObjectMapper();
		testUser = new User("testuser", "password123", 25);
		testUserDTO = new UserDTO("testuser", "password123", 25);
	}

	@Nested
	@DisplayName("GET /users tests")
	class GetAllUsersTests {

		@Test
		@DisplayName("Should return all users")
		void getAllUsers_shouldReturnAllUsers() throws Exception {
			User user2 = new User("user2", "pass2", 30);
			List<User> users = Arrays.asList(testUser, user2);
			when(userService.findAllUsers()).thenReturn(users);

			mockMvc.perform(get("/users"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.users").isArray())
				.andExpect(jsonPath("$.users.length()").value(2))
				.andExpect(jsonPath("$.users[0].username").value("testuser"))
				.andExpect(jsonPath("$.users[1].username").value("user2"));
		}

		@Test
		@DisplayName("Should return empty list when no users exist")
		void getAllUsers_shouldReturnEmptyList() throws Exception {
			when(userService.findAllUsers()).thenReturn(List.of());

			mockMvc.perform(get("/users"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.users").isArray())
				.andExpect(jsonPath("$.users.length()").value(0));
		}
	}

	@Nested
	@DisplayName("GET /users/{name} tests")
	class GetUserByUsernameTests {

		@Test
		@DisplayName("Should return user when found")
		void getUserByUsername_shouldReturnUser() throws Exception {
			when(userService.findUserByUsername("testuser")).thenReturn(Optional.of(testUser));

			mockMvc.perform(get("/users/testuser"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.username").value("testuser"))
				.andExpect(jsonPath("$.password").value("password123"))
				.andExpect(jsonPath("$.age").value(25));
		}

		@Test
		@DisplayName("Should return 404 when user not found")
		void getUserByUsername_shouldReturn404() throws Exception {
			when(userService.findUserByUsername("nonexistent")).thenReturn(Optional.empty());

			mockMvc.perform(get("/users/nonexistent"))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.error").value("This user does not exist!"));
		}
	}

	@Nested
	@DisplayName("POST /users tests")
	class CreateUserTests {

		@Test
		@DisplayName("Should create user successfully")
		void createUser_shouldCreateUser() throws Exception {
			when(userService.createUser(any(UserDTO.class))).thenReturn(testUser);

			mockMvc.perform(post("/users")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(testUserDTO)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.username").value("testuser"))
				.andExpect(jsonPath("$.password").value("password123"))
				.andExpect(jsonPath("$.age").value(25));
		}

		@Test
		@DisplayName("Should return 400 for invalid user data - blank username")
		void createUser_shouldReturn400ForBlankUsername() throws Exception {
			UserDTO invalidDTO = new UserDTO("", "password123", 25);

			mockMvc.perform(post("/users")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(invalidDTO)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.violations").isArray());
		}

		@Test
		@DisplayName("Should return 400 for invalid user data - negative age")
		void createUser_shouldReturn400ForNegativeAge() throws Exception {
			UserDTO invalidDTO = new UserDTO("testuser", "password123", -1);

			mockMvc.perform(post("/users")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(invalidDTO)))
				.andExpect(status().isBadRequest());
		}
	}

	@Nested
	@DisplayName("DELETE /users/{name} tests")
	class DeleteUserTests {

		@Test
		@DisplayName("Should delete user successfully")
		void deleteUser_shouldDeleteUser() throws Exception {
			doNothing().when(userService).deleteUser("testuser");

			mockMvc.perform(delete("/users/testuser"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message").value("This user was successfully deleted!"));
		}

		@Test
		@DisplayName("Should return 404 when user not found")
		void deleteUser_shouldReturn404() throws Exception {
			doThrow(new EntityNotFoundException("This user does not exist!"))
				.when(userService).deleteUser("nonexistent");

			mockMvc.perform(delete("/users/nonexistent"))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.error").value("This user does not exist!"));
		}
	}

	@Nested
	@DisplayName("PATCH /users/{name} tests")
	class UpdateUserTests {

		@Test
		@DisplayName("Should update user successfully")
		void updateUser_shouldUpdateUser() throws Exception {
			User updatedUser = new User("testuser", "newpassword", 30);
			when(userService.updateUser(anyString(), any(UserPatchDTO.class))).thenReturn(updatedUser);

			String patchJson = "{\"password\":\"newpassword\",\"age\":30}";

			mockMvc.perform(patch("/users/testuser")
					.contentType(MediaType.APPLICATION_JSON)
					.content(patchJson))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.password").value("newpassword"))
				.andExpect(jsonPath("$.age").value(30));
		}

		@Test
		@DisplayName("Should return 404 when user not found")
		void updateUser_shouldReturn404() throws Exception {
			doThrow(new EntityNotFoundException("This user does not exist!"))
				.when(userService).updateUser(anyString(), any(UserPatchDTO.class));

			String patchJson = "{\"password\":\"newpassword\"}";

			mockMvc.perform(patch("/users/nonexistent")
					.contentType(MediaType.APPLICATION_JSON)
					.content(patchJson))
				.andExpect(status().isNotFound());
		}
	}
}
