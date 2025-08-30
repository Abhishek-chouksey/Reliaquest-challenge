package com.reliaquest.api.service;

import com.reliaquest.api.client.EmployeeClient;
import com.reliaquest.api.exception.EmployeeNotFoundException;
import com.reliaquest.api.model.CreateEmployeeInput;
import com.reliaquest.api.model.Employee;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeService {
    private static final Logger log = LoggerFactory.getLogger(EmployeeService.class);

    private final EmployeeClient client;

    public List<Employee> fetchAll() {
        log.debug("Fetching all employees from remote service");
        List<Employee> employees = client.fetchAll();
        log.info("Fetched {} employees", employees != null ? employees.size() : 0);
        return employees;
    }

    public Employee fetchById(String id) {
        log.debug("Fetching employee by id={}", id);
        try {
            Employee employee = client.fetchById(id);
            if (employee == null) {
                log.warn("Employee with id={} not found", id);
                throw new EmployeeNotFoundException("Employee with id " + id + " not found");
            }
            log.info("Fetched employee with id={} and name={}", id, employee.getEmployeeName());
            return employee;
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                log.warn("Employee with id={} not found (404 from remote service)", id);
                throw new EmployeeNotFoundException("Employee with id " + id + " not found");
            }
            log.error("Error fetching employee with id={} -> {}", id, ex.getMessage(), ex);
            throw ex;
        }
    }

    public String deleteById(String id) {
        log.debug("Deleting employee by id={}", id);
        Employee employee = fetchById(id);
        String name = employee.getEmployeeName();
        if (Boolean.TRUE.equals(client.deleteByName(name))) {
            log.info("Successfully deleted employee id={} name={}", id, name);
            return name;
        }
        log.warn("Failed to delete employee with id={} (not found in remote)", id);
        throw new EmployeeNotFoundException("Employee with id " + id + " not found");
    }

    public List<Employee> searchByName(String name) {
        log.debug("Searching employees by name name='{}'", name);
        String formattedName = name == null ? "" : name.toLowerCase();
        List<Employee> results = fetchAll().stream()
                .filter(e -> e.getEmployeeName() != null
                        && e.getEmployeeName().toLowerCase().contains(formattedName))
                .collect(Collectors.toList());
        log.info("Found {} employees matching '{}'", results.size(), name);
        return results;
    }

    public int highestSalary() {
        log.debug("Calculating highest salary among employees");
        int maxSalary = fetchAll().stream()
                .map(Employee::getEmployeeSalary)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .max()
                .orElse(0);
        log.info("Highest salary found={}", maxSalary);
        return maxSalary;
    }

    public List<String> topTenNamesBySalary() {
        log.debug("Fetching top 10 employee names by salary");
        List<String> topTen = fetchAll().stream()
                .sorted(Comparator.comparing(
                                Employee::getEmployeeSalary, Comparator.nullsLast(Comparator.naturalOrder()))
                        .reversed())
                .limit(10)
                .map(Employee::getEmployeeName)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        log.info("Top 10 employees by salary: {}", topTen);
        return topTen;
    }

    public Employee create(CreateEmployeeInput in) {
        log.debug("Creating new employee with input: {}", in);
        Map<String, Object> body = new HashMap<>();
        body.put("name", in.getName());
        body.put("salary", in.getSalary());
        body.put("age", in.getAge());
        body.put("title", in.getTitle());
        Employee employee = client.create(body);
        log.info(
                "Created employee id={} name={}",
                employee != null ? employee.getId() : null,
                employee != null ? employee.getEmployeeName() : null);
        return employee;
    }
}
