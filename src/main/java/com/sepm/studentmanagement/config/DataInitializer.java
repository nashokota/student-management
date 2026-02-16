package com.sepm.studentmanagement.config;

import com.sepm.studentmanagement.entity.AppUser;
import com.sepm.studentmanagement.entity.Student;
import com.sepm.studentmanagement.repository.AppUserRepository;
import com.sepm.studentmanagement.repository.StudentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final AppUserRepository appUserRepository;
    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(AppUserRepository appUserRepository,
                           StudentRepository studentRepository,
                           PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Create default users if none exist
        if (appUserRepository.count() == 0) {
            // Create a teacher account
            AppUser teacher = new AppUser(
                    "teacher",
                    passwordEncoder.encode("teacher123"),
                    "ROLE_TEACHER",
                    "Prof. Smith"
            );
            appUserRepository.save(teacher);

            // Create a student account
            AppUser student = new AppUser(
                    "student",
                    passwordEncoder.encode("student123"),
                    "ROLE_STUDENT",
                    "John Doe"
            );
            appUserRepository.save(student);

            System.out.println("=== Default users created ===");
            System.out.println("Teacher -> username: teacher, password: teacher123");
            System.out.println("Student -> username: student, password: student123");
        }

        // Add some sample students if none exist
        if (studentRepository.count() == 0) {
            studentRepository.save(new Student("Rahul", "Sharma", "rahul@example.com", "Computer Science", 2));
            studentRepository.save(new Student("Priya", "Patel", "priya@example.com", "Electronics", 3));
            studentRepository.save(new Student("Amit", "Kumar", "amit@example.com", "Mechanical", 1));
            studentRepository.save(new Student("Sneha", "Gupta", "sneha@example.com", "Computer Science", 4));
            studentRepository.save(new Student("Vikram", "Singh", "vikram@example.com", "Civil", 2));

            System.out.println("=== Sample students added ===");
        }
    }
}
