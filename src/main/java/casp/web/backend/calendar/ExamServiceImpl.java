package casp.web.backend.calendar;

import casp.web.backend.calendar.data.Exam;
import casp.web.backend.calendar.data.ExamRepository;
import casp.web.backend.calendar.data.participants.ExamParticipant;
import casp.web.backend.common.enums.EntityStatus;
import casp.web.backend.common.reference.DogHasHandlerReferenceRepository;
import casp.web.backend.common.reference.MemberReferenceRepository;
import casp.web.backend.deprecated.event.BaseEventMigrationService;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static casp.web.backend.calendar.ExamMapper.EXAM_MAPPER;

@Service
class ExamServiceImpl extends BaseEventServiceImpl<Exam, ExamDto> implements ExamService {

    ExamServiceImpl(MemberReferenceRepository memberReferenceRepository,
                    ExamRepository examRepository,
                    BaseEventMigrationService migrationService,
                    DogHasHandlerReferenceRepository dogHasHandlerReferenceRepository) {
        super(memberReferenceRepository, examRepository, dogHasHandlerReferenceRepository, migrationService);
    }

    @Override
    public void save(ExamDto dto) {
        var exam = EXAM_MAPPER.toSource(dto);
        setCalendarEntriesAndMember(dto, exam);
        setParticipants(dto, exam);

        baseRepository.save(exam);
    }

    @Override
    public ExamDto getOneById(UUID id) {
        return EXAM_MAPPER.toTarget(getOneByIdOrThrowException(id));
    }

    private void setParticipants(ExamDto dto, Exam exam) {
        // 1. Get existing active participants from the DB event that are also desired in the DTO's list
        var existingParticipants = getExistingParticipantsMatchingDtoParticipantIds(dto);

        // 2. Determine which participant IDs from the DTO are genuinely new (not already linked to the existing event)
        var newParticipants = getNewParticipants(dto, existingParticipants);

        // 3. Combine the existing participants (those already in the DB and still desired) with the new ones
        exam.addParticipants(existingParticipants);
        exam.addParticipants(newParticipants);
    }

    private Stream<ExamParticipant> mapToParticipant(final UUID dogHasHandlerId) {
        return findDogHandlerReferenceById(dogHasHandlerId)
                        .map(ExamParticipant::new)
                .stream();
    }

    private Set<ExamParticipant> getExistingParticipantsMatchingDtoParticipantIds(final ExamDto dto) {
        return baseRepository.findOneByIdAndEntityStatus(dto.getId(), EntityStatus.ACTIVE)
                .stream()
                .flatMap(p -> p.getParticipants().stream())
                .filter(p -> dto.getParticipantIds().contains(p.getId()))
                .collect(Collectors.toSet());
    }

    private Set<ExamParticipant> getNewParticipants(final ExamDto dto, final Set<ExamParticipant> existingParticipants) {
        var existingParticipantIds = existingParticipants.stream().map(ExamParticipant::getId).collect(Collectors.toSet());
        return dto.getParticipantIds().stream()
                .filter(participantId -> !existingParticipantIds.contains(participantId))
                .flatMap(this::mapToParticipant)
                .collect(Collectors.toSet());
    }
}
