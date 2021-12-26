package com.pmoproject.employeeservice.models.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Class representing an employee's role
 */
@Entity
@Data
public class Role {
    @Id
    @Column(name = "RoleId")
    private int id;
    @Column(name = "Role")
    private String role;
}
