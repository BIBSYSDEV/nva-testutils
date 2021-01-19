package no.unit.nva.hamcrest;

import static no.unit.nva.hamcrest.DoesNotHaveEmptyValues.EMPTY_FIELD_ERROR;
import static no.unit.nva.hamcrest.DoesNotHaveEmptyValues.FIELD_PATH_DELIMITER;
import static no.unit.nva.hamcrest.DoesNotHaveEmptyValues.doesNotHaveEmptyValues;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.jupiter.api.Assertions.assertThrows;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DoesNotHaveEmptyValuesTest {

    public static final String SAMPLE_STRING = "sampleString";
    public static final Map<String, String> SAMPLE_MAP = Map.of(SAMPLE_STRING, SAMPLE_STRING);
    public static final List<String> SAMPLE_LIST = Collections.singletonList(SAMPLE_STRING);
    public static final int SAMPLE_INT = 16;
    public static final String EMPTY_STRING = "";
    private static final JsonNode SAMPLE_JSON_NODE = nonEmptyJsonNode();
    private DoesNotHaveEmptyValues<Object> matcher;

    @BeforeEach
    public void init() {
        matcher = new DoesNotHaveEmptyValues<>();
    }

    @Test
    public void matchesReturnsTrueWhenObjectWithSimpleFieldsHasNoNullValue() {
        WithPrimitiveFields nonEmptyObject = new WithPrimitiveFields(SAMPLE_STRING,
            SAMPLE_INT,
            SAMPLE_LIST,
            SAMPLE_MAP,
            SAMPLE_JSON_NODE);
        assertThat(matcher.matches(nonEmptyObject), is(true));
    }

    @Test
    public void matchesReturnsFalseWhenStringIsEmpty() {
        WithPrimitiveFields nonEmptyObject = new WithPrimitiveFields(EMPTY_STRING,
            SAMPLE_INT,
            SAMPLE_LIST,
            SAMPLE_MAP,
            SAMPLE_JSON_NODE);
        assertThat(matcher.matches(nonEmptyObject), is(false));
    }

    @Test
    public void matcherReturnsMessageContainingTheFieldNameWhenFieldIsEmpty() {
        WithPrimitiveFields withEmptyInt = new WithPrimitiveFields(SAMPLE_STRING,
            null,
            SAMPLE_LIST,
            SAMPLE_MAP,
            SAMPLE_JSON_NODE);

        AssertionError exception = assertThrows(AssertionError.class,
            () -> assertThat(withEmptyInt, doesNotHaveEmptyValues()));
        assertThat(exception.getMessage(), containsString(EMPTY_FIELD_ERROR));
        assertThat(exception.getMessage(), containsString("intField"));
    }

    @Test
    public void matcherReturnsFalseWhenObjectContainsChildWithEmptyField() {
        ClassWithChildrenWithMultipleFields testObject =
            new ClassWithChildrenWithMultipleFields(SAMPLE_STRING, objectMissingStringField(), SAMPLE_INT);
        assertThat(matcher.matches(testObject), is(false));
    }

    @Test
    public void matcherReturnsMessageWithMissingFieldsPath() {
        ClassWithChildrenWithMultipleFields testObject =
            new ClassWithChildrenWithMultipleFields(SAMPLE_STRING, objectMissingStringField(), SAMPLE_INT);
        AssertionError error = assertThrows(AssertionError.class,
            () -> assertThat(testObject, doesNotHaveEmptyValues()));
        assertThat(error.getMessage(), containsString("objectWithFields" + FIELD_PATH_DELIMITER + "stringField"));
    }

    private WithPrimitiveFields objectMissingStringField() {
        return new WithPrimitiveFields(null, SAMPLE_INT, SAMPLE_LIST, SAMPLE_MAP, SAMPLE_JSON_NODE);
    }

    @Test
    public void matchesReturnsFalseWhenStringIsNull() {
        WithPrimitiveFields nonEmptyObject = new WithPrimitiveFields(null,
            SAMPLE_INT,
            SAMPLE_LIST,
            SAMPLE_MAP,
            SAMPLE_JSON_NODE);
        assertThat(matcher.matches(nonEmptyObject), is(false));
    }

    private static JsonNode nonEmptyJsonNode() {
        ObjectNode node = new ObjectMapper().createObjectNode();
        node.put("someKey", "someValue");
        return node;
    }

    private static class WithPrimitiveFields {

        private final String stringField;
        private final Integer intField;
        private final List<String> list;
        private final Map<String, String> map;
        private final JsonNode jsonNode;

        public WithPrimitiveFields(String stringField, Integer intField, List<String> list,
                                   Map<String, String> map, JsonNode jsonNode) {
            this.stringField = stringField;
            this.intField = intField;
            this.list = list;
            this.map = map;
            this.jsonNode = jsonNode;
        }

        public String getStringField() {
            return stringField;
        }

        public Integer getIntField() {
            return intField;
        }

        public List<String> getList() {
            return list;
        }

        public Map<String, String> getMap() {
            return map;
        }

        public JsonNode getJsonNode() {
            return jsonNode;
        }
    }

    private static class ClassWithChildrenWithMultipleFields {

        private final String someStringField;
        private final WithPrimitiveFields objectWithFields;
        private final Integer someIntField;

        public ClassWithChildrenWithMultipleFields(String someStringField,
                                                   WithPrimitiveFields objectWithFields, Integer someIntField) {
            this.someStringField = someStringField;
            this.objectWithFields = objectWithFields;
            this.someIntField = someIntField;
        }

        public String getSomeStringField() {
            return someStringField;
        }

        public WithPrimitiveFields getObjectWithFields() {
            return objectWithFields;
        }

        public Integer getSomeIntField() {
            return someIntField;
        }
    }
}
