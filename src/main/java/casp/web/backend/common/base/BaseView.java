package casp.web.backend.common.base;

import java.util.Objects;
import java.util.UUID;

public abstract class BaseView {
    protected UUID id;
    protected Long version;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseView baseView)) return false;
        return Objects.equals(id, baseView.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
