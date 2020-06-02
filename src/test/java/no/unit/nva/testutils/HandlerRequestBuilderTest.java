package no.unit.nva.testutils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import com.fasterxml.jackson.core.JsonProcessingException;
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

    // Can not use ObjectMapper from nva-commons because it would create a circular dependency
    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void buildReturnsEmptyRequestOnNoArguments() throws Exception {
        InputStream request = new HandlerRequestBuilder<String>(objectMapper)
            .build();

        Map mapWithNullBody = toMap(request);
        assertThat(mapWithNullBody.get(BODY), nullValue());

    }

    @Test
    public void buildReturnsRequestWithBodyWhenStringInput() throws Exception {
        InputStream request = new HandlerRequestBuilder<String>(objectMapper)
            .withBody(VALUE)
            .build();

        Map mapWithBody = toMap(request);
        assertThat(mapWithBody.get(BODY), equalTo(VALUE));
    }

    @Test
    public void buildReturnsRequestWithBodyWhenMapInput() throws Exception {
        InputStream request = new HandlerRequestBuilder<Map>(objectMapper)
            .withBody(Map.of(KEY, VALUE))
            .build();

        Map mapWithBody = toMap(request);
        assertThat(mapWithBody.get(BODY), notNullValue());
    }

    @Test
    public void buildReturnsRequestWithHeadersWhenWithHeaders() throws  Exception {
        InputStream request = new HandlerRequestBuilder<String>(objectMapper)
            .withHeaders(Map.of(KEY, VALUE))
            .build();

        Map mapWithHeaders = toMap(request);
        assertThat(mapWithHeaders.get(HEADERS), notNullValue());
    }

    @Test
    public void buildReturnsRequestWithQueryParametersWhenWithQueryParameters() throws  Exception {
        InputStream request = new HandlerRequestBuilder<String>(objectMapper)
            .withQueryParameters(Map.of(KEY, VALUE))
            .build();

        Map mapWithQueryParameters = toMap(request);
        assertThat(mapWithQueryParameters.get(QUERY_PARAMETERS), notNullValue());
    }

    @Test
    public void buildReturnsRequestWithPathParametersWhenWithPathParameters() throws  Exception {
        InputStream request = new HandlerRequestBuilder<String>(objectMapper)
            .withPathParameters(Map.of(KEY, VALUE))
            .build();

        Map mapWthPathParameters = toMap(request);
        assertThat(mapWthPathParameters.get(PATH_PARAMETERS), notNullValue());
    }

    @Test
    public void buildReturnsRequestWithRequestContextWhenWithRequestContext() throws Exception {
        InputStream request = new HandlerRequestBuilder<String>(objectMapper)
            .withRequestContext(Map.of(KEY, VALUE))
            .build();

        Map mapWithRequestContext = toMap(request);
        assertThat(mapWithRequestContext.get(REQUEST_CONTEXT), notNullValue());
    }

    private Map<String,Object> toMap(InputStream inputStream) throws JsonProcessingException {
        return objectMapper.readValue(HandlerRequestBuilder.toString(inputStream), Map.class);
    }

}
