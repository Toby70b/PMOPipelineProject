package com.pmoproject.employeeservice.controllers;

import com.pmoproject.employeeservice.models.domain.Employee;
import com.pmoproject.employeeservice.models.rabbitmq.EmployeeServiceResponse;
import com.pmoproject.employeeservice.services.EmployeeService;
import lombok.Data;
import org.springframework.stereotype.Controller;

/**
 * Class for the handling requests from the queue and providing an appropriate response to be sent back to the reply queue
 */
@Controller
@Data
public class EmployeeServiceController {
    private final EmployeeService employeeService;

    /**
     * Creates a new employee in the database
     * @param employee employee to create
     * @return a <code>EmployeeServiceResponse</code> object containing details of the outcome of the request
     */
    public EmployeeServiceResponse createEmployee(Employee employee) {
        try {
            Employee newEmployee = employeeService.createEmployee(employee);
            return new EmployeeServiceResponse(true, null, newEmployee);
        } catch (IllegalArgumentException exception) {
            return new EmployeeServiceResponse(false, exception.getMessage(), null);
        }

    }

    /**
     * Attempts to retrieve and employee by Id
     * @param id id of the employee to retrieve
     * @return a <code>EmployeeServiceResponse</code> object containing details of the outcome of the request
     */
    public EmployeeServiceResponse getEmployeeById(int id) {
        Employee employee = employeeService.getEmployeeById(id);
        if (employee == null) {
            //TODO: add enum for response messages
            return new EmployeeServiceResponse(false, String.format("No employee with id [%d] found",id), null);
        }
        return new EmployeeServiceResponse(true, null, employee);
    }
}
