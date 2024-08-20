package casp.web.backend.data.access.layer.documents.event;

import casp.web.backend.data.access.layer.documents.event.participant.CoTrainer;
import casp.web.backend.data.access.layer.documents.event.participant.EventParticipant;
import casp.web.backend.data.access.layer.documents.event.participant.ExamParticipant;
import casp.web.backend.data.access.layer.documents.event.participant.Space;
import casp.web.backend.data.access.layer.documents.event.types.Course;
import casp.web.backend.data.access.layer.documents.event.types.Event;
import casp.web.backend.data.access.layer.documents.event.types.Exam;

public final class TypesRegex {

    private TypesRegex() {
    }

    public static final String BASE_EVENT_TYPES_REGEX = "^" + Event.EVENT_TYPE
            + "|" + Course.EVENT_TYPE
            + "|" + Exam.EVENT_TYPE + "$";
    public static final String BASE_PARTICIPANT_TYPES_REGEX = "^" + CoTrainer.PARTICIPANT_TYPE
            + "|" + EventParticipant.PARTICIPANT_TYPE
            + "|" + ExamParticipant.PARTICIPANT_TYPE
            + "|" + Space.PARTICIPANT_TYPE + "$";

}
