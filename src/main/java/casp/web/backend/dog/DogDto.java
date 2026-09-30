package casp.web.backend.dog;

import casp.web.backend.common.base.BaseDto;
import casp.web.backend.common.enums.Gender;
import casp.web.backend.dog.data.EuropeNetState;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class DogDto extends BaseDto implements DogRequiredFields {
    private String name;
    private String breederName;
    private String breedName;
    private LocalDate birthDate;
    private String pedigree;
    private Gender gender = Gender.FEMALE;
    private String chipNumber;
    private LocalDate rabiesDate;
    private Float height;
    private String ownerName;
    private String ownerAddress;
    private EuropeNetState europeNetState = EuropeNetState.NOT_CHECKED;
}
