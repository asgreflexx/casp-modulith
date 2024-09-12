package casp.web.backend.data.access.layer.member;

import casp.web.backend.data.access.layer.commons.BaseDocument;
import com.querydsl.core.annotations.QueryEntity;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.StringJoiner;
import java.util.UUID;

@QueryEntity
@Document
public class Card extends BaseDocument {

    @NotBlank
    private String code;

    /**
     * @deprecated Use {@link #member} instead.
     */
    @Deprecated(forRemoval = true)
    @NotNull
    private UUID memberId;

    //TODO @NotNull
    @Valid
    @DBRef
    private Member member;

    private double balance;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public UUID getMemberId() {
        return memberId;
    }

    public void setMemberId(UUID memberId) {
        this.memberId = memberId;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(final Member member) {
        this.member = member;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    @Override
    public boolean equals(final Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Card.class.getSimpleName() + "[", "]")
                .add("code='" + code + "'")
                .add("memberId=" + memberId)
                .add("member=" + member)
                .add("balance=" + balance)
                .add("id=" + id)
                .add("version=" + version)
                .add("createdBy='" + createdBy + "'")
                .add("created=" + created)
                .add("modifiedBy='" + modifiedBy + "'")
                .add("modified=" + modified)
                .add("entityStatus=" + entityStatus)
                .toString();
    }
}
