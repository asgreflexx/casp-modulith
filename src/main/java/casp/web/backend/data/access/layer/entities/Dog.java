package casp.web.backend.data.access.layer.entities;

import casp.web.backend.data.access.layer.enumerations.EuropeNetState;
import casp.web.backend.data.access.layer.enumerations.Gender;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document
public class Dog extends BaseEntity {

    @NotBlank
    private String name;

    private String breederName;

    private String breedName;

    private LocalDate birthDate;

    private String pedigree;

    private Gender gender = Gender.FEMALE;

    private String chipNumber;

    private LocalDate rabiesDate;

    private Float height;

    @NotBlank
    private String ownerName;

    @NotBlank
    private String ownerAddress;

    private EuropeNetState europetnetState = EuropeNetState.NOT_CHECKED;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBreederName() {
        return breederName;
    }

    public void setBreederName(String breederName) {
        this.breederName = breederName;
    }

    public String getBreedName() {
        return breedName;
    }

    public void setBreedName(String breedName) {
        this.breedName = breedName;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getPedigree() {
        return pedigree;
    }

    public void setPedigree(String pedigree) {
        this.pedigree = pedigree;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getChipNumber() {
        return chipNumber;
    }

    public void setChipNumber(String chipNumber) {
        this.chipNumber = chipNumber;
    }

    public LocalDate getRabiesDate() {
        return rabiesDate;
    }

    public void setRabiesDate(LocalDate rabiesDate) {
        this.rabiesDate = rabiesDate;
    }

    public Float getHeight() {
        return height;
    }

    public void setHeight(Float height) {
        this.height = height;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerAddress() {
        return ownerAddress;
    }

    public void setOwnerAddress(String ownerAddress) {
        this.ownerAddress = ownerAddress;
    }

    public EuropeNetState getEuropetnetState() {
        return europetnetState;
    }

    public void setEuropetnetState(EuropeNetState europetnetState) {
        this.europetnetState = europetnetState;
    }

    @Override
    public String toString() {
        return "Dog{" +
                "name='" + name + '\'' +
                ", breederName='" + breederName + '\'' +
                ", breedName='" + breedName + '\'' +
                ", birthDate=" + birthDate +
                ", pedigree='" + pedigree + '\'' +
                ", gender=" + gender +
                ", chipNumber='" + chipNumber + '\'' +
                ", rabiesDate=" + rabiesDate +
                ", height=" + height +
                ", ownerName='" + ownerName + '\'' +
                ", ownerAddress='" + ownerAddress + '\'' +
                ", europetnet='" + europetnetState + '\'' +
                '}';
    }
}
