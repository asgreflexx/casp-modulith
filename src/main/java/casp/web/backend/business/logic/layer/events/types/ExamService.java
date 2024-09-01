package casp.web.backend.business.logic.layer.events.types;


import casp.web.backend.business.logic.layer.events.dtos.ExamDto;
import casp.web.backend.data.access.layer.documents.event.participant.ExamParticipant;
import casp.web.backend.data.access.layer.documents.event.types.Exam;

public interface ExamService extends BaseEventService<Exam, ExamParticipant, ExamDto> {

}
