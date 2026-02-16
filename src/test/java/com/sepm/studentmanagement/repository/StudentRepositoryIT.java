package com.sepm.studentmanagement.repository;

import com.sepm.studentmanagement.entity.Student;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for StudentRepository.
 * Uses @DataJpaTest which auto-configures an in-memory H2 database,
 * runs only the JPA slice (no web layer, no security).
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class StudentRepositoryIT {

    @Autowired
    private StudentRepository studentRepository;

    @Test
    @DisplayName("Should save and retrieve a student")
    void saveAndFindStudent() {
        Student student = new Student("Test", "User", "test@example.com", "CS", 2);
        Student saved = studentRepository.save(student);

        assertThat(saved.getId()).isNotNull();

        Optional<Student> found = studentRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getFirstName()).isEqualTo("Test");
        assertThat(found.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("Should return all students")
    void findAllStudents() {
        studentRepository.save(new Student("A", "One", "a@example.com", "CS", 1));
        studentRepository.save(new Student("B", "Two", "b@example.com", "EC", 2));

        List<Student> students = studentRepository.findAll();
        assertThat(students).hasSize(2);
    }

    @Test
    @DisplayName("Should delete a student by ID")
    void deleteStudent() {
        Student student = studentRepository.save(new Student("Del", "Test", "del@example.com", "ME", 3));
        Long id = student.getId();

        studentRepository.deleteById(id);

        Optional<Student> result = studentRepository.findById(id);
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should update an existing student")
    void updateStudent() {
        Student student = studentRepository.save(new Student("Old", "Name", "old@example.com", "CS", 1));

        student.setFirstName("New");
        student.setEmail("new@example.com");
        studentRepository.save(student);

        Optional<Student> updated = studentRepository.findById(student.getId());
        assertThat(updated).isPresent();
        assertThat(updated.get().getFirstName()).isEqualTo("New");
        assertThat(updated.get().getEmail()).isEqualTo("new@example.com");
    }
}
