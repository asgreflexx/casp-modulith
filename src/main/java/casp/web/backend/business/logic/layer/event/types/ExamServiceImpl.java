package casp.web.backend.business.logic.layer.event.types;

import casp.web.backend.common.enums.EntityStatus;
import casp.web.backend.common.reference.DogHasHandlerReferenceRepository;
import casp.web.backend.common.reference.MemberReferenceRepository;
import casp.web.backend.data.access.layer.event.participants.ExamParticipant;
import casp.web.backend.data.access.layer.event.types.Exam;
import casp.web.backend.data.access.layer.event.types.ExamRepository;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

import static casp.web.backend.business.logic.layer.event.types.ExamMapper.EXAM_MAPPER;

@Service
class ExamServiceImpl extends BaseEventServiceImpl<Exam, ExamDto> implements ExamService {
    private final DogHasHandlerReferenceRepository dogHasHandlerReferenceRepository;

    ExamServiceImpl(final MemberReferenceRepository memberReferenceRepository,
                    final ExamRepository examRepository,
                    final BaseEventMigrationService migrationService,
                    final DogHasHandlerReferenceRepository dogHasHandlerReferenceRepository) {
        super(memberReferenceRepository, examRepository, migrationService);
        this.dogHasHandlerReferenceRepository = dogHasHandlerReferenceRepository;
    }

    @Override
    public void save(final ExamDto dto) {
        var exam = EXAM_MAPPER.toSource(dto);
        setCalendarEntries(dto, exam);
        setMember(dto, exam);
        setParticipants(dto, exam);

        baseRepository.setMetadataAndSave(exam);
    }

    private void setParticipants(final ExamDto dto, final Exam exam) {
        var actualParticipants = dto.getParticipants();
        var newParticipants = dto.getNewParticipants()
                .stream()
                .flatMap(id -> dogHasHandlerReferenceRepository.findOneByIdAndEntityStatus(id, EntityStatus.ACTIVE)
                        .map(ExamParticipant::new)
                        .stream())
                .collect(Collectors.toSet());

        actualParticipants.addAll(newParticipants);
        exam.setParticipants(actualParticipants);
    }
}
