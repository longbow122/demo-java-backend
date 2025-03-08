package me.longbow122.demojavabackend.repository;

import me.longbow122.demojavabackend.repository.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

	Optional<User> findUserByUsername(String name);

	List<User> findUserByUsernameStartingWith(String name);

	// ? JPA does not support delete query methods, need to write our own. JPA only supports find, read, query, count and get. Updates and Deletes will need to be handled ourselves.
	//* Transactional and Modifying annotations are required if we are modifying the database in any way.
	//? ?1 placeholder states which parameter in the method call to make use of when working with the data in any way.
	@Transactional
	@Modifying
	@Query("DELETE FROM User WHERE username = ?1")
	int deleteUserByName(String name);
}
