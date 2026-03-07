package br.com.convoquei.backend.domain.organization.organization.repository;

import br.com.convoquei.backend.sharedkernel.repository.BaseRepository;
import br.com.convoquei.backend.domain.organization.organization.model.entity.Organization;

import java.util.UUID;

public interface OrganizationRepository extends BaseRepository<Organization, UUID> {
    public boolean existsBySlug(String slug);
}
