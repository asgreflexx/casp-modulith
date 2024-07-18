package at.unleashit.caspmodulith.services.calendar.model.configurations;

import at.unleashit.caspmodulith.services.calendar.model.events.Course;
import at.unleashit.caspmodulith.services.calendar.model.events.Event;
import at.unleashit.caspmodulith.services.calendar.model.events.Exam;
import at.unleashit.caspmodulith.services.calendar.model.participants.CoTrainer;
import at.unleashit.caspmodulith.services.calendar.model.participants.EventParticipant;
import at.unleashit.caspmodulith.services.calendar.model.participants.ExamParticipant;
import at.unleashit.caspmodulith.services.calendar.model.participants.Space;

public class TypesRegex {
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
