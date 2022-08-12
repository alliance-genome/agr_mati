package org.alliancegenome.mati.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Identifier {
    private Long counter;
    private String subdomain_code;
    private String subdomain_name;
    private String curie;
}
