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

    public static <T> InputStream requestObjectToApiGatewayRequestInputSteam(T requestObject,
                                                                             Map<String, String> headers)
        throws JsonProcessingException {
        String requestString = requestObjectToApiGatewayRequestString(requestObject, headers);
        return new ByteArrayInputStream(requestString.getBytes(StandardCharsets.UTF_8));
    }

    public static <T> String requestObjectToApiGatewayRequestString(T requestObject, Map<String, String> headers)
        throws JsonProcessingException {
        ObjectNode root = OBJECT_MAPPER.createObjectNode();
        String requestObjString = OBJECT_MAPPER.writeValueAsString(requestObject);
        root.put(BODY_FIELD, requestObjString);
        JsonNode headersNode = OBJECT_MAPPER.convertValue(headers, JsonNode.class);
        root.set(HEADERS_FIELD, headersNode);
        return OBJECT_MAPPER.writeValueAsString(root);
    }
}
