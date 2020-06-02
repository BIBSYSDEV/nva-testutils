package no.unit.nva.testutils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.InputStream;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class HandlerRequestBuilderTest {

    public static final String KEY = "key";
    public static final String VALUE = "value";
    public static final String BODY = "body";
    public static final String PATH_PARAMETERS = "pathParameters";
    public static final String QUERY_PARAMETERS = "queryParameters";
    public static final String REQUEST_CONTEXT = "requestContext";

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Test
    public void buildReturnsEmptyRequestOnNoArguments() throws Exception {
        InputStream request = new HandlerRequestBuilder<String>(objectMapper)
            .build();

        Map map = toMap(request);
        assertNull(map.get("body"));
    }

    @Test
    public void buildReturnsRequestWithBodyWhenWithBody() throws Exception {
        InputStream request = new HandlerRequestBuilder<String>(objectMapper)
            .withBody(VALUE)
            .build();

        Map map = toMap(request);
        assertEquals(map.get(BODY), VALUE);
    }

    @Test
    public void buildReturnsRequestWithHeadersWhenWithHeaders() throws  Exception {
        InputStream request = new HandlerRequestBuilder<String>(objectMapper)
            .withHeaders(Map.of(KEY, VALUE))
            .build();

        Map map = toMap(request);
        assertNotNull(map.get("headers"));
    }

    @Test
    public void buildReturnsRequestWithQueryParametersWhenWithQueryParameters() throws  Exception {
        InputStream request = new HandlerRequestBuilder<String>(objectMapper)
            .withQueryParameters(Map.of(KEY, VALUE))
            .build();

        Map map = toMap(request);
        assertNotNull(map.get(QUERY_PARAMETERS));
    }

    @Test
    public void buildReturnsRequestWithPathParametersWhenWithPathParameters() throws  Exception {
        InputStream request = new HandlerRequestBuilder<String>(objectMapper)
            .withPathParameters(Map.of(KEY, VALUE))
            .build();

        Map map = toMap(request);
        assertNotNull(map.get(PATH_PARAMETERS));
    }

    @Test
    public void buildReturnsRequestWithRequestContextWhenWithRequestContext() throws Exception {
        InputStream request = new HandlerRequestBuilder<String>(objectMapper)
            .withRequestContext(Map.of(KEY, VALUE))
            .build();

        Map map = toMap(request);
        assertNotNull(map.get(REQUEST_CONTEXT));
    }

    private Map<String,Object> toMap(InputStream inputStream) throws JsonProcessingException {
        return objectMapper.readValue(HandlerRequestBuilder.toString(inputStream), Map.class);
    }

}
