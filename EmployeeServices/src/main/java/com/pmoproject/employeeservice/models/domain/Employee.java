package com.pmoproject.employeeservice.models.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Class representing an item in the employee
 */
@Data
@Entity
public class Employee {
    @Id
    @Column(name = "EmployeeId")
    private int id;
    @Column(name = "Name")
    private String name;
    @OneToOne
    @JoinColumn(name = "AreaId")
    private Area area;
    @OneToOne
    @JoinColumn(name = "RoleId")
    private Role role;
    @OneToOne
    @JoinColumn(name = "LocationId")
    private Location location;
    @Column(name = "InPlace")
    private boolean inPlace;
    @Column(name = "Contract")
    private boolean contract;
    @Column(name = "StartDate")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime startDate;
    @Column(name = "EndDate")
    private LocalDateTime endDate;
    @Column(name = "Archived")
    private boolean archived;
}
