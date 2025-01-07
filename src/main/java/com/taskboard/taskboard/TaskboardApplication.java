package com.taskboard.taskboard;

import com.taskboard.taskboard.entities.User;
import com.taskboard.taskboard.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TaskboardApplication implements CommandLineRunner {

	@Autowired
	UserRepository userRepository;

	public static void main(String[] args) {
		SpringApplication.run(TaskboardApplication.class, args);

	}

	@Override
	public void run(String... args) throws Exception {
		addUser();
	}

	@Transactional
	public void addUser() {
		userRepository.save(new User("Dani", "dani@gmail.com"));
	}
}
