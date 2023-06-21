package ru.veselov.websocketroomproject.config.resolver;

import org.springframework.core.MethodParameter;
import org.springframework.lang.NonNull;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.annotation.RequestParamMethodArgumentResolver;
import ru.veselov.websocketroomproject.annotation.SortingParam;
import ru.veselov.websocketroomproject.dto.request.SortParameters;

public class SortParameterRequestParamsResolver extends RequestParamMethodArgumentResolver {

    public SortParameterRequestParamsResolver(boolean useDefaultResolution) {
        super(useDefaultResolution);
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(SortingParam.class);
    }

    @Override
    protected Object resolveName(@NonNull String name,
                                 @NonNull MethodParameter parameter,
                                 @NonNull NativeWebRequest request) {
        return new SortParameters(
                resolvePageNumber(request),
                resolveSortingField(request),
                resolveOrder(request)
        );
    }

    private Integer resolvePageNumber(NativeWebRequest request) {
        String page = request.getParameter("page");
        return page == null ? 0 : Integer.parseInt(page);
    }

    private String resolveSortingField(NativeWebRequest request) {
        String sort = request.getParameter("sort");
        return sort == null ? "createdAt" : sort;
    }

    private String resolveOrder(NativeWebRequest request) {
        String order = request.getParameter("order");
        return order == null ? "desc" : order;
    }

}
