package casp.web.backend.data.access.layer.event;

import casp.web.backend.data.access.layer.event.participants.CoTrainer;
import casp.web.backend.data.access.layer.event.participants.EventParticipant;
import casp.web.backend.data.access.layer.event.participants.ExamParticipant;
import casp.web.backend.data.access.layer.event.participants.Space;
import casp.web.backend.data.access.layer.event.types.Course;
import casp.web.backend.data.access.layer.event.types.Event;
import casp.web.backend.data.access.layer.event.types.Exam;

public final class TypesRegex {
    public static final String BASE_EVENT_TYPES_REGEX = "^" + Event.EVENT_TYPE
            + "|" + Course.EVENT_TYPE
            + "|" + Exam.EVENT_TYPE + "$";
    public static final String BASE_PARTICIPANT_TYPES_REGEX = "^" + CoTrainer.PARTICIPANT_TYPE
            + "|" + EventParticipant.PARTICIPANT_TYPE
            + "|" + ExamParticipant.PARTICIPANT_TYPE
            + "|" + Space.PARTICIPANT_TYPE + "$";

    private TypesRegex() {
    }
}
