package com.pmoproject.employeeservice.models.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

/**
 * Class representing an item in the employee service outbox, to be sent to a reply queue
 */
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@Data
public class EmployeeMessageOutboxItem {
    @Id
    @Column(name = "EmployeeMessageOutboxItemId")
    private int id;
    @Column(name = "CorrelationId")
    private String correlationId;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime MessageCreated;
    @Column(name = "Payload", columnDefinition = "json")
    @JsonRawValue
    private String payload;

    public EmployeeMessageOutboxItem(String correlationId, String payload) {
        this.correlationId = correlationId;
        this.payload = payload;
    }
}
