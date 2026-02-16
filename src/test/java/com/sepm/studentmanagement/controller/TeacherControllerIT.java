package com.sepm.studentmanagement.controller;

import com.sepm.studentmanagement.entity.Student;
import com.sepm.studentmanagement.service.StudentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for TeacherController.
 * Uses @SpringBootTest with MockMvc to test the full stack (controller + security + service + H2 DB).
 * @WithMockUser simulates an authenticated user with a specific role.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TeacherControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StudentService studentService;

    @Test
    @DisplayName("Teacher can access teacher dashboard")
    @WithMockUser(username = "teacher", roles = {"TEACHER"})
    void teacherDashboard_WithTeacherRole_ReturnsOk() throws Exception {
        mockMvc.perform(get("/teacher/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("teacher-dashboard"))
                .andExpect(model().attributeExists("students"))
                .andExpect(model().attributeExists("newStudent"));
    }

    @Test
    @DisplayName("Student cannot access teacher dashboard — gets 403 Forbidden")
    @WithMockUser(username = "student", roles = {"STUDENT"})
    void teacherDashboard_WithStudentRole_ReturnsForbidden() throws Exception {
        mockMvc.perform(get("/teacher/dashboard"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Unauthenticated user is redirected to login")
    void teacherDashboard_Unauthenticated_RedirectsToLogin() throws Exception {
        mockMvc.perform(get("/teacher/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @DisplayName("Teacher can add a new student")
    @WithMockUser(username = "teacher", roles = {"TEACHER"})
    void addStudent_WithValidData_RedirectsToDashboard() throws Exception {
        mockMvc.perform(post("/teacher/add-student")
                        .with(csrf())
                        .param("firstName", "NewFirst")
                        .param("lastName", "NewLast")
                        .param("email", "new@example.com")
                        .param("department", "Computer Science")
                        .param("year", "2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/teacher/dashboard"));
    }

    @Test
    @DisplayName("Adding student with invalid data returns form with errors")
    @WithMockUser(username = "teacher", roles = {"TEACHER"})
    void addStudent_WithInvalidData_ReturnsFormWithErrors() throws Exception {
        mockMvc.perform(post("/teacher/add-student")
                        .with(csrf())
                        .param("firstName", "")   // blank — validation fails
                        .param("lastName", "")
                        .param("email", "invalid") // not a valid email
                        .param("department", "")
                        .param("year", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("teacher-dashboard"))
                .andExpect(model().hasErrors());
    }

    @Test
    @DisplayName("Teacher can delete a student")
    @WithMockUser(username = "teacher", roles = {"TEACHER"})
    void deleteStudent_WithTeacherRole_RedirectsToDashboard() throws Exception {
        // First add a student to delete
        Student student = studentService.saveStudent(
                new Student("ToDelete", "User", "delete@example.com", "CS", 1));

        mockMvc.perform(get("/teacher/delete-student/" + student.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/teacher/dashboard"));
    }

    @Test
    @DisplayName("Teacher can access edit student form")
    @WithMockUser(username = "teacher", roles = {"TEACHER"})
    void editStudentForm_WithValidId_ReturnsEditPage() throws Exception {
        Student student = studentService.saveStudent(
                new Student("Edit", "Me", "edit@example.com", "EC", 3));

        mockMvc.perform(get("/teacher/edit-student/" + student.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("edit-student"))
                .andExpect(model().attributeExists("student"));
    }

    @Test
    @DisplayName("Teacher can update a student")
    @WithMockUser(username = "teacher", roles = {"TEACHER"})
    void updateStudent_WithValidData_RedirectsToDashboard() throws Exception {
        Student student = studentService.saveStudent(
                new Student("Before", "Update", "before@example.com", "ME", 1));

        mockMvc.perform(post("/teacher/update-student/" + student.getId())
                        .with(csrf())
                        .param("firstName", "After")
                        .param("lastName", "Update")
                        .param("email", "after@example.com")
                        .param("department", "ME")
                        .param("year", "2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/teacher/dashboard"));
    }
}
