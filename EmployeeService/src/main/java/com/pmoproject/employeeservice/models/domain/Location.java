package com.pmoproject.employeeservice.models.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Class representing an employee's location
 */
@Entity
@Data
public class Location {
    @Id
    @Column(name = "LocationId")
    private int id;
    @Column(name = "Location")
    private String location;
}
