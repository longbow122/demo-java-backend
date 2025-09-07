package me.longbow122.demojavabackend.repository;

import me.longbow122.demojavabackend.repository.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

	Optional<User> findUserByUsername(String name);
}
