package com.sepm.studentmanagement.service;

import com.sepm.studentmanagement.entity.Student;
import com.sepm.studentmanagement.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for StudentService.
 * Uses Mockito to mock the repository layer — no database or Spring context needed.
 */
@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentService studentService;

    private Student student1;
    private Student student2;

    @BeforeEach
    void setUp() {
        student1 = new Student("Rahul", "Sharma", "rahul@example.com", "Computer Science", 2);
        student1.setId(1L);

        student2 = new Student("Priya", "Patel", "priya@example.com", "Electronics", 3);
        student2.setId(2L);
    }

    @Test
    @DisplayName("Should return all students")
    void getAllStudents_ReturnsListOfStudents() {
        when(studentRepository.findAll()).thenReturn(Arrays.asList(student1, student2));

        List<Student> result = studentService.getAllStudents();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getFirstName()).isEqualTo("Rahul");
        assertThat(result.get(1).getFirstName()).isEqualTo("Priya");
        verify(studentRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no students exist")
    void getAllStudents_ReturnsEmptyList() {
        when(studentRepository.findAll()).thenReturn(List.of());

        List<Student> result = studentService.getAllStudents();

        assertThat(result).isEmpty();
        verify(studentRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return student by ID when found")
    void getStudentById_WhenExists_ReturnsStudent() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student1));

        Optional<Student> result = studentService.getStudentById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getFirstName()).isEqualTo("Rahul");
        assertThat(result.get().getEmail()).isEqualTo("rahul@example.com");
        verify(studentRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should return empty when student ID not found")
    void getStudentById_WhenNotExists_ReturnsEmpty() {
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Student> result = studentService.getStudentById(99L);

        assertThat(result).isEmpty();
        verify(studentRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Should save and return the student")
    void saveStudent_ReturnsSavedStudent() {
        Student newStudent = new Student("Amit", "Kumar", "amit@example.com", "Mechanical", 1);
        when(studentRepository.save(any(Student.class))).thenReturn(newStudent);

        Student result = studentService.saveStudent(newStudent);

        assertThat(result).isNotNull();
        assertThat(result.getFirstName()).isEqualTo("Amit");
        assertThat(result.getDepartment()).isEqualTo("Mechanical");
        verify(studentRepository, times(1)).save(newStudent);
    }

    @Test
    @DisplayName("Should delete student by ID")
    void deleteStudent_CallsRepositoryDelete() {
        doNothing().when(studentRepository).deleteById(1L);

        studentService.deleteStudent(1L);

        verify(studentRepository, times(1)).deleteById(1L);
    }
}
