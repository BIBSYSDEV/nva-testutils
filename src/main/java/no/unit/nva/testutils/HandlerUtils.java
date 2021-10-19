package no.unit.nva.testutils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Use {@link HandlerRequestBuilder}.
 * @deprecated use {@link HandlerRequestBuilder}.
 */
@Deprecated
public final class HandlerUtils {

    public static final String BODY_FIELD = "body";
    public static final String HEADERS_FIELD = "headers";
    public static final String PATH_PARAMETERS = "pathParameters";
    public static final String QUERY_PARAMETERS = "queryStringParameters";

    private final ObjectMapper objectMapper;

    public HandlerUtils() {
        this(new ObjectMapper());
    }

    public HandlerUtils(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public <T> InputStream requestObjectToApiGatewayRequestInputSteam(T requestObject)
        throws JsonProcessingException {
        String requestString = requestObjectToApiGatewayRequestString(requestObject, null, null,
            null);
        return new ByteArrayInputStream(requestString.getBytes(StandardCharsets.UTF_8));
    }

    public <T> InputStream requestObjectToApiGatewayRequestInputSteam(T requestObject,
                                                                      Map<String, String> headers)
        throws JsonProcessingException {
        String requestString = requestObjectToApiGatewayRequestString(requestObject, headers, null,
            null);
        return new ByteArrayInputStream(requestString.getBytes(StandardCharsets.UTF_8));
    }

    public <T> InputStream requestObjectToApiGatewayRequestInputSteam(T requestObject,
                                                                      Map<String, String> headers,
                                                                      Map<String, String> pathParameters,
                                                                      Map<String, String> queryParameters)
        throws JsonProcessingException {
        String requestString = requestObjectToApiGatewayRequestString(requestObject, headers, pathParameters,
            queryParameters);
        return new ByteArrayInputStream(requestString.getBytes(StandardCharsets.UTF_8));
    }

    public <T> String requestObjectToApiGatewayRequestString(T requestObject,
                                                             Map<String, String> headers,
                                                             Map<String, String> pathParameters,
                                                             Map<String, String> queryParameters)
        throws JsonProcessingException {
        InputStream inputStream = new HandlerRequestBuilder<T>(objectMapper)
            .withBody(requestObject)
            .withHeaders(headers)
            .withPathParameters(pathParameters)
            .withQueryParameters(queryParameters)
            // no support for requestContext, use HandlerRequestBuilder instead
            .build();

        return HandlerRequestBuilder.toString(inputStream);
    }
}
