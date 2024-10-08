package casp.web.backend.business.logic.layer.event.types;

import casp.web.backend.business.logic.layer.event.calendar.CalendarService;
import casp.web.backend.business.logic.layer.event.participants.ExamParticipantService;
import casp.web.backend.data.access.layer.event.participants.ExamParticipant;
import casp.web.backend.data.access.layer.event.types.BaseEventRepository;
import casp.web.backend.data.access.layer.event.types.Exam;
import casp.web.backend.data.access.layer.member.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
class ExamServiceImpl extends BaseEventServiceImpl<Exam, ExamParticipant> implements ExamService {

    @Autowired
    ExamServiceImpl(final CalendarService calendarService,
                    final ExamParticipantService participantService,
                    final BaseEventRepository eventRepository,
                    final MemberRepository memberRepository) {
        super(calendarService, participantService, eventRepository, memberRepository, Exam.EVENT_TYPE);
    }
}
