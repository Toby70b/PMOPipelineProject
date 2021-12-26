package com.pmoproject.employeeservice.repositories;

import com.pmoproject.employeeservice.models.domain.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Represents a JPA repository for the Employee table, contains methods for interacting with it
 */
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    /**
     * Returns an optional with the first employee with an EmployeeId matching the value passed. These ids are unique.
     * @param employeeId the value with which to search the employee table for a matching id
     * @return an optional containing an  <code>Employee</code> object if a matching id is found, or empty if none is found
     */
    Optional<Employee> findById(Integer employeeId);
}
