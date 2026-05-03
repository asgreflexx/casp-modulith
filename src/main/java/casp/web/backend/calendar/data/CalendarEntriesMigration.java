package casp.web.backend.calendar.data;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Deprecated(forRemoval = true, since = "2026-04-23")
@Component
@Slf4j
class CalendarEntriesMigration {
    private final EventRepository eventRepository;
    private final CourseRepository courseRepository;
    private final ExamRepository examRepository;

    CalendarEntriesMigration(EventRepository eventRepository, CourseRepository courseRepository, ExamRepository examRepository) {
        this.eventRepository = eventRepository;
        this.courseRepository = courseRepository;
        this.examRepository = examRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    void migrate() {
        var isMigrationFinished = true;
        do {
            isMigrationFinished = this.eventRepository.migrateLocaDateTimeToOffsetDateTime();
        } while (!isMigrationFinished);
        log.info("Event calendar entries migration finished");

        do {
            isMigrationFinished = this.courseRepository.migrateLocaDateTimeToOffsetDateTime();
        } while (!isMigrationFinished);
        log.info("Course calendar entries migration finished");

        do {
            isMigrationFinished = this.examRepository.migrateLocaDateTimeToOffsetDateTime();
        } while (!isMigrationFinished);
        log.info("Exam calendar entries migration finished");
    }
}
