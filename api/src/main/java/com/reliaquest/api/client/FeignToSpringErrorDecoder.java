package com.reliaquest.api.client;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

public class FeignToSpringErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        int status = response.status();
        String reason = response.reason() != null ? response.reason() : "";
        if (status >= 400 && status < 500) {
            HttpStatus httpStatus = HttpStatus.resolve(status);
            if (httpStatus != null) {
                return new HttpClientErrorException(httpStatus, reason);
            }
        }
        return defaultDecoder.decode(methodKey, response);
    }
}
