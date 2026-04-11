package casp.web.backend.common.base;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@EqualsAndHashCode(of = "id")
@Setter
@Getter
public abstract class BaseDto {
    protected UUID id = UUID.randomUUID();
    protected long version;
}
