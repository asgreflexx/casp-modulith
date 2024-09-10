package casp.web.backend.business.logic.layer.events.types;

import casp.web.backend.business.logic.layer.events.calendar.CalendarService;
import casp.web.backend.business.logic.layer.events.participants.ExamParticipantService;
import casp.web.backend.data.access.layer.documents.event.participant.ExamParticipant;
import casp.web.backend.data.access.layer.documents.event.types.Exam;
import casp.web.backend.data.access.layer.repositories.BaseEventRepository;
import casp.web.backend.presentation.layer.dtos.events.ExamDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static casp.web.backend.presentation.layer.dtos.events.ExamMapper.EXAM_MAPPER;

@Service
class ExamServiceImpl extends BaseEventServiceImpl<Exam, ExamDto, ExamParticipant> implements ExamService {

    @Autowired
    ExamServiceImpl(final CalendarService calendarService,
                    final ExamParticipantService participantService,
                    final BaseEventRepository eventRepository) {
        super(calendarService, participantService, eventRepository, Exam.EVENT_TYPE, EXAM_MAPPER);
    }


    @Override
    public ExamDto createNewBaseEventWithOneCalendarEntry() {
        return super.createNewEventWithOneCalendarEntry(new ExamDto());
    }
}
