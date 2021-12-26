package com.pmoproject.employeeservice.models.rabbitmq;

/**
 * Represents different actions this micro-service supports, provided in requests and used for routing.
 */
public enum RequestType {
    CREATE_EMPLOYEE,
    GET_EMPLOYEE_BY_ID
}
