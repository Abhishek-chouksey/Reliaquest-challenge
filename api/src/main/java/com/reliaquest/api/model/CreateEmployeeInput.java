package com.reliaquest.api.model;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateEmployeeInput {
    @NotBlank
    private String name;

    @NotNull @Positive private Integer salary;

    @NotNull @Min(16)
    @Max(75)
    private Integer age;

    @NotBlank
    private String title;
}
