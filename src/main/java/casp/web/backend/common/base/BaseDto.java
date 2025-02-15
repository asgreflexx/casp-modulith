package casp.web.backend.common.base;

import java.util.Objects;
import java.util.UUID;

public abstract class BaseDto {
    protected UUID id = UUID.randomUUID();
    protected long version;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseDto baseDto)) return false;
        return Objects.equals(id, baseDto.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
