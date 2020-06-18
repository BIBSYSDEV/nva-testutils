package no.unit.nva.testutils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

public class HandlerRequestBuilder<T> {

    @JsonProperty("body")
    private String body;
    @JsonProperty("headers")
    private Map<String, String> headers;
    @JsonProperty("queryStringParameters")
    private Map<String, String> queryParameters;
    @JsonProperty("pathParameters")
    private Map<String, String> pathParameters;
    @JsonProperty("requestContext")
    private Map<String, Object> requestContext;
    @JsonProperty("method")
    private String method;

    public static final String DELIMITER = "\n";
    private final transient ObjectMapper objectMapper;

    public HandlerRequestBuilder(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public HandlerRequestBuilder<T> withBody(T body) throws JsonProcessingException {
        if (body instanceof String) {
            this.body = (String)body;
        } else {
            this.body = objectMapper.writeValueAsString(body);
        }
        return this;
    }

    public HandlerRequestBuilder<T> withHeaders(Map<String, String> headers) {
        this.headers = headers;
        return  this;
    }

    public HandlerRequestBuilder<T> withQueryParameters(Map<String, String> queryParameters) {
        this.queryParameters = queryParameters;
        return this;
    }

    public HandlerRequestBuilder<T> withPathParameters(Map<String, String> pathParameters) {
        this.pathParameters = pathParameters;
        return this;
    }

    public HandlerRequestBuilder<T> withRequestContext(Map<String, Object> requestContext) {
        this.requestContext = requestContext;
        return this;
    }

    public HandlerRequestBuilder<T> withMethod(String method){
        this.method=method;
        return this;
    }

    public InputStream build() throws JsonProcessingException {
        return new ByteArrayInputStream(objectMapper.writeValueAsBytes(this));
    }

    public static String toString(InputStream inputStream) {
        return new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
            .lines()
            .collect(Collectors.joining(DELIMITER));
    }
}
