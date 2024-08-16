package casp.web.backend.data.access.layer.entities;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Document
public class Card extends BaseEntity {

    private String code;

    //TODO: Michi darf NUll sein?
    private UUID memberId;

    private Double balance;

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

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "Card{" +
                "code='" + code + '\'' +
                ", memberId=" + memberId +
                ", balance=" + balance +
                '}';
    }
}
