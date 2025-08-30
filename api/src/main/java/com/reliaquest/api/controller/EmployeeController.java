package com.reliaquest.api.controller;

import com.reliaquest.api.model.CreateEmployeeInput;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.service.EmployeeService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/employee")
public class EmployeeController implements IEmployeeController<Employee, CreateEmployeeInput> {

    private static final Logger log = LoggerFactory.getLogger(EmployeeController.class);
    private final EmployeeService svc;

    public EmployeeController(EmployeeService svc) {
        this.svc = svc;
    }

    @Override
    @GetMapping
    public ResponseEntity<List<Employee>> getAllEmployees() {
        log.info("getAllEmployees called");
        return ResponseEntity.ok(svc.fetchAll());
    }

    @Override
    @GetMapping("/search/{name}")
    public ResponseEntity<List<Employee>> getEmployeesByNameSearch(@PathVariable String name) {
        log.info("getEmployeesByNameSearch called: {}", name);
        return ResponseEntity.ok(svc.searchByName(name));
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable String id) {
        log.info("getEmployeeById called: {}", id);
        return ResponseEntity.ok(svc.fetchById(id));
    }

    @Override
    @GetMapping("/highestSalary")
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        log.info("getHighestSalaryOfEmployees called");
        return ResponseEntity.ok(svc.highestSalary());
    }

    @Override
    @GetMapping("/topTenHighestEarningEmployeeNames")
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        log.info("getTopTenHighestEarningEmployeeNames called");
        return ResponseEntity.ok(svc.topTenNamesBySalary());
    }

    @Override
    @PostMapping
    public ResponseEntity<Employee> createEmployee(@Validated @RequestBody CreateEmployeeInput employeeInput) {
        log.info("createEmployee called for {}", employeeInput.getName());
        return ResponseEntity.ok(svc.create(employeeInput));
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEmployeeById(@PathVariable String id) {
        log.info("deleteEmployeeById called: {}", id);
        return ResponseEntity.ok(svc.deleteById(id));
    }
}
