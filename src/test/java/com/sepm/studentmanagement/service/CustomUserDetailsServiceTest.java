package com.sepm.studentmanagement.service;

import com.sepm.studentmanagement.entity.AppUser;
import com.sepm.studentmanagement.repository.AppUserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CustomUserDetailsService.
 * Verifies that Spring Security's UserDetailsService loads users correctly.
 */
@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private AppUserRepository appUserRepository;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    @Test
    @DisplayName("Should load user by username when user exists")
    void loadUserByUsername_WhenUserExists_ReturnsUserDetails() {
        AppUser teacher = new AppUser("teacher", "encoded_password", "ROLE_TEACHER", "Prof. Smith");
        when(appUserRepository.findByUsername("teacher")).thenReturn(Optional.of(teacher));

        UserDetails userDetails = userDetailsService.loadUserByUsername("teacher");

        assertThat(userDetails.getUsername()).isEqualTo("teacher");
        assertThat(userDetails.getPassword()).isEqualTo("encoded_password");
        assertThat(userDetails.getAuthorities()).hasSize(1);
        assertThat(userDetails.getAuthorities().iterator().next().getAuthority()).isEqualTo("ROLE_TEACHER");
        verify(appUserRepository, times(1)).findByUsername("teacher");
    }

    @Test
    @DisplayName("Should load student user correctly")
    void loadUserByUsername_StudentRole_ReturnsCorrectAuthority() {
        AppUser student = new AppUser("student", "encoded_password", "ROLE_STUDENT", "John Doe");
        when(appUserRepository.findByUsername("student")).thenReturn(Optional.of(student));

        UserDetails userDetails = userDetailsService.loadUserByUsername("student");

        assertThat(userDetails.getAuthorities().iterator().next().getAuthority()).isEqualTo("ROLE_STUDENT");
    }

    @Test
    @DisplayName("Should throw exception when username not found")
    void loadUserByUsername_WhenUserNotFound_ThrowsException() {
        when(appUserRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userDetailsService.loadUserByUsername("unknown"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User not found: unknown");

        verify(appUserRepository, times(1)).findByUsername("unknown");
    }
}
