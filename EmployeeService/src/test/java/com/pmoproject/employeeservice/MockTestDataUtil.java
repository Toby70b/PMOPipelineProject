package com.pmoproject.employeeservice;

import com.pmoproject.employeeservice.models.domain.Employee;

import java.time.LocalDateTime;
import java.time.Month;

//TODO: might want to remove this, a small change could break a number of tests.
public class MockTestDataUtil {

    /**
     * Creates a mock employee with some example data
     *
     * @return a mock employee with some example data
     */
    public static Employee createMockEmployee() {
        Employee mockEmployee = new Employee();
        mockEmployee.setName("exampleName");
        mockEmployee.setArchived(false);
        mockEmployee.setStartDate(LocalDateTime.of(2021, Month.APRIL, 8, 12, 30));
        mockEmployee.setContract(false);
        mockEmployee.setInPlace(true);
        return mockEmployee;
    }
}
