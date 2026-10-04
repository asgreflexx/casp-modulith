package casp.web.backend.dog.presentation;

import casp.web.backend.common.base.BaseView;
import casp.web.backend.common.enums.Gender;
import casp.web.backend.dog.DogRequiredFields;
import casp.web.backend.dog.data.EuropeNetState;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class DogRead extends BaseView implements DogRequiredFields {
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
