package casp.web.backend.business.logic.layer.events.participants;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BaseParticipantObserverImplTest {
    private static final UUID MEMBER_OR_HANDLER_ID = UUID.randomUUID();

    @Mock
    private CoTrainerService coTrainerService;
    @Mock
    private EventParticipantService eventParticipantService;
    @Mock
    private ExamParticipantService examParticipantService;
    @Mock
    private SpaceService spaceService;

    @InjectMocks
    private BaseParticipantObserverImpl baseParticipantObserver;

    @Test
    void deleteParticipantsByMemberOrHandlerId() {
        baseParticipantObserver.deleteParticipantsByMemberOrHandlerId(MEMBER_OR_HANDLER_ID);

        verify(coTrainerService).deleteParticipantsByMemberOrHandlerId(MEMBER_OR_HANDLER_ID);
        verify(eventParticipantService).deleteParticipantsByMemberOrHandlerId(MEMBER_OR_HANDLER_ID);
        verify(examParticipantService).deleteParticipantsByMemberOrHandlerId(MEMBER_OR_HANDLER_ID);
        verify(spaceService).deleteParticipantsByMemberOrHandlerId(MEMBER_OR_HANDLER_ID);
    }

    @Test
    void deactivateParticipantsByMemberOrHandlerId() {
        baseParticipantObserver.deactivateParticipantsByMemberOrHandlerId(MEMBER_OR_HANDLER_ID);

        verify(coTrainerService).deactivateParticipantsByMemberOrHandlerId(MEMBER_OR_HANDLER_ID);
        verify(eventParticipantService).deactivateParticipantsByMemberOrHandlerId(MEMBER_OR_HANDLER_ID);
        verify(examParticipantService).deactivateParticipantsByMemberOrHandlerId(MEMBER_OR_HANDLER_ID);
        verify(spaceService).deactivateParticipantsByMemberOrHandlerId(MEMBER_OR_HANDLER_ID);
    }

    @Test
    void activateParticipantsByMemberOrHandlerId() {
        baseParticipantObserver.activateParticipantsByMemberOrHandlerId(MEMBER_OR_HANDLER_ID);

        verify(coTrainerService).activateParticipantsByMemberOrHandlerId(MEMBER_OR_HANDLER_ID);
        verify(eventParticipantService).activateParticipantsByMemberOrHandlerId(MEMBER_OR_HANDLER_ID);
        verify(examParticipantService).activateParticipantsByMemberOrHandlerId(MEMBER_OR_HANDLER_ID);
        verify(spaceService).activateParticipantsByMemberOrHandlerId(MEMBER_OR_HANDLER_ID);
    }
}
