package no.unit.nva.testutils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class HandlerUtilsTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    public static final String VALUE = "value";
    public static final String SOME_HEADER_VALUE = "SomeHeaderValue";
    public static final String SOME_HEADER = "SomeHeader";

    @Test
    public void requestObjectToApiGatewayRequestStringReturnsValidJsonObjectForNullHeaders()
        throws JsonProcessingException {
        String requestString = gatewayRequestWithNullHeaders();
        assertNotNull(requestString);
        RequestBody actual = extractBodyFromSerializedRequest(requestString);
        assertThat(actual.getMyField(), is(equalTo(VALUE)));
    }



    @Test
    public void requestObjectToApiGatewayRequestStringReturnsValidJsonObjectForNonNullHeaders()
        throws JsonProcessingException {
        String requestString = gatewayRequestWithNonNullHeaders();
        assertNotNull(requestString);
        RequestBody actual= extractBodyFromSerializedRequest(requestString);
        assertThat(actual.getMyField(),is(equalTo(VALUE)));
    }

    private String gatewayRequestWithNullHeaders() throws JsonProcessingException {
        RequestBody requestBody = new RequestBody();
        requestBody.setMyField(VALUE);
        return HandlerUtils.requestObjectToApiGatewayRequestString(requestBody, null);
    }

    private String gatewayRequestWithNonNullHeaders() throws JsonProcessingException {
        RequestBody requestBody = new RequestBody();
        requestBody.setMyField(VALUE);
        Map<String,String> headers= new HashMap<String,String>();
        headers.put(SOME_HEADER, SOME_HEADER_VALUE);
        return HandlerUtils.requestObjectToApiGatewayRequestString(requestBody, headers);
    }

    private RequestBody extractBodyFromSerializedRequest(String requestString) throws JsonProcessingException {
        JsonNode json = OBJECT_MAPPER.readTree(requestString);
        String body = json.get(HandlerUtils.BODY_FIELD).textValue();
        return OBJECT_MAPPER.readValue(body, RequestBody.class);
    }

    private static class RequestBody {

        private String myField;

        public String getMyField() {
            return myField;
        }

        public void setMyField(String myField) {
            this.myField = myField;
        }
    }
}