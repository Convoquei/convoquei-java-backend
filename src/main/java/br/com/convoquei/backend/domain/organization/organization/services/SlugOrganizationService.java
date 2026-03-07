package br.com.convoquei.backend.domain.organization.organization.services;

import br.com.convoquei.backend.domain.organization.organization.repository.OrganizationRepository;
import org.springframework.stereotype.Service;

@Service
public class SlugOrganizationService {
    private final OrganizationRepository organizationRepository;

    public SlugOrganizationService(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    public String generate(String name) {
        return name.toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", "");
    }

    public boolean isUnique(String slug) {
        return !organizationRepository.existsBySlug(slug);
    }
}
