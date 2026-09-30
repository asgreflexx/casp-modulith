package casp.web.backend.calendar;

import casp.web.backend.calendar.data.Exam;
import casp.web.backend.calendar.data.ExamRepository;
import casp.web.backend.calendar.data.participants.ExamParticipant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.stream.Stream;

import static casp.web.backend.calendar.ExamMapper.EXAM_MAPPER;

@Service
class ExamServiceImpl extends BaseEventServiceImpl<Exam, ExamDto, ExamParticipant, ExamRepository> implements ExamService {

    ExamServiceImpl(ExamRepository examRepository) {
        super(examRepository);
    }

    @Override
    public void save(ExamDto dto) {
        var exam = EXAM_MAPPER.toSource(dto);
        setCalendarEntriesAndMember(dto, exam);
        setParticipants(dto, exam);

        repository.save(exam);
    }

    @Override
    public ExamDto getOneById(UUID id) {
        return EXAM_MAPPER.toTarget(getOneByIdOrThrowException(id));
    }

    @Override
    public Page<ExamDto> getExamsByDogHasHandlerId(UUID dogHasHandlerId, Pageable pageable) {
        var examPage = repository.findAllByParticipantId(dogHasHandlerId, pageable);
        return EXAM_MAPPER.toTargetPage(examPage);
    }

    @Override
    Stream<ExamParticipant> mapToParticipant(UUID id) {
        return findDogHandlerReferenceById(id).map(ExamParticipant::new).stream();
    }
}
