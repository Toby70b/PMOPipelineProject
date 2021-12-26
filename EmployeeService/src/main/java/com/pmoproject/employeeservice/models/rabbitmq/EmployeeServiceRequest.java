package com.pmoproject.employeeservice.models.rabbitmq;

import lombok.Data;

@Data
/**
 * Used to represent a request to this micro-service
 */
public class EmployeeServiceRequest {
    Object requestBody;
}
