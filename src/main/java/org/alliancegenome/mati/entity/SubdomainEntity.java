package org.alliancegenome.mati.entity;


import io.quarkus.hibernate.orm.panache.PanacheEntity;
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
public class SubdomainEntity extends PanacheEntity  {
    @Column @NotEmpty
    private String code;

    @Column @NotEmpty
    private String name;

    @Column
    private String description;
}
