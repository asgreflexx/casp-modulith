package casp.web.backend.common.base;

import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

import java.util.Set;


public interface BaseMapper<S, T> {
    T toTarget(S source);

    @Mapping(target = "id", conditionExpression = "java(null != source.getId())")
    S toSource(T source);

    default Page<T> toTargetPage(Page<S> sourcePage) {
        return sourcePage.map(this::toTarget);
    }

    Set<T> toTargetSet(Set<S> sourceSet);
}
