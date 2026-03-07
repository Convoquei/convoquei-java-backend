package br.com.convoquei.backend.core.organization.organization.repository;

import br.com.convoquei.backend.sharedkernel.repository.BaseRepository;
import br.com.convoquei.backend.core.organization.organization.entity.Organization;

import java.util.UUID;

public interface OrganizationRepository extends BaseRepository<Organization, UUID> {
    public boolean existsBySlug(String slug);
}
