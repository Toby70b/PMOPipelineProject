package com.pmoproject.employeeservice.models.rabbitmq;

import lombok.Data;

@Data
/**
 * Represents the structure of a response message this micro-service returns
 */
public class EmployeeServiceResponse {
    private boolean success;
    private String errorMessage;
    private Object responseBody;

    /**
     * Used to instantiate an object of this class
     *
     * @param success      indicates whether the request was successful
     * @param errorMessage details on the request's failure if it was not successful. If the request was successful this should be null
     * @param responseBody the response body to return. This will be serialized into JSON. If the request was not successful this might be null
     */
    public EmployeeServiceResponse(boolean success, String errorMessage, Object responseBody) {
        this.success = success;
        this.errorMessage = errorMessage;
        this.responseBody = responseBody;
    }
}
