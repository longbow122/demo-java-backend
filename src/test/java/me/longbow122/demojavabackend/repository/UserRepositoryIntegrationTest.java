package me.longbow122.demojavabackend.repository;

import me.longbow122.demojavabackend.repository.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryIntegrationTest {

	@Autowired
	private TestEntityManager entityManager;

	@Autowired
	private UserRepository userRepository;

	private User testUser;

	@BeforeEach
	void setUp() {
		testUser = new User("testuser", "password123", 25);
	}

	@Test
	@DisplayName("Should save and retrieve a user")
	void saveAndFindUser() {
		entityManager.persistAndFlush(testUser);

		Optional<User> found = userRepository.findById("testuser");

		assertTrue(found.isPresent());
		assertEquals("testuser", found.get().getUsername());
		assertEquals("password123", found.get().getPassword());
		assertEquals(25, found.get().getAge());
	}

	@Test
	@DisplayName("Should find user by username using custom query")
	void findUserByUsername() {
		entityManager.persistAndFlush(testUser);

		Optional<User> found = userRepository.findUserByUsername("testuser");

		assertTrue(found.isPresent());
		assertEquals("testuser", found.get().getUsername());
	}

	@Test
	@DisplayName("Should return empty Optional when user not found")
	void findUserByUsername_notFound() {
		Optional<User> found = userRepository.findUserByUsername("nonexistent");

		assertTrue(found.isEmpty());
	}

	@Test
	@DisplayName("Should find all users")
	void findAllUsers() {
		User user2 = new User("user2", "pass2", 30);
		entityManager.persistAndFlush(testUser);
		entityManager.persistAndFlush(user2);

		List<User> users = userRepository.findAll();

		assertEquals(2, users.size());
	}

	@Test
	@DisplayName("Should delete user by id")
	void deleteUserById() {
		entityManager.persistAndFlush(testUser);

		userRepository.deleteById("testuser");
		entityManager.flush();

		assertFalse(userRepository.existsById("testuser"));
	}

	@Test
	@DisplayName("Should check if user exists")
	void existsById() {
		entityManager.persistAndFlush(testUser);

		assertTrue(userRepository.existsById("testuser"));
		assertFalse(userRepository.existsById("nonexistent"));
	}

	@Test
	@DisplayName("Should update user fields")
	void updateUser() {
		entityManager.persistAndFlush(testUser);

		User found = userRepository.findById("testuser").orElseThrow();
		found.setPassword("newpassword");
		found.setAge(30);
		userRepository.saveAndFlush(found);

		User updated = userRepository.findById("testuser").orElseThrow();
		assertEquals("newpassword", updated.getPassword());
		assertEquals(30, updated.getAge());
	}
}
