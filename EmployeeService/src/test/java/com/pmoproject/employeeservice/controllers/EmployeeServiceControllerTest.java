package com.pmoproject.employeeservice.controllers;

import com.pmoproject.employeeservice.models.domain.Employee;
import com.pmoproject.employeeservice.models.rabbitmq.EmployeeServiceResponse;
import com.pmoproject.employeeservice.services.EmployeeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.pmoproject.employeeservice.MockTestDataUtil.createMockEmployee;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceControllerTest {

    public static final String CONTRACT_EMPLOYEE_NO_END_DATE_ERROR_MESSAGE = "A contracted employee must have an end-date";
    public static final String NO_EMPLOYEE_WITH_ID_FOUND_ERROR_MESSAGE = "No employee with id [1] found";

    @Mock
    EmployeeService employeeService;

    @InjectMocks
    EmployeeServiceController employeeServiceController;

    @Nested
    @DisplayName("Employee Creation Tests")
    class EmployeeCreationTests {

        @Test
        @DisplayName("If the employee service throws an IllegalArgumentException then it will return a failed response with an appropriate message ")
        void ifTheEmployeeServiceThrowsAnIllegalArgumentExceptionThenItWillReturnAFailedResponseWithAnAppropriateMessage() {
            when(employeeService.createEmployee(any(Employee.class)))
                    .thenThrow(new IllegalArgumentException(CONTRACT_EMPLOYEE_NO_END_DATE_ERROR_MESSAGE));
            EmployeeServiceResponse mockEmployeeServiceResponse =
                    new EmployeeServiceResponse(false, CONTRACT_EMPLOYEE_NO_END_DATE_ERROR_MESSAGE, null);

            assertEquals(mockEmployeeServiceResponse, employeeServiceController.createEmployee(createMockEmployee()));
        }

        @Test
        @DisplayName("If the employee is successfully created then it will return a successful response with the employee created")
        void ifTheEmployeeIsSuccessfullyCreatedThenItWillReturnASuccessfulResponseWithTheEmployee() {
            when(employeeService.createEmployee(any(Employee.class))).thenReturn(createMockEmployee());
            EmployeeServiceResponse mockEmployeeServiceResponse =
                    new EmployeeServiceResponse(true, null, createMockEmployee());

            assertEquals(mockEmployeeServiceResponse, employeeServiceController.createEmployee(createMockEmployee()));
        }
    }

    @Nested
    @DisplayName("Employee Read Tests")
    class EmployeeReadTests {

        @Test
        @DisplayName("If the employee returned by the employee service is null then it will return a failed response with an appropriate message")
        void ifEmployeeReturnedIsNullThenItWillReturnAFailedResponseWithAnAppropriateMessage() {
            when(employeeService.getEmployeeById(anyInt())).thenReturn(null);
            EmployeeServiceResponse mockEmployeeServiceResponse =
                    new EmployeeServiceResponse(false, NO_EMPLOYEE_WITH_ID_FOUND_ERROR_MESSAGE, null);

            assertEquals(mockEmployeeServiceResponse, employeeServiceController.getEmployeeById(1));
        }

        @Test
        @DisplayName("If the employee returned by the employee service is noy null then it will return a successful response with the employee retrieved")
        void ifEmployeeReturnedIsNotNullThenItWillReturnASuccessfulResponseWithTheEmployee() {
            when(employeeService.getEmployeeById(anyInt())).thenReturn(createMockEmployee());
            EmployeeServiceResponse mockEmployeeServiceResponse =
                    new EmployeeServiceResponse(true, null, createMockEmployee());

            assertEquals(mockEmployeeServiceResponse, employeeServiceController.getEmployeeById(1));
        }
    }

}