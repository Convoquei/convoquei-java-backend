package br.com.convoquei.backend.organizationMember.services;
import br.com.convoquei.backend._shared.exceptions.DomainBusinessRuleException;
import br.com.convoquei.backend._shared.exceptions.DomainNotFoundException;
import br.com.convoquei.backend._shared.model.dto.response.PagedResponse;
import br.com.convoquei.backend.organizationGroup.model.dto.response.MemberGroupSummaryResponse;
import br.com.convoquei.backend.organizationGroup.repository.OrganizationGroupParticipantRepository;
import br.com.convoquei.backend.organizationMember.model.dto.request.ListMembersByOrganizationRequest;
import br.com.convoquei.backend.organizationMember.model.dto.response.MyMemberhipResponse;
import br.com.convoquei.backend.organizationMember.model.dto.response.OrganizationMemberResponse;
import br.com.convoquei.backend.organizationMember.model.entity.OrganizationMember;
import br.com.convoquei.backend.organizationMember.model.enums.OrganizationMemberStatus;
import br.com.convoquei.backend.organizationMember.repository.OrganizationMemberRepository;
import br.com.convoquei.backend.organizationMember.repository.specifications.OrganizationMemberSpec;
import br.com.convoquei.backend.organizationRole.model.enums.OrganizationPermission;
import br.com.convoquei.backend.user.provider.CurrentUserProvider;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
@Service
public class OrganizationMemberService {
    private final CurrentUserProvider currentUserProvider;
    private final OrganizationMemberRepository organizationMemberRepository;
    private final OrganizationGroupParticipantRepository organizationGroupParticipantRepository;

    public OrganizationMemberService(CurrentUserProvider currentUserProvider, OrganizationMemberRepository organizationMemberRepository, OrganizationGroupParticipantRepository organizationGroupParticipantRepository) {
        this.currentUserProvider = currentUserProvider;
        this.organizationMemberRepository = organizationMemberRepository;
        this.organizationGroupParticipantRepository = organizationGroupParticipantRepository;
    }

    public MyMemberhipResponse getMembership(UUID organizationId) {
        OrganizationMember member = currentUserProvider.requireOrganizationMembership(organizationId);
        return MyMemberhipResponse.fromMember(member);

    }

    @Transactional
    public OrganizationMemberResponse kickMemberOfOrganization(UUID organizationId, UUID memberId) {
        OrganizationMember memberAdmin = currentUserProvider.requireOrganizationMembership(organizationId);

        if (!memberAdmin.hasPermission(OrganizationPermission.KICK_ORGANIZATION_MEMBER))
            throw new DomainBusinessRuleException("Voc� n�o tem permiss�o para expulsar membros dessa organiza��o.");

        OrganizationMember memberToKick = organizationMemberRepository.findByIdAndOrganizationIdAndStatus(memberId, organizationId, OrganizationMemberStatus.ACTIVE)
                .orElseThrow(() -> new DomainNotFoundException("Membro n�o encontrado nessa organiza��o."));

        if (memberToKick.getId().equals(memberAdmin.getId())) {
            if (memberToKick.isOwner())
                throw new DomainBusinessRuleException("Voc� n�o pode abandonar a organiza��o enquanto for o dono. Transfira a propriedade ou exclua a organiza��o para sair.");

            throw new DomainBusinessRuleException("Voc� n�o pode se expulsar da organiza��o, ao em vez disso, abandone-a.");
        }

        if (memberToKick.isOwner())
            throw new DomainBusinessRuleException("O dono da organiza��o n�o pode ser expulso.");

        memberToKick.markAsAbandoned();
        organizationGroupParticipantRepository.deleteAllByOrganizationMemberId(memberToKick.getId());
        organizationMemberRepository.save(memberToKick);

        return OrganizationMemberResponse.buildFromMember(memberToKick);
    }

    public PagedResponse<OrganizationMemberResponse> listMembers(UUID organizationId, ListMembersByOrganizationRequest request) {
        Specification<OrganizationMember> spec = OrganizationMemberSpec.fetchUserAndRoles()
                .and(OrganizationMemberSpec.organizationId(organizationId))
                .and(OrganizationMemberSpec.from(request));

        var sort = Sort.by("user.name").ascending().and(Sort.by("joinedAt").ascending());

        var membersPage = organizationMemberRepository.findAll(spec, request.toPageRequest(sort));

        List<UUID> memberIds = membersPage.getContent().stream()
                .map(OrganizationMember::getId)
                .toList();

        Map<UUID, List<MemberGroupSummaryResponse>> groupsByMemberId = organizationGroupParticipantRepository
                .findAllByOrganizationMemberIdIn(memberIds)
                .stream()
                .collect(Collectors.groupingBy(
                        p -> p.getOrganizationMember().getId(),
                        Collectors.mapping(
                                p -> new MemberGroupSummaryResponse(
                                        p.getOrganizationGroup().getId(),
                                        p.getOrganizationGroup().getName(),
                                        p.getOrganizationGroup().getEmoji()
                                ),
                                Collectors.toList()
                        )
                ));

        var page = membersPage.map(member ->
                OrganizationMemberResponse.buildFromMember(
                        member,
                        groupsByMemberId.getOrDefault(member.getId(), List.of())
                )
        );

        return PagedResponse.buildFromPage(page);
    }
}
