package me.longbow122.demojavabackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {
	"me.longbow122.demojavabackend.controller",
	"me.longbow122.demojavabackend.service",
	"me.longbow122.demojavabackend.repository"
})
@EnableJpaRepositories(basePackages = {
	"me.longbow122.demojavabackend.repository"
})
public class DemoJavaBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoJavaBackendApplication.class, args);
	}

}
