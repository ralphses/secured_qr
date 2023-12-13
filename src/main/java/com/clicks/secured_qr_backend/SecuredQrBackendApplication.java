package com.clicks.secured_qr_backend;

import com.clicks.secured_qr_backend.enums.UserRole;
import com.clicks.secured_qr_backend.models.AppUser;
import com.clicks.secured_qr_backend.repository.AppUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class SecuredQrBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(SecuredQrBackendApplication.class, args);
	}
	
	@Bean
	CommandLineRunner commandLineRunner(AppUserRepository userRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			AppUser user = AppUser.builder()
					.email("eze.raph@gmail.com")
					.role(UserRole.CLIENT)
					.name("Raphael")
					.verified(true)
					.password(passwordEncoder.encode("password"))
					.build();

			userRepository.save(user);
		};
	}


}
