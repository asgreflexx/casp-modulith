package casp.web.backend.member.data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.data.annotation.Id;

import java.util.Objects;


public class Card {
    @Id
    @NotBlank
    private String code;

    @PositiveOrZero
    private double balance;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Card card)) return false;
        return Objects.equals(code, card.code);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(code);
    }
}
