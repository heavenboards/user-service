package heavenboards.user.service.invitation.servce;

import heavenboards.user.service.invitation.domain.InvitationRepository;
import heavenboards.user.service.invitation.mapping.InvitationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import transfer.contract.domain.invitation.InvitationTo;
import transfer.contract.domain.user.UserTo;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Use case поиска приглашений пользователя в проекты.
 */
@Service
@RequiredArgsConstructor
public class InvitationFindUseCase {
    /**
     * Репозиторий для приглашений.
     */
    private final InvitationRepository invitationRepository;

    /**
     * Маппер для приглашений.
     */
    private final InvitationMapper invitationMapper;

    /**
     * Найти все приглашения, которые пришли этому пользователю в проекты.
     *
     * @return все найденные приглашения
     */
    public List<InvitationTo> findAllReceivedInvitations() {
        var user = (UserTo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return invitationRepository.findAllByInvitedUserId(user.getId()).stream()
            .map(entity -> invitationMapper.mapFromEntity(new InvitationTo(), entity))
            .collect(Collectors.toList());
    }

    /**
     * Найти все приглашения, которые присылал этот пользователь другим пользователям.
     *
     * @return все найденные приглашения
     */
    public List<InvitationTo> findAllSentInvitations() {
        var user = (UserTo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return invitationRepository.findAllByInvitationSenderId(user.getId()).stream()
            .map(entity -> invitationMapper.mapFromEntity(new InvitationTo(), entity))
            .collect(Collectors.toList());
    }
}
