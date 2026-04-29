package casp.web.backend.calendar;

import casp.web.backend.calendar.data.CalendarRepository;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
class CalendarServiceImpl implements CalendarService {
    private final CalendarRepository calendarRepository;

    CalendarServiceImpl(CalendarRepository calendarRepository) {
        this.calendarRepository = calendarRepository;
    }

    @Override
    public List<CalendarEntryDto> findCalendarEntriesByFromAndToAndMemberId(OffsetDateTime from, OffsetDateTime to, UUID memberId) {
        return calendarRepository
                .findCalendarEntriesByFromAndToAndMemberId(from, to, memberId)
                .stream()
                .flatMap(cep -> cep
                        .calendarEntries()
                        .stream()
                        .map(ce -> new CalendarEntryDto(ce, cep.id(), cep.eventType(), cep.name())))
                .toList();
    }
}
