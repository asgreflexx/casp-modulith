package casp.web.backend.presentation.layer.event.facades;

import casp.web.backend.TestFixture;
import casp.web.backend.business.logic.layer.event.participants.EventParticipantService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class EventFacadeImplTest {
    @Mock
    private EventParticipantService eventService;

    @InjectMocks
    private EventFacadeImpl eventFacade;

    @Nested
    class MapDocumentToDto {
        @Test
        void mapParticipant() {
            var eventParticipant = TestFixture.createEventParticipant();
            var event = eventParticipant.getBaseEvent();
            var member = event.getMember();
            eventParticipant.setMemberOrHandlerId(member.getId());
            eventParticipant.setMember(member);

            when(eventService.getActiveParticipantsIfMembersOrDogHasHandlerAreActive(event.getId())).thenReturn(Set.of(eventParticipant));

            var eventDto = eventFacade.mapBaseEventToDto(event);

            assertThat(eventDto.getParticipants())
                    .singleElement()
                    .satisfies(actual -> {
                        assertEquals(eventParticipant.getId(), actual.getId());
                        assertEquals(eventParticipant.getMemberOrHandlerId(), actual.getMember().getId());
                    });
        }

        @Test
        void mapDailyEventOption() {
            var dailyEventOption = TestFixture.createDailyEventOption();
            var event = TestFixture.createEvent();
            event.setDailyOption(dailyEventOption);

            var eventDto = eventFacade.mapBaseEventToDto(event);

            assertEquals(dailyEventOption.getOptionType(), eventDto.getOption().getOptionType());
        }

        @Test
        void mapWeeklyEventOption() {
            var weeklyEventOption = TestFixture.createWeeklyEventOption();
            var event = TestFixture.createEvent();
            event.setWeeklyOption(weeklyEventOption);

            var eventDto = eventFacade.mapBaseEventToDto(event);

            assertEquals(weeklyEventOption.getOptionType(), eventDto.getOption().getOptionType());
        }
    }
}
