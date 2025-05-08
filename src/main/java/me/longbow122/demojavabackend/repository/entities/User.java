package me.longbow122.demojavabackend.repository.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "users")
public class User {

	/*
	* Worth noting that some business logic is still needed to do some proper checking on the data here.
	* The #save() method implemented by JPA repositories handles upsert behaviour, and not direct creation.
	* As such, saving something with the same primary key will be updating, not inserting.
	*
	* If we attempted to insert a record with the same primary key/username for whatever reason, we need to ensure that we do some checks
	* on the service layer to prevent that from happening!
	 */

	@Id
	@Column(nullable = false, unique = true)
	@Size(min = 1, max = 50, message = "Usernames must be between 1 and 50 characters long!")
	private String username;

	@Column(nullable = false)
	@Size(min = 1, max = 150, message = "Passwords must be between 1 and 150 characters long!")
	private String password;

	@Column(nullable = false)
	@PositiveOrZero(message = "Ages must be positive or zero!")
	@NotNull(message = "Ages cannot be null!")
	private int age;

	public User(String name, String password, int age) {
		this.username = name;
		this.password = password;
		this.age = age;
	}

	public User() {}
}
