package casp.web.backend.deprecated.event.participants;

import casp.web.backend.data.access.layer.member.Member;
import casp.web.backend.deprecated.event.types.Course;
import com.querydsl.core.annotations.QueryEntity;
import jakarta.validation.Valid;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @deprecated use {@link casp.web.backend.data.access.layer.event.participants.CoTrainer} instead.It will be removed in #3.
 */
@Deprecated(forRemoval = true, since = "0.0.0")
@QueryEntity
@Document(BaseParticipant.COLLECTION)
@TypeAlias(CoTrainer.PARTICIPANT_TYPE)
public class CoTrainer extends BaseParticipant {
    public static final String PARTICIPANT_TYPE = "CO_TRAINER";

    @Valid
    @DBRef
    private Member member;

    public CoTrainer() {
        super(PARTICIPANT_TYPE);
    }

    public CoTrainer(Course course, Member member) {
        super(PARTICIPANT_TYPE, member.getId(), course);
        this.member = member;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }
}
