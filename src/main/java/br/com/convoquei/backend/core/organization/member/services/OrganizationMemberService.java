package br.com.convoquei.backend.core.organization.member.services;
import br.com.convoquei.backend.sharedkernel.exceptions.DomainBusinessRuleException;
import br.com.convoquei.backend.sharedkernel.exceptions.DomainNotFoundException;
import br.com.convoquei.backend.app.api.dto.response.PagedResponse;
import br.com.convoquei.backend.core.organization.group.api.dto.response.MemberGroupSummaryResponse;
import br.com.convoquei.backend.core.organization.group.entity.OrganizationGroup;
import br.com.convoquei.backend.core.organization.group.entity.OrganizationGroupParticipant;
import br.com.convoquei.backend.core.organization.group.repository.OrganizationGroupParticipantRepository;
import br.com.convoquei.backend.core.organization.group.repository.OrganizationGroupRepository;
import br.com.convoquei.backend.core.organization.member.api.dto.request.ListMembersByOrganizationRequest;
import br.com.convoquei.backend.core.organization.member.api.dto.request.UpdateMemberGroupsRequest;
import br.com.convoquei.backend.core.organization.member.api.dto.response.MyMemberhipResponse;
import br.com.convoquei.backend.core.organization.member.api.dto.response.OrganizationMemberResponse;
import br.com.convoquei.backend.core.organization.member.entity.OrganizationMember;
import br.com.convoquei.backend.core.organization.member.enums.OrganizationMemberStatus;
import br.com.convoquei.backend.core.organization.member.repository.OrganizationMemberRepository;
import br.com.convoquei.backend.core.organization.member.repository.specifications.OrganizationMemberSpec;
import br.com.convoquei.backend.core.organization.role.enums.OrganizationPermission;
import br.com.convoquei.backend.core.user.provider.CurrentUserProvider;
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
    private final OrganizationGroupRepository organizationGroupRepository;

    public OrganizationMemberService(CurrentUserProvider currentUserProvider, OrganizationMemberRepository organizationMemberRepository, OrganizationGroupParticipantRepository organizationGroupParticipantRepository, OrganizationGroupRepository organizationGroupRepository) {
        this.currentUserProvider = currentUserProvider;
        this.organizationMemberRepository = organizationMemberRepository;
        this.organizationGroupParticipantRepository = organizationGroupParticipantRepository;
        this.organizationGroupRepository = organizationGroupRepository;
    }

    public MyMemberhipResponse getMembership(UUID organizationId) {
        OrganizationMember member = currentUserProvider.requireOrganizationMembership(organizationId);
        return MyMemberhipResponse.fromMember(member);

    }

    @Transactional
    public OrganizationMemberResponse kickMemberOfOrganization(UUID organizationId, UUID memberId) {
        OrganizationMember memberAdmin = currentUserProvider.requireOrganizationMembership(organizationId);

        if (!memberAdmin.hasPermission(OrganizationPermission.KICK_MEMBERS))
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

    @Transactional
    public OrganizationMemberResponse updateMemberGroups(UUID organizationId, UUID memberId, UpdateMemberGroupsRequest request) {
        OrganizationMember requester = currentUserProvider.requireOrganizationMembership(organizationId);
        if (!requester.hasPermission(OrganizationPermission.MANAGE_GROUPS))
            throw new DomainBusinessRuleException("Você não tem permissão para gerenciar grupos desta organização.");

        OrganizationMember member = organizationMemberRepository
                .findByIdAndOrganizationIdAndStatus(memberId, organizationId, OrganizationMemberStatus.ACTIVE)
                .orElseThrow(() -> new DomainNotFoundException("Membro não encontrado nessa organização."));

        List<UUID> groupIds = request.groupIds() != null ? request.groupIds() : List.of();

        List<OrganizationGroup> groups = groupIds.isEmpty()
                ? List.of()
                : organizationGroupRepository.findAllByIdInAndOrganizationId(groupIds, organizationId);

        if (groups.size() != groupIds.stream().distinct().count())
            throw new DomainNotFoundException("Um ou mais grupos não foram encontrados nessa organização.");

        organizationGroupParticipantRepository.deleteAllByOrganizationMemberIdAndOrganizationId(memberId, organizationId);
        organizationGroupParticipantRepository.flush();

        List<OrganizationGroupParticipant> newParticipants = groups.stream()
                .map(group -> new OrganizationGroupParticipant(group, member.getOrganization(), member))
                .toList();

        organizationGroupParticipantRepository.saveAll(newParticipants);

        List<MemberGroupSummaryResponse> groupSummaries = groups.stream()
                .map(g -> new MemberGroupSummaryResponse(g.getId(), g.getName(), g.getEmoji()))
                .toList();

        return OrganizationMemberResponse.buildFromMember(member, groupSummaries);
    }
}
