package at.unleashit.caspmodulith.services.calendar.model.configurations;

import at.unleashit.caspmodulith.enums.EntityStatus;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class BaseEntity {
    @Id
    protected UUID id = UUID.randomUUID();

    @Version
    protected long version;

    @CreatedBy
    protected String createdBy;

    @CreatedDate
    protected LocalDateTime created;

    @LastModifiedBy
    protected String modifiedBy;

    @LastModifiedDate
    protected LocalDateTime modified;

    @NotNull
    protected EntityStatus entityStatus = EntityStatus.ACTIVE;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public long getVersion() {
        return version;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public LocalDateTime getModified() {
        return modified;
    }

    public EntityStatus getEntityStatus() {
        return entityStatus;
    }

    public void setEntityStatus(EntityStatus entityStatus) {
        this.entityStatus = entityStatus;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public void setModified(LocalDateTime modified) {
        this.modified = modified;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseEntity that = (BaseEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
