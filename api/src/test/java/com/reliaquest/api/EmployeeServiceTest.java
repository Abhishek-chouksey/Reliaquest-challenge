package com.reliaquest.api;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.reliaquest.api.client.EmployeeApi;
import com.reliaquest.api.exception.EmployeeNotFoundException;
import com.reliaquest.api.model.CreateEmployeeInput;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.Response;
import com.reliaquest.api.service.EmployeeService;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class EmployeeServiceTest {

    @Autowired
    private EmployeeService employeeService;

    @MockBean
    private EmployeeApi employeeApi;

    @Test
    void testFetchAll() {
        Employee e = new Employee();
        e.setId("1");
        e.setEmployeeName("John");
        Response<Employee[]> resp = new Response<>();
        resp.setData(new Employee[] {e});
        when(employeeApi.fetchAll()).thenReturn(resp);

        List<Employee> result = employeeService.fetchAll();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEmployeeName()).isEqualTo("John");
    }

    @Test
    void testFetchById_notFound() {
        when(employeeApi.fetchById("999"))
                .thenThrow(new org.springframework.web.client.HttpClientErrorException(
                        org.springframework.http.HttpStatus.NOT_FOUND, "Not Found"));

        assertThatThrownBy(() -> employeeService.fetchById("999"))
                .isInstanceOf(EmployeeNotFoundException.class)
                .hasMessageContaining("Employee with id 999 not found");
    }

    @Test
    void testDeleteById_success() {
        Employee e = new Employee();
        e.setId("9");
        e.setEmployeeName("Mark");

        Response<Employee> fetchResp = new Response<>();
        fetchResp.setData(e);
        when(employeeApi.fetchById("9")).thenReturn(fetchResp);

        Response<Boolean> delResp = new Response<>();
        delResp.setData(Boolean.TRUE);
        when(employeeApi.deleteByName(any(Map.class))).thenReturn(delResp);

        String deleted = employeeService.deleteById("9");
        assertThat(deleted).isEqualTo("Mark");
    }

    @Test
    void testDeleteById_notFound() {
        Employee e = new Employee();
        e.setId("11");
        e.setEmployeeName("Ghost");

        Response<Employee> fetchResp = new Response<>();
        fetchResp.setData(e);
        when(employeeApi.fetchById("11")).thenReturn(fetchResp);

        Response<Boolean> delResp = new Response<>();
        delResp.setData(Boolean.FALSE);
        when(employeeApi.deleteByName(any(Map.class))).thenReturn(delResp);

        assertThatThrownBy(() -> employeeService.deleteById("11")).isInstanceOf(EmployeeNotFoundException.class);
    }

    @Test
    void testCreate() {
        CreateEmployeeInput input = new CreateEmployeeInput();
        input.setName("NewGuy");
        input.setAge(25);
        input.setSalary(1000);
        input.setTitle("Dev");

        Employee created = new Employee();
        created.setId("99");
        created.setEmployeeName("NewGuy");

        Response<Employee> respBody = new Response<>();
        respBody.setData(created);

        when(employeeApi.create(any(Map.class))).thenReturn(respBody);

        Employee result = employeeService.create(input);

        assertThat(result.getEmployeeName()).isEqualTo("NewGuy");
    }

    @Test
    void testSearchAndTopSalary() {
        Employee e1 = new Employee();
        e1.setId("1");
        e1.setEmployeeName("Alice");
        e1.setEmployeeSalary(5000);

        Employee e2 = new Employee();
        e2.setId("2");
        e2.setEmployeeName("Bob");
        e2.setEmployeeSalary(8000);

        Response<Employee[]> resp = new Response<>();
        resp.setData(new Employee[] {e1, e2});
        when(employeeApi.fetchAll()).thenReturn(resp);

        List<Employee> all = employeeService.fetchAll();
        assertThat(all).hasSize(2);

        List<String> topNames = employeeService.topTenNamesBySalary();
        assertThat(topNames).contains("Bob");

        int highest = employeeService.highestSalary();
        assertThat(highest).isEqualTo(8000);

        List<Employee> search = employeeService.searchByName("ali");
        assertThat(search).hasSize(1).extracting(Employee::getEmployeeName).contains("Alice");
    }
}
