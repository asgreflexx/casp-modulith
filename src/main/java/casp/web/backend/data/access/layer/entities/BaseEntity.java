package casp.web.backend.data.access.layer.entities;

import casp.web.backend.data.access.layer.enumerations.EntityStatus;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;

import java.time.LocalDateTime;
import java.util.UUID;

public abstract class BaseEntity {
    @Id
    protected UUID id = UUID.randomUUID();

    @Version
    protected long version;

    // FIXME This should be added automatically
    @CreatedBy
    protected String createdBy = "Bonsai";

    @CreatedDate
    protected LocalDateTime created;

    // FIXME This should be added automatically
    @LastModifiedBy
    protected String modifiedBy = "Bonsai";

    @LastModifiedDate
    protected LocalDateTime modified;

    protected EntityStatus entityStatus = EntityStatus.ACTIVE;

    BaseEntity() {
    }

    public EntityStatus getEntityStatus() {
        return entityStatus;
    }

    public void setEntityStatus(EntityStatus entityStatus) {
        this.entityStatus = entityStatus;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
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

    @Override
    public String toString() {
        return "BaseEntity{" +
                "id=" + id +
                ", version=" + version +
                ", createdBy='" + createdBy + '\'' +
                ", created=" + created +
                ", modifiedBy='" + modifiedBy + '\'' +
                ", modified=" + modified +
                ", entityStatus=" + entityStatus +
                '}';
    }
}
