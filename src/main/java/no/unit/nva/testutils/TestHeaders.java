package no.unit.nva.testutils;

import static org.apache.http.HttpHeaders.ACCEPT;
import static org.apache.http.HttpHeaders.CONTENT_TYPE;
import java.util.Map;

public final class TestHeaders {

    public static String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
    public static String APPLICATION_JSON = "application/json";
    public static String APPLICATION_PROBLEM_JSON = "application/problem+json";
    public static String WILDCARD = "*";

    private TestHeaders() {
    }

    /**
     * Request headers for testing.
     * @return headers
     */
    public static Map<String,String> getRequestHeaders() {
        return Map.of(
                CONTENT_TYPE, APPLICATION_JSON,
                ACCEPT, APPLICATION_JSON);
    }

    /**
     * Successful response headers for testing.
     * @return headers
     */
    public static  Map<String,String> getResponseHeaders() {
        return Map.of(
                CONTENT_TYPE, APPLICATION_JSON,
                ACCESS_CONTROL_ALLOW_ORIGIN, WILDCARD
        );
    }

    /**
     * Failing response headers for testing.
     * @return headers
     */
    public static  Map<String,String> getErrorResponseHeaders() {
        return Map.of(
                CONTENT_TYPE, APPLICATION_PROBLEM_JSON,
                ACCESS_CONTROL_ALLOW_ORIGIN, WILDCARD
        );
    }

}