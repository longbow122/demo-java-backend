package me.longbow122.demojavabackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.longbow122.demojavabackend.dto.UserDTO;
import me.longbow122.demojavabackend.repository.UserRepository;
import me.longbow122.demojavabackend.repository.entities.User;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private UserRepository userRepository;

	@BeforeEach
	void setUp() {
		userRepository.deleteAll();
	}

	@Test
	@Order(1)
	@DisplayName("POST /users - Should create a new user")
	void createUser_shouldCreateNewUser() throws Exception {
		UserDTO userDTO = new UserDTO("integrationuser", "password123", 25);

		mockMvc.perform(post("/users")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(userDTO)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.username").value("integrationuser"))
			.andExpect(jsonPath("$.password").value("password123"))
			.andExpect(jsonPath("$.age").value(25));

		Assertions.assertTrue(userRepository.existsById("integrationuser"));
	}

	@Test
	@Order(2)
	@DisplayName("POST /users - Should return 400 for duplicate user")
	void createUser_shouldReturn400ForDuplicate() throws Exception {
		userRepository.save(new User("existinguser", "password", 20));
		UserDTO userDTO = new UserDTO("existinguser", "password123", 25);

		mockMvc.perform(post("/users")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(userDTO)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.error").value("This user already exists!"));
	}

	@Test
	@Order(3)
	@DisplayName("GET /users - Should return all users")
	void getAllUsers_shouldReturnAllUsers() throws Exception {
		userRepository.save(new User("user1", "pass1", 20));
		userRepository.save(new User("user2", "pass2", 30));

		mockMvc.perform(get("/users"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.users").isArray())
			.andExpect(jsonPath("$.users.length()").value(2));
	}

	@Test
	@Order(4)
	@DisplayName("GET /users/{name} - Should return user by username")
	void getUserByUsername_shouldReturnUser() throws Exception {
		userRepository.save(new User("findme", "password", 25));

		mockMvc.perform(get("/users/findme"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.username").value("findme"))
			.andExpect(jsonPath("$.password").value("password"))
			.andExpect(jsonPath("$.age").value(25));
	}

	@Test
	@Order(5)
	@DisplayName("GET /users/{name} - Should return 404 for non-existent user")
	void getUserByUsername_shouldReturn404() throws Exception {
		mockMvc.perform(get("/users/nonexistent"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.error").value("This user does not exist!"));
	}

	@Test
	@Order(6)
	@DisplayName("PATCH /users/{name} - Should update user password")
	void updateUser_shouldUpdatePassword() throws Exception {
		userRepository.save(new User("patchuser", "oldpassword", 25));

		String patchJson = "{\"password\":\"newpassword\"}";

		mockMvc.perform(patch("/users/patchuser")
				.contentType(MediaType.APPLICATION_JSON)
				.content(patchJson))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.password").value("newpassword"));

		User updated = userRepository.findById("patchuser").orElseThrow();
		Assertions.assertEquals("newpassword", updated.getPassword());
	}

	@Test
	@Order(7)
	@DisplayName("PATCH /users/{name} - Should update user age")
	void updateUser_shouldUpdateAge() throws Exception {
		userRepository.save(new User("ageuser", "password", 25));

		String patchJson = "{\"age\":30}";

		mockMvc.perform(patch("/users/ageuser")
				.contentType(MediaType.APPLICATION_JSON)
				.content(patchJson))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.age").value(30));
	}

	@Test
	@Order(8)
	@DisplayName("PATCH /users/{name} - Should update username")
	void updateUser_shouldUpdateUsername() throws Exception {
		userRepository.save(new User("oldname", "password", 25));

		String patchJson = "{\"username\":\"newname\"}";

		mockMvc.perform(patch("/users/oldname")
				.contentType(MediaType.APPLICATION_JSON)
				.content(patchJson))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.username").value("newname"));

		Assertions.assertFalse(userRepository.existsById("oldname"));
		Assertions.assertTrue(userRepository.existsById("newname"));
	}

	@Test
	@Order(9)
	@DisplayName("PATCH /users/{name} - Should return 400 when new username exists")
	void updateUser_shouldReturn400WhenUsernameExists() throws Exception {
		userRepository.save(new User("original", "password", 25));
		userRepository.save(new User("taken", "password", 30));

		String patchJson = "{\"username\":\"taken\"}";

		mockMvc.perform(patch("/users/original")
				.contentType(MediaType.APPLICATION_JSON)
				.content(patchJson))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.error").exists());
	}

	@Test
	@Order(10)
	@DisplayName("DELETE /users/{name} - Should delete user")
	void deleteUser_shouldDeleteUser() throws Exception {
		userRepository.save(new User("deleteme", "password", 25));

		mockMvc.perform(delete("/users/deleteme"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message").value("This user was successfully deleted!"));

		Assertions.assertFalse(userRepository.existsById("deleteme"));
	}

	@Test
	@Order(11)
	@DisplayName("DELETE /users/{name} - Should return 404 for non-existent user")
	void deleteUser_shouldReturn404() throws Exception {
		mockMvc.perform(delete("/users/nonexistent"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.error").value("This user does not exist!"));
	}

	@Test
	@Order(12)
	@DisplayName("POST /users - Should validate username length")
	void createUser_shouldValidateUsernameLength() throws Exception {
		String longUsername = "a".repeat(51);
		UserDTO userDTO = new UserDTO(longUsername, "password123", 25);

		mockMvc.perform(post("/users")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(userDTO)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.violations").isArray());
	}

	@Test
	@Order(13)
	@DisplayName("POST /users - Should validate password length")
	void createUser_shouldValidatePasswordLength() throws Exception {
		String longPassword = "a".repeat(151);
		UserDTO userDTO = new UserDTO("validuser", longPassword, 25);

		mockMvc.perform(post("/users")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(userDTO)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.violations").isArray());
	}

	@Test
	@Order(14)
	@DisplayName("POST /users - Should validate age minimum")
	void createUser_shouldValidateAgeMinimum() throws Exception {
		UserDTO userDTO = new UserDTO("validuser", "password123", 0);

		mockMvc.perform(post("/users")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(userDTO)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.violations").isArray());
	}

	@Test
	@Order(15)
	@DisplayName("Full CRUD workflow integration test")
	void fullCrudWorkflow() throws Exception {
		UserDTO createDTO = new UserDTO("workflowuser", "initial", 20);
		mockMvc.perform(post("/users")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createDTO)))
			.andExpect(status().isOk());

		mockMvc.perform(get("/users/workflowuser"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.username").value("workflowuser"));

		mockMvc.perform(patch("/users/workflowuser")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"password\":\"updated\",\"age\":25}"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.password").value("updated"))
			.andExpect(jsonPath("$.age").value(25));

		mockMvc.perform(delete("/users/workflowuser"))
			.andExpect(status().isOk());

		mockMvc.perform(get("/users/workflowuser"))
			.andExpect(status().isNotFound());
	}
}
