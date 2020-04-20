package no.unit.nva.testutils;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;

class HandlerUtilsTest {

    public static final String VALUE = "value";

    @Test
    public void requestBodyToStringReturnsValidJsonObjectForNullHeaders() throws JsonProcessingException {
        RequestBody requestBody = new RequestBody();
        requestBody.setMyField(VALUE);
        String requestString = HandlerUtils.requestObjectToString(requestBody, null);
        assertNotNull(requestString);
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