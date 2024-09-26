package casp.web.backend.presentation.layer.dtos.member;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public class CardDto {
    private UUID id;
    private long version;
    private LocalDateTime created;
    private LocalDateTime modified;

    @NotBlank
    private String code;

    @NotNull
    private UUID memberId;

    public UUID getId() {
        return id;
    }

    public void setId(final UUID id) {
        this.id = id;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(final long version) {
        this.version = version;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(final LocalDateTime created) {
        this.created = created;
    }

    public LocalDateTime getModified() {
        return modified;
    }

    public void setModified(final LocalDateTime modified) {
        this.modified = modified;
    }

    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    public UUID getMemberId() {
        return memberId;
    }

    public void setMemberId(final UUID memberId) {
        this.memberId = memberId;
    }
}
