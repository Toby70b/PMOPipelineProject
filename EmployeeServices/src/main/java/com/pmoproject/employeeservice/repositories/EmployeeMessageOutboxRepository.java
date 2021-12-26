package com.pmoproject.employeeservice.repositories;

import com.pmoproject.employeeservice.models.domain.EmployeeMessageOutboxItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
/**
 * Represents a JPA repository for the EmployeeMessageOutboxItem table, contains methods for interacting with it
 */
@Repository
public interface EmployeeMessageOutboxRepository extends JpaRepository<EmployeeMessageOutboxItem,Integer> {
     /**
      * Returns an optional with the first outbox item with a UUID matching the value passed.
      * @param correlationId the value with which to search the table for a matching id
      * @return an optional containing an <code>EmployeeMessageOutboxItem</code> object if a matching id is found, or empty if none is found
      */
     Optional<EmployeeMessageOutboxItem> findEmployeeMessageOutboxItemByCorrelationId(String correlationId);

     /**
      * Deletes outbox item with the matching correlation id. to indicate a message has been sent and received
      * @param correlationId the value with which to search the table for a matching id and delete
      */
     @Transactional
     void deleteEmployeeMessageOutboxItemByCorrelationId(String correlationId);
}
