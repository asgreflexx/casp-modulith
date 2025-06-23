package casp.web.backend.calendar;

import casp.web.backend.calendar.data.Exam;
import casp.web.backend.calendar.data.ExamRepository;
import casp.web.backend.calendar.data.participants.ExamParticipant;
import casp.web.backend.common.reference.DogHasHandlerReferenceRepository;
import casp.web.backend.common.reference.MemberReferenceRepository;
import casp.web.backend.deprecated.event.BaseEventMigrationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.stream.Stream;

import static casp.web.backend.calendar.ExamMapper.EXAM_MAPPER;

@Service
class ExamServiceImpl extends BaseEventServiceImpl<Exam, ExamDto, ExamParticipant> implements ExamService {
    private final ExamRepository examRepository;

    ExamServiceImpl(MemberReferenceRepository memberReferenceRepository,
                    ExamRepository examRepository,
                    BaseEventMigrationService migrationService,
                    DogHasHandlerReferenceRepository dogHasHandlerReferenceRepository) {
        super(memberReferenceRepository, examRepository, dogHasHandlerReferenceRepository, migrationService);
        this.examRepository = examRepository;
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

    @Override
    public Page<ExamDto> getExamsByDogHasHandlerId(final UUID dogHasHandlerId, final Pageable pageable) {
        var dogHasHandlerReference = findDogHandlerReferenceByIdOrThrowException(dogHasHandlerId);
        var examPage = examRepository.findAllByParticipant(new ExamParticipant(dogHasHandlerReference), pageable);
        return EXAM_MAPPER.toTargetPage(examPage);
    }

    @Override
    Stream<ExamParticipant> mapToParticipant(final UUID id) {
        return findDogHandlerReferenceById(id)
                .map(ExamParticipant::new)
                .stream();
    }
}
