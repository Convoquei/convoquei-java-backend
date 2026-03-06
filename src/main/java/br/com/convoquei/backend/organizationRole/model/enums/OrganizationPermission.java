package br.com.convoquei.backend.organizationRole.model.enums;

public enum OrganizationPermission {

    OWNER(0, "Permissão total", "Permissão total na organização"),// 1
    MEMBER(1, "Membro", "Utilizar recursos da organização"), //2
    INVITE_ORGANIZATION_MEMBERS(2, "Convidar Membros", "Convidar membros para a organização"), // 4
    KICK_ORGANIZATION_MEMBER(3, "Remover Membros", "Excluir e banir participantes da organização"), // 8
    MANAGE_GROUPS(4, "Gerenciar Grupos", "Criar, editar e deletar grupos da organização"); // 16

    private final int bitIndex;
    private final String title;
    private final String description;

    OrganizationPermission(int bitIndex, String title, String description) {
        this.bitIndex = bitIndex;
        this.title = title;
        this.description = description;
    }

    public boolean isOwner() { return this == OWNER; }

    public long mask() { return 1L << bitIndex; }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
}