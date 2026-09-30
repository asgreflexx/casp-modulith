package casp.web.backend.dog;

import casp.web.backend.common.enums.Gender;
import casp.web.backend.dog.data.EuropeNetState;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public interface DogRequiredFields {
    @NotBlank
    String getName();

    void setName(@NotBlank String name);

    String getBreederName();

    void setBreederName(String breederName);

    String getBreedName();

    void setBreedName(String breedName);

    LocalDate getBirthDate();

    void setBirthDate(LocalDate birthDate);

    String getPedigree();

    void setPedigree(String pedigree);

    @NotNull
    Gender getGender();

    void setGender(@NotNull Gender gender);

    @Pattern(regexp = "\\d+$")
    String getChipNumber();

    void setChipNumber(@Pattern(regexp = "\\d+$") String chipNumber);

    LocalDate getRabiesDate();

    void setRabiesDate(LocalDate rabiesDate);

    @Positive
    Float getHeight();

    void setHeight(@Positive Float height);

    @NotBlank
    String getOwnerName();

    void setOwnerName(@NotBlank String ownerName);

    @NotBlank
    String getOwnerAddress();

    void setOwnerAddress(@NotBlank String ownerAddress);

    @NotNull
    EuropeNetState getEuropeNetState();

    void setEuropeNetState(@NotNull EuropeNetState europeNetState);
}
