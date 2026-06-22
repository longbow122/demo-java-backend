package me.longbow122.demojavabackend.dto.mapper;

import me.longbow122.demojavabackend.dto.UserDTO;
import me.longbow122.demojavabackend.repository.entities.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

	@Test
	@DisplayName("toUser should correctly map UserDTO to User entity")
	void toUser_shouldMapDTOToEntity() {
		UserDTO dto = new UserDTO("testuser", "password123", 25);

		User result = UserMapper.toUser(dto);

		assertNotNull(result);
		assertEquals("testuser", result.getUsername());
		assertEquals("password123", result.getPassword());
		assertEquals(25, result.getAge());
	}

	@Test
	@DisplayName("toUserDTO should correctly map User entity to UserDTO")
	void toUserDTO_shouldMapEntityToDTO() {
		User user = new User("testuser", "password123", 30);

		UserDTO result = UserMapper.toUserDTO(user);

		assertNotNull(result);
		assertEquals("testuser", result.getUsername());
		assertEquals("password123", result.getPassword());
		assertEquals(30, result.getAge());
	}

	@Test
	@DisplayName("UserMapper constructor should throw IllegalStateException")
	void constructor_shouldThrowIllegalStateException() throws Exception {
		var constructor = UserMapper.class.getDeclaredConstructor();
		constructor.setAccessible(true);
		var exception = assertThrows(java.lang.reflect.InvocationTargetException.class, constructor::newInstance);
		assertInstanceOf(IllegalStateException.class, exception.getCause());
	}
}
