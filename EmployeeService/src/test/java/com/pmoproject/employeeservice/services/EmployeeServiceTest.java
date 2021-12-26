package com.pmoproject.employeeservice.services;

import com.pmoproject.employeeservice.models.domain.Employee;
import com.pmoproject.employeeservice.repositories.EmployeeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;

import static com.pmoproject.employeeservice.MockTestDataUtil.createMockEmployee;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    public static final String NULL_EMPLOYEE_CREATION_ERROR_MESSAGE = "Employee object cannot be null during creation";
    public static final String CONTRACT_EMPLOYEE_NO_END_DATE_ERROR_MESSAGE = "A contracted employee must have an end-date";
    public static final String ARCHIVED_EMPLOYEE_CREATION_ERROR_MESSAGE = "An archived employee cannot be created";
    public static final String EMPLOYEE_NULL_START_DATE_ERROR_MESSAGE = "An employee's start date cannot be null";
    public static final String NO_EMPLOYEE_WITH_ID_FOUND_ERROR_MESSAGE = "No employee with id [1] was found";

    @Mock
    EmployeeRepository employeeRepository;

    @InjectMocks
    EmployeeService employeeService;

    @Nested
    @DisplayName("Employee Creation Tests")
    class EmployeeCreationTests {

        @Test
        @DisplayName("If the employee is null then an exception with an appropriate message will be thrown")
        void ifEmployeeIsNullThenExceptionWithAppropriateMessageWillBeThrown() {
            Throwable exceptionThatWasThrown = assertThrows(IllegalArgumentException.class, () -> employeeService.createEmployee(null));
            assertEquals(NULL_EMPLOYEE_CREATION_ERROR_MESSAGE, exceptionThatWasThrown.getMessage());
        }

        @Test
        @DisplayName("If an employee is a contractor but has no end-date, then an exception with an appropriate message will be thrown ")
        void IfAContractorEmployeeIsSavedWithoutEndDateThenThrowException() {
            Employee exampleContractEmployee = new Employee();
            exampleContractEmployee.setContract(true);
            Throwable exceptionThatWasThrown = assertThrows(IllegalArgumentException.class, () -> employeeService.createEmployee(exampleContractEmployee));
            assertEquals(CONTRACT_EMPLOYEE_NO_END_DATE_ERROR_MESSAGE, exceptionThatWasThrown.getMessage());
        }

        @Test
        @DisplayName("If an employee is archived, then an exception with an appropriate message will be thrown ")
        void IfAnEmployeeIsArchivedThenThrowException() {
            Employee exampleContractEmployee = new Employee();
            exampleContractEmployee.setArchived(true);
            Throwable exceptionThatWasThrown = assertThrows(IllegalArgumentException.class, () -> employeeService.createEmployee(exampleContractEmployee));
            assertEquals(ARCHIVED_EMPLOYEE_CREATION_ERROR_MESSAGE, exceptionThatWasThrown.getMessage());

        }

        @Test
        @DisplayName("If an employee's start date is null, then an exception with an appropriate message will be thrown ")
        void IfAnEmployeeStatDateIsNullThenThrowException() {
            Employee exampleContractEmployee = new Employee();
            exampleContractEmployee.setStartDate(null);
            Throwable exceptionThatWasThrown = assertThrows(IllegalArgumentException.class, () -> employeeService.createEmployee(exampleContractEmployee));
            assertEquals(EMPLOYEE_NULL_START_DATE_ERROR_MESSAGE, exceptionThatWasThrown.getMessage());
        }

        @Test
        @DisplayName("If the employee is null then nothing will be saved")
        void ifAnEmployeeIsNullThenNothingWillBeSaved() {
            assertThrows(IllegalArgumentException.class, () -> employeeService.createEmployee(null));
            verify(employeeRepository, never()).save(any());
        }

        @Test
        @DisplayName("If an employee is a contractor but has no end-date, then nothing will be saved")
        void IfAContractorEmployeeIsSavedWithoutEndDateThenNothingWillBeSaved() {
            Employee exampleContractEmployee = new Employee();
            exampleContractEmployee.setContract(true);
            assertThrows(IllegalArgumentException.class, () -> employeeService.createEmployee(exampleContractEmployee));
            verify(employeeRepository, never()).save(any());
        }

        @Test
        @DisplayName("If an employee is archived, then nothing will be saved")
        void IfAnEmployeeIsArchivedThenNothingWillBeSaved() {
            Employee exampleContractEmployee = new Employee();
            exampleContractEmployee.setArchived(true);
            assertThrows(IllegalArgumentException.class, () -> employeeService.createEmployee(exampleContractEmployee));
            verify(employeeRepository, never()).save(any());
        }

        @Test
        @DisplayName("If an employee's start date is null, then nothing will be saved")
        void IfAnEmployeeStatDateIsNullThenNothingWillBeSaved() {
            Employee exampleContractEmployee = new Employee();
            exampleContractEmployee.setStartDate(null);
            assertThrows(IllegalArgumentException.class, () -> employeeService.createEmployee(exampleContractEmployee));
            verify(employeeRepository, never()).save(any());
        }

        @Test
        @DisplayName("If an employee is valid, then it should be saved ")
        void IfAnEmployeeIsValidItShouldBeSaved() {
            when(employeeRepository.save(any(Employee.class))).then(i -> i.getArgument(0, Employee.class));
            Employee exampleContractEmployee = createMockEmployee();
            assertEquals(exampleContractEmployee,employeeService.createEmployee(exampleContractEmployee));
        }

    }

    @Nested
    @DisplayName("Employee Read by Id Tests")
    class EmployeeReadByIdTests {
        @Test
        @DisplayName("If a matching id is passed then it will return the matching employee object")
        void ifEmployeeIsMatchedThenItWillReturnTheMatchingEmployeeObject() {
            when(employeeRepository.findById(anyInt())).thenReturn(Optional.of(createMockEmployee()));
            assertEquals(employeeService.getEmployeeById(1), createMockEmployee());
        }

        @Test
        @DisplayName("If no employee with a matching id is found it should throw an appropriate exception")
        void ifNoEmployeeWithAMatchingIdIsFoundItShouldThrowAnAppropriateException() {
            when(employeeRepository.findById(anyInt())).thenReturn(Optional.empty());
            Throwable exceptionThatWasThrown = assertThrows(NoSuchElementException.class, () -> employeeService.getEmployeeById(1));
            assertEquals(NO_EMPLOYEE_WITH_ID_FOUND_ERROR_MESSAGE, exceptionThatWasThrown.getMessage());
        }
    }

}