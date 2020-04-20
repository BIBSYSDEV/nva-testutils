package no.unit.nva.testutils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public final class HandlerUtils {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    public static final String BODY_FIELD = "body";
    public static final String HEADERS_FIELD = "headers";
    public static final String PATH_PARAMETERS = "pathParameters";
    public static final String QUERY_PARAMETERS = "queryStringParameters";

    public static <T> InputStream requestObjectToApiGatewayRequestInputSteam(T requestObject)
        throws JsonProcessingException {
        String requestString = requestObjectToApiGatewayRequestString(requestObject, null, null,
            null);
        return new ByteArrayInputStream(requestString.getBytes(StandardCharsets.UTF_8));
    }

    public static <T> InputStream requestObjectToApiGatewayRequestInputSteam(T requestObject,
                                                                             Map<String, String> headers)
        throws JsonProcessingException {
        String requestString = requestObjectToApiGatewayRequestString(requestObject, headers, null,
            null);
        return new ByteArrayInputStream(requestString.getBytes(StandardCharsets.UTF_8));
    }

    public static <T> InputStream requestObjectToApiGatewayRequestInputSteam(T requestObject,
                                                                             Map<String, String> headers,
                                                                             Map<String, String> pathParameters,
                                                                             Map<String, String> queryParameters)
        throws JsonProcessingException {
        String requestString = requestObjectToApiGatewayRequestString(requestObject, headers, pathParameters,
            queryParameters);
        return new ByteArrayInputStream(requestString.getBytes(StandardCharsets.UTF_8));
    }

    public static <T> String requestObjectToApiGatewayRequestString(T requestObject,
                                                                    Map<String, String> headers,
                                                                    Map<String, String> pathParameters,
                                                                    Map<String, String> queryParameters)
        throws JsonProcessingException {
        ObjectNode root = OBJECT_MAPPER.createObjectNode();
        String requestObjString = OBJECT_MAPPER.writeValueAsString(requestObject);
        root.put(BODY_FIELD, requestObjString);
        JsonNode headersNode = OBJECT_MAPPER.convertValue(headers, JsonNode.class);
        root.set(HEADERS_FIELD, headersNode);
        JsonNode pathParamsNode = OBJECT_MAPPER.convertValue(pathParameters, JsonNode.class);
        JsonNode queryParametersNode = OBJECT_MAPPER.convertValue(queryParameters, JsonNode.class);
        root.set(PATH_PARAMETERS, pathParamsNode);
        root.set(QUERY_PARAMETERS, queryParametersNode);
        return OBJECT_MAPPER.writeValueAsString(root);
    }
}
