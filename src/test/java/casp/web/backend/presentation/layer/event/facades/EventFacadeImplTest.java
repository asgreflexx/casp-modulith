package casp.web.backend.presentation.layer.event.facades;

import casp.web.backend.TestFixture;
import casp.web.backend.business.logic.layer.event.participants.EventParticipantService;
import casp.web.backend.business.logic.layer.event.participants.ParticipantMember;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class EventFacadeImplTest {
    @Mock
    private EventParticipantService eventService;

    @InjectMocks
    private EventFacadeImpl eventFacade;

    @Test
    void mapBaseEventToDto() {
        var eventParticipant = TestFixture.createValidEventParticipant();
        var event = eventParticipant.getBaseEvent();
        var member = event.getMember();
        eventParticipant.setMemberOrHandlerId(member.getId());

        when(eventService.getActiveEventParticipantsIfMembersAreActive(event.getId())).thenReturn(Set.of(new ParticipantMember(eventParticipant, member)));

        var eventDto = eventFacade.mapBaseEventToDto(event);

        assertThat(eventDto.getParticipants())
                .singleElement()
                .satisfies(actual -> {
                    assertEquals(eventParticipant.getId(), actual.getId());
                    assertEquals(eventParticipant.getMemberOrHandlerId(), actual.getMember().getId());
                    assertNull(actual.getBaseEvent());
                });
    }
}
