package com.reliaquest.api.client;

import com.reliaquest.api.exception.TooManyRequestsException;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.Response;
import feign.FeignException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmployeeClient {

    private final EmployeeApi employeeApi;

    @Retryable(
            value = {FeignException.class},
            maxAttempts = 5,
            backoff = @Backoff(delay = 200, multiplier = 2))
    public List<Employee> fetchAll() {
        Response<Employee[]> resp = employeeApi.fetchAll();
        return List.of(Objects.requireNonNull(resp.getData()));
    }

    @Retryable(
            value = {FeignException.class},
            maxAttempts = 5,
            backoff = @Backoff(delay = 200, multiplier = 2))
    public Employee fetchById(String id) {
        Response<Employee> resp = employeeApi.fetchById(id);
        return Objects.requireNonNull(resp.getData());
    }

    @Retryable(
            value = {FeignException.class},
            maxAttempts = 5,
            backoff = @Backoff(delay = 200, multiplier = 2))
    public Boolean deleteByName(String name) {
        Response<Boolean> resp = employeeApi.deleteByName(Map.of("name", name));
        return Objects.requireNonNull(resp.getData());
    }

    public Employee create(Map<String, Object> body) {
        Response<Employee> resp = employeeApi.create(body);
        return Objects.requireNonNull(resp.getData());
    }

    @Recover
    public List<Employee> recoverFetchAll(HttpClientErrorException ex) {
        handleTooManyRequests(ex);
        throw ex;
    }

    @Recover
    public Employee recoverFetchById(HttpClientErrorException ex, String id) {
        handleTooManyRequests(ex);
        throw ex;
    }

    @Recover
    public Boolean recoverDeleteByName(HttpClientErrorException ex, String name) {
        handleTooManyRequests(ex);
        throw ex;
    }

    private void handleTooManyRequests(HttpClientErrorException ex) {
        if (ex.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
            log.warn("Service busy, throwing custom exception...");
            throw new TooManyRequestsException("Employee service busy. Try again later.");
        }
    }
}
