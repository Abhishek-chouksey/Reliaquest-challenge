package com.reliaquest.api.model;

import lombok.Getter;
import lombok.Setter;

public class Response<T> {
    @Getter
    @Setter
    private T data;

    private String status;
}
