package org.alliancegenome.mati.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import lombok.Data;

@Entity(name = "Subdomain")
@Table(name = "subdomain")
@Data
@SequenceGenerator(name = "subdomain_id_seq", sequenceName = "subdomain_id_seq", allocationSize = 1)
public class SubdomainEntity extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "subdomain_id_seq")
    private Long id;
    @NotEmpty
    private String code;

    @NotEmpty
    private String name;

    private String description;
}
