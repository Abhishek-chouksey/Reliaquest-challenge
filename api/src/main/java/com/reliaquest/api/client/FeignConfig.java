package com.reliaquest.api.client;

import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Value("${employee.service.url:http://localhost:8112/api/v1/employee}")
    private String baseUrl;

    @Bean
    public EmployeeApi employeeApi() {
        return Feign.builder()
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .errorDecoder(new FeignToSpringErrorDecoder())
                .target(EmployeeApi.class, baseUrl);
    }
}
