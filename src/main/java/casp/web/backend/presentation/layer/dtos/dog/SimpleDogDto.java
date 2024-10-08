package casp.web.backend.presentation.layer.dtos.dog;

import java.util.UUID;

public class SimpleDogDto {
    private UUID id;
    private String name;

    public UUID getId() {
        return id;
    }

    public void setId(final UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }
}
