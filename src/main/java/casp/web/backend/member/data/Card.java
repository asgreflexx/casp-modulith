package casp.web.backend.member.data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@EqualsAndHashCode(of = "code")
@Setter
@Getter
public class Card {
    @Id
    @NotBlank
    private String code;
    @PositiveOrZero
    private double balance;
}
