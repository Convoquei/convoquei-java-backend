package br.com.convoquei.backend.core.organization.role.enums;

public enum OrganizationPermission {

    OWNER(0, "Permissao total", "Permissao total na organizacao"), // 1
    MEMBER(1, "Acessar organizacao", "Permite acessar e utilizar os recursos da organizacao"), // 2
    INVITE_MEMBERS(2, "Convidar membros", "Permite convidar novos membros para a organizacao"), // 4
    KICK_MEMBERS(3, "Remover membros", "Permite remover ou banir membros da organizacao"), // 8
    MANAGE_ROLES(4, "Gerenciar cargos", "Criar, editar, excluir cargos e atribuir cargos aos membros"), // 16
    MANAGE_GROUPS(5, "Gerenciar grupos", "Criar, editar, excluir grupos e gerenciar membros dos grupos"), // 32
    MANAGE_EVENTS(6, "Gerenciar eventos", "Criar, editar e excluir eventos da organizacao"), // 64
    MANAGE_EVENT_RECURRENCE(7, "Gerenciar recorrencias", "Criar, editar e remover recorrencias de eventos"), // 128
    MANAGE_EVENT_SCHEDULE(8, "Gerenciar escalas", "Abrir, fechar e editar escalas de participantes nos eventos"), // 256
    MANAGE_ORGANIZATION_SETTINGS(9, "Gerenciar configuracoes", "Alterar nome, foto e configuracoes gerais da organizacao"), // 512
    MANAGE_SUBSCRIPTION(10, "Gerenciar assinatura", "Alterar plano de assinatura e informacoes de pagamento"); // 1024


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