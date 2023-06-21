package ru.veselov.websocketroomproject.config.resolver;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.ServletWebRequest;
import ru.veselov.websocketroomproject.dto.request.SortParameters;

@ExtendWith(MockitoExtension.class)
class SortParameterRequestParamsResolverTest {

    SortParameterRequestParamsResolver resolver = new SortParameterRequestParamsResolver(true);

    MethodParameter methodParameter = Mockito.mock(MethodParameter.class);

    ServletWebRequest servletWebRequest = Mockito.mock(ServletWebRequest.class);

    @Test
    void shouldResolveName() {
        Mockito.when(servletWebRequest.getParameter("order")).thenReturn("asc");
        Mockito.when(servletWebRequest.getParameter("page")).thenReturn("500");
        Mockito.when(servletWebRequest.getParameter("sort")).thenReturn("name");

        SortParameters parameters = (SortParameters) resolver.resolveName(
                "parameters",
                methodParameter,
                servletWebRequest);

        Assertions.assertThat(parameters).isNotNull();
        Assertions.assertThat(parameters.getSort()).isEqualTo("name");
        Assertions.assertThat(parameters.getPage()).isEqualTo(500);
        Assertions.assertThat(parameters.getOrder()).isEqualTo("asc");
    }

    @Test
    void shouldReturnDefaultObject() {
        Mockito.when(servletWebRequest.getParameter("order")).thenReturn(null);
        Mockito.when(servletWebRequest.getParameter("page")).thenReturn(null);
        Mockito.when(servletWebRequest.getParameter("sort")).thenReturn(null);

        SortParameters parameters = (SortParameters) resolver.resolveName(
                "parameters",
                methodParameter,
                servletWebRequest);

        Assertions.assertThat(parameters).isNotNull();
        Assertions.assertThat(parameters.getSort()).isEqualTo("createdAt");
        Assertions.assertThat(parameters.getPage()).isZero();
        Assertions.assertThat(parameters.getOrder()).isEqualTo("desc");
    }

}
