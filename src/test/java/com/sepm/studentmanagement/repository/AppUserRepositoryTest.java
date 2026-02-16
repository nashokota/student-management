package com.sepm.studentmanagement.repository;

import com.sepm.studentmanagement.entity.AppUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for AppUserRepository.
 * Tests the custom findByUsername query method.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class AppUserRepositoryTest {

    @Autowired
    private AppUserRepository appUserRepository;

    @Test
    @DisplayName("Should find user by username")
    void findByUsername_WhenExists_ReturnsUser() {
        AppUser user = new AppUser("testteacher", "password123", "ROLE_TEACHER", "Test Teacher");
        appUserRepository.save(user);

        Optional<AppUser> found = appUserRepository.findByUsername("testteacher");

        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("testteacher");
        assertThat(found.get().getRole()).isEqualTo("ROLE_TEACHER");
        assertThat(found.get().getFullName()).isEqualTo("Test Teacher");
    }

    @Test
    @DisplayName("Should return empty when username not found")
    void findByUsername_WhenNotExists_ReturnsEmpty() {
        Optional<AppUser> found = appUserRepository.findByUsername("nonexistent");
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Should save user with student role")
    void saveStudentUser() {
        AppUser student = new AppUser("teststudent", "pass456", "ROLE_STUDENT", "Test Student");
        AppUser saved = appUserRepository.save(student);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getRole()).isEqualTo("ROLE_STUDENT");
    }
}
