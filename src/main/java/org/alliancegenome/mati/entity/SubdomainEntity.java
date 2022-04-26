package org.alliancegenome.mati.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import lombok.Data;

@Entity(name = "Subdomain")
@Table(name = "subdomain")
@Data
public class SubdomainEntity extends PanacheEntity  {
    @NotEmpty
    private String code;

    @NotEmpty
    private String name;

    private String description;
}
