package org.alliancegenome.mati.entity;


import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;

@Entity(name = "Subdomain")
@Table(name = "subdomain")
@Data
public class SubdomainEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "code")
    @NotEmpty
    private String code;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;
}
