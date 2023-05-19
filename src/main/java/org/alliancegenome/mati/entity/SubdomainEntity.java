package org.alliancegenome.mati.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import org.hibernate.Hibernate;

import java.util.Objects;

/** AGR has identifiers per subdomain (entity or class
 * for example: disease_annotation, gene, allele
 * */
@Entity(name = "Subdomain")
@Table(name = "subdomain")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        SubdomainEntity that = (SubdomainEntity) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
