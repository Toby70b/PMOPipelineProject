package com.pmoproject.employeeservice.services;

import com.pmoproject.employeeservice.models.domain.Employee;
import com.pmoproject.employeeservice.repositories.EmployeeRepository;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Service for the <code>Employee</code> class. Contains methods to perform CRUD operations on the Employee table
 * within the database
 */
@Service
@Data
public class EmployeeService {
    /**
     * JPA repository used to alter the Employee table
     */
    private final EmployeeRepository employeeRepository;

    /**
     * Creates a new employee in the database
     *
     * @param employee an <code>Employee</code> object to save to the database
     * @return the <code>Employee</code> instance that was saved to the database
     * @throws IllegalArgumentException if the <code>employee</code> parameter is null
     * @throws IllegalArgumentException if an employee is on contract but has no end-date
     * @throws IllegalArgumentException if an employee is archived
     * @throws IllegalArgumentException if the employee's start date is null
     */
    public Employee createEmployee(Employee employee) {

        if (employee == null) {
            throw new IllegalArgumentException("Employee object cannot be null during creation");
        }
        if (employee.isContract() && employee.getEndDate() == null) {
            throw new IllegalArgumentException("A contracted employee must have an end-date");
        }
        if (employee.isArchived()) {
            throw new IllegalArgumentException("An archived employee cannot be created");
        }
        if (employee.getStartDate() == null) {
            throw new IllegalArgumentException("An employee's start date cannot be null");
        }
        return employeeRepository.save(employee);
    }

    /**
     * Gets and employee from the database with a matching employee id
     *
     * @param employeeId the id to match against
     * @return an <code>Employee</code> object with a matching id
     * @throws NoSuchElementException if no employee matched the id given
     */
    public Employee getEmployeeById(int employeeId) {
        Optional<Employee> employee = employeeRepository.findById(employeeId);
        return employee.orElseThrow(() ->
                new NoSuchElementException(String.format("No employee with id [%s] was found", employeeId)));
    }
}
