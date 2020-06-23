package no.unit.nva.testutils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class HandlerRequestBuilderTest {

    public static final String KEY = "key";
    public static final String VALUE = "value";
    public static final String BODY = "body";
    public static final String HEADERS = "headers";
    public static final String PATH_PARAMETERS = "pathParameters";
    public static final String QUERY_PARAMETERS = "queryStringParameters";
    public static final String REQUEST_CONTEXT = "requestContext";
    public static final String SOME_METHOD = "POST";
    private static final String HTTP_METHOD = "httpMethod";

    // Can not use ObjectMapper from nva-commons because it would create a circular dependency
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void buildReturnsEmptyRequestOnNoArguments() throws Exception {
        InputStream request = new HandlerRequestBuilder<String>(objectMapper)
            .build();

        Map<String, Object> mapWithNullBody = toMap(request);
        assertThat(mapWithNullBody.get(BODY), nullValue());
    }

    @Test
    public void buildReturnsRequestWithBodyWhenStringInput() throws Exception {
        InputStream request = new HandlerRequestBuilder<String>(objectMapper)
            .withBody(VALUE)
            .build();

        Map<String, Object> mapWithBody = toMap(request);
        assertThat(mapWithBody.get(BODY), equalTo(VALUE));
    }

    @Test
    public void buildReturnsRequestWithBodyWhenMapInput() throws Exception {
        InputStream request = new HandlerRequestBuilder<Map<String, Object>>(objectMapper)
            .withBody(Map.of(KEY, VALUE))
            .build();

        Map<String, Object> mapWithBody = toMap(request);
        assertThat(mapWithBody.get(BODY), notNullValue());
    }

    @Test
    public void buildReturnsRequestWithHeadersWhenWithHeaders() throws Exception {
        InputStream request = new HandlerRequestBuilder<String>(objectMapper)
            .withHeaders(Map.of(KEY, VALUE))
            .build();

        Map<String, Object> mapWithHeaders = toMap(request);
        assertThat(mapWithHeaders.get(HEADERS), notNullValue());
    }

    @Test
    public void buildReturnsRequestWithQueryParametersWhenWithQueryParameters() throws Exception {
        InputStream request = new HandlerRequestBuilder<String>(objectMapper)
            .withQueryParameters(Map.of(KEY, VALUE))
            .build();

        Map<String, Object> mapWithQueryParameters = toMap(request);
        assertThat(mapWithQueryParameters.get(QUERY_PARAMETERS), notNullValue());
    }

    @Test
    public void buildReturnsRequestWithPathParametersWhenWithPathParameters() throws Exception {
        InputStream request = new HandlerRequestBuilder<String>(objectMapper)
            .withPathParameters(Map.of(KEY, VALUE))
            .build();

        Map<String, Object> mapWthPathParameters = toMap(request);
        assertThat(mapWthPathParameters.get(PATH_PARAMETERS), notNullValue());
    }

    @Test
    public void buildReturnsRequestWithRequestContextWhenWithRequestContext() throws Exception {
        InputStream request = new HandlerRequestBuilder<String>(objectMapper)
            .withRequestContext(Map.of(KEY, VALUE))
            .build();

        Map<String, Object> mapWithRequestContext = toMap(request);
        assertThat(mapWithRequestContext.get(REQUEST_CONTEXT), notNullValue());
    }

    @Test
    public void buildReturnsRequestWithMethodWhenWithMethod() throws Exception {
        InputStream request = new HandlerRequestBuilder<String>(objectMapper)
            .withHttpMethod(SOME_METHOD)
            .build();

        Map<String, Object> mapWithMethod = toMap(request);
        assertThat(mapWithMethod.get(HTTP_METHOD).toString(), is(equalTo(SOME_METHOD)));
    }

    private Map<String, Object> toMap(InputStream inputStream) throws JsonProcessingException {
        TypeReference<Map<String, Object>> type = new TypeReference<>() {
        };
        return objectMapper.readValue(HandlerRequestBuilder.toString(inputStream), type);
    }
}
