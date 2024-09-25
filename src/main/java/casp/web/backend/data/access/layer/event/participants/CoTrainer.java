package casp.web.backend.data.access.layer.event.participants;

import casp.web.backend.data.access.layer.member.Member;
import com.querydsl.core.annotations.QueryEntity;
import jakarta.validation.Valid;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

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

    public Member getMember() {
        return member;
    }

    public void setMember(final Member member) {
        this.member = member;
    }
}
