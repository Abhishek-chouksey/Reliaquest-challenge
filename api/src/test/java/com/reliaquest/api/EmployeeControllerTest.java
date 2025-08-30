package com.reliaquest.api;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reliaquest.api.controller.EmployeeController;
import com.reliaquest.api.model.CreateEmployeeInput;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.service.EmployeeService;
import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(EmployeeController.class)
public class EmployeeControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private EmployeeService svc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllReturnsList() throws Exception {
        Employee e = new Employee();
        e.setId("1");
        e.setEmployeeName("Alice");
        when(svc.fetchAll()).thenReturn(Arrays.asList(e));

        mvc.perform(get("/api/v1/employee").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].employee_name", is("Alice")));
    }

    @Test
    void getByIdReturnsEmployee() throws Exception {
        Employee e = new Employee();
        e.setId("123");
        e.setEmployeeName("Bob");
        when(svc.fetchById("123")).thenReturn(e);

        mvc.perform(get("/api/v1/employee/123").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("123")))
                .andExpect(jsonPath("$.employee_name", is("Bob")));
    }

    @Test
    void searchByNameReturnsMatches() throws Exception {
        Employee e = new Employee();
        e.setId("9");
        e.setEmployeeName("Charlie");
        when(svc.searchByName("Char")).thenReturn(Collections.singletonList(e));

        mvc.perform(get("/api/v1/employee/search/Char").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].employee_name", is("Charlie")));
    }

    @Test
    void createEmployeeReturnsCreated() throws Exception {
        CreateEmployeeInput in = new CreateEmployeeInput();
        in.setName("Diana");
        in.setAge(30);
        in.setSalary(100000);
        in.setTitle("Engineer");

        Employee created = new Employee();
        created.setId("42");
        created.setEmployeeName("Diana");
        created.setEmployeeAge(30);
        created.setEmployeeSalary(100000);
        created.setEmployeeTitle("Engineer");

        when(svc.create(any(CreateEmployeeInput.class))).thenReturn(created);

        mvc.perform(post("/api/v1/employee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(in)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("42")))
                .andExpect(jsonPath("$.employee_name", is("Diana")))
                .andExpect(jsonPath("$.employee_age", is(30)));
    }

    @Test
    void deleteByIdReturnsMessage() throws Exception {
        when(svc.deleteById("99")).thenReturn("deleted: 99");

        mvc.perform(delete("/api/v1/employee/99"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("deleted: 99")));
    }
}
