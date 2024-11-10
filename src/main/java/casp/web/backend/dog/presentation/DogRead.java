package casp.web.backend.dog.presentation;

import casp.web.backend.business.logic.layer.event.types.SpaceDto;
import casp.web.backend.common.base.BaseView;
import casp.web.backend.common.enums.Gender;
import casp.web.backend.dog.DogDtoRequiredFields;
import casp.web.backend.dog.DogHasHandler;
import casp.web.backend.dog.data.EuropeNetState;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class DogRead extends BaseView implements DogDtoRequiredFields {
    private String name;
    private String breederName;
    private String breedName;
    private LocalDate birthDate;
    private String pedigree;
    private Gender gender = Gender.FEMALE;
    private String chipNumber;
    private LocalDate rabiesDate;
    private float height;
    private String ownerName;
    private String ownerAddress;
    private EuropeNetState europeNetState = EuropeNetState.NOT_CHECKED;
    private Set<DogHasHandler> dogHasHandlerSet = new HashSet<>();
    private Set<SpaceDto> spaces;

    @Override
    public Set<DogHasHandler> getDogHasHandlerSet() {
        return dogHasHandlerSet;
    }

    @Override
    public void setDogHasHandlerSet(Set<DogHasHandler> dogHasHandlerSet) {
        this.dogHasHandlerSet = dogHasHandlerSet;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getBreederName() {
        return breederName;
    }

    @Override
    public void setBreederName(String breederName) {
        this.breederName = breederName;
    }

    @Override
    public String getBreedName() {
        return breedName;
    }

    @Override
    public void setBreedName(String breedName) {
        this.breedName = breedName;
    }

    @Override
    public LocalDate getBirthDate() {
        return birthDate;
    }

    @Override
    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    @Override
    public String getPedigree() {
        return pedigree;
    }

    @Override
    public void setPedigree(String pedigree) {
        this.pedigree = pedigree;
    }

    @Override
    public Gender getGender() {
        return gender;
    }

    @Override
    public void setGender(Gender gender) {
        this.gender = gender;
    }

    @Override
    public String getChipNumber() {
        return chipNumber;
    }

    @Override
    public void setChipNumber(String chipNumber) {
        this.chipNumber = chipNumber;
    }

    @Override
    public LocalDate getRabiesDate() {
        return rabiesDate;
    }

    @Override
    public void setRabiesDate(LocalDate rabiesDate) {
        this.rabiesDate = rabiesDate;
    }

    @Override
    public float getHeight() {
        return height;
    }

    @Override
    public void setHeight(float height) {
        this.height = height;
    }

    @Override
    public String getOwnerName() {
        return ownerName;
    }

    @Override
    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    @Override
    public String getOwnerAddress() {
        return ownerAddress;
    }

    @Override
    public void setOwnerAddress(String ownerAddress) {
        this.ownerAddress = ownerAddress;
    }

    @Override
    public EuropeNetState getEuropeNetState() {
        return europeNetState;
    }

    @Override
    public void setEuropeNetState(EuropeNetState europeNetState) {
        this.europeNetState = europeNetState;
    }

    @Override
    public Set<SpaceDto> getSpaces() {
        return spaces;
    }

    @Override
    public void setSpaces(Set<SpaceDto> spaces) {
        this.spaces = spaces;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
