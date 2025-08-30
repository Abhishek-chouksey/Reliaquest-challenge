package com.reliaquest.api.client;

import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.Response;
import feign.Headers;
import feign.Param;
import feign.RequestLine;
import java.util.Map;

public interface EmployeeApi {

    @RequestLine("GET")
    Response<Employee[]> fetchAll();

    @RequestLine("GET {id}")
    Response<Employee> fetchById(@Param("id") String id);

    @RequestLine("POST")
    @Headers("Content-Type: application/json")
    Response<Employee> create(Map<String, Object> body);

    @RequestLine("DELETE")
    @Headers("Content-Type: application/json")
    Response<Boolean> deleteByName(Map<String, Object> body);
}
