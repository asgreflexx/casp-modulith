package casp.web.backend.business.logic.layer.events.types;


import casp.web.backend.data.access.layer.documents.event.participant.ExamParticipant;
import casp.web.backend.data.access.layer.documents.event.types.Exam;
import casp.web.backend.presentation.layer.dtos.events.ExamDto;

public interface ExamService extends BaseEventService<Exam, ExamParticipant, ExamDto> {

}
