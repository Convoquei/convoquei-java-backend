package br.com.convoquei.backend.core.organization.event.repository;

import br.com.convoquei.backend.core.organization.event.entity.OrganizationEvent;
import br.com.convoquei.backend.sharedkernel.repository.BaseRepository;

import java.util.UUID;

public interface OrganizationEventRepository extends BaseRepository<OrganizationEvent, UUID>, OrganizationEventRepositoryCustom {
}