package com.pmoproject.employeeservice.models.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Class representing an employee's area
 */
@Entity
@Data
public class Area {
    @Id
    @Column(name = "AreaId")
    private int id;
    @Column(name = "Area")
    private String area;
}
