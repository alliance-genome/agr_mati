package org.alliancegenome.mati.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.alliancegenome.mati.entity.SubdomainEntity;
import org.alliancegenome.mati.repository.SubdomainRepository;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
@AllArgsConstructor
@Slf4j
public class SubdomainService {
    private final SubdomainRepository subdomainRepository;

    public List<SubdomainEntity> findAll() {
        return subdomainRepository.findAll().list();
    }
}
