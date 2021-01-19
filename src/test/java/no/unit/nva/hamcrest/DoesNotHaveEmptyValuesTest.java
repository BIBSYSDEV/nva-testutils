package no.unit.nva.hamcrest;

import static no.unit.nva.hamcrest.DoesNotHaveEmptyValues.EMPTY_FIELD_ERROR;
import static no.unit.nva.hamcrest.PropertyValuePair.FIELD_PATH_DELIMITER;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.jupiter.api.Assertions.assertThrows;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.net.URI;
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
        WithBaseTypes nonEmptyObject = new WithBaseTypes(SAMPLE_STRING,
            SAMPLE_INT,
            SAMPLE_LIST,
            SAMPLE_MAP,
            SAMPLE_JSON_NODE);
        assertThat(matcher.matches(nonEmptyObject), is(true));
    }

    @Test
    public void matchesReturnsFalseWhenStringIsEmpty() {
        WithBaseTypes nonEmptyObject = new WithBaseTypes(EMPTY_STRING,
            SAMPLE_INT,
            SAMPLE_LIST,
            SAMPLE_MAP,
            SAMPLE_JSON_NODE);
        assertThat(matcher.matches(nonEmptyObject), is(false));
    }

    @Test
    public void matcherReturnsMessageContainingTheFieldNameWhenFieldIsEmpty() {
        WithBaseTypes withEmptyInt = new WithBaseTypes(SAMPLE_STRING,
            null,
            SAMPLE_LIST,
            SAMPLE_MAP,
            SAMPLE_JSON_NODE);

        AssertionError exception = assertThrows(AssertionError.class,
            () -> assertThat(withEmptyInt, DoesNotHaveEmptyValues.doesNotHaveEmptyValues()));
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
            () -> assertThat(testObject, DoesNotHaveEmptyValues.doesNotHaveEmptyValues()));
        assertThat(error.getMessage(), containsString("objectWithFields" + FIELD_PATH_DELIMITER + "stringField"));
    }

    @Test
    public void matchesReturnsFalseWhenBaseTypeIsNull() {
        WithBaseTypes baseTypesObject = new WithBaseTypes(null,
            SAMPLE_INT,
            SAMPLE_LIST,
            SAMPLE_MAP,
            SAMPLE_JSON_NODE);
        assertThat(matcher.matches(baseTypesObject), is(false));
    }

    @Test
    public void matchesReturnsFalseWhenListIsEmpty() {
        WithBaseTypes baseTypesObject = new WithBaseTypes(SAMPLE_STRING,
            SAMPLE_INT,
            Collections.emptyList(),
            SAMPLE_MAP,
            SAMPLE_JSON_NODE);
        assertThat(matcher.matches(baseTypesObject), is(false));
    }

    @Test
    public void matchesReturnsFalseWhenMapIsEmpty() {
        WithBaseTypes baseTypesObject = new WithBaseTypes(SAMPLE_STRING,
            SAMPLE_INT,
            SAMPLE_LIST,
            Collections.emptyMap(),
            SAMPLE_JSON_NODE);
        assertThat(matcher.matches(baseTypesObject), is(false));
    }

    @Test
    public void matchesReturnsFalseWhenJsonNodeIsEmpty() {
        WithBaseTypes baseTypesObject = new WithBaseTypes(SAMPLE_STRING,
            SAMPLE_INT,
            SAMPLE_LIST,
            SAMPLE_MAP,
            new ObjectMapper().createObjectNode());
        assertThat(matcher.matches(baseTypesObject), is(false));
    }

    @Test
    public void matchesDoesNotCheckFieldsInIgnoredList() {
        ClassWithUri withUri = new ClassWithUri(URI.create("http://example.com"));
        assertThat(matcher.matches(withUri), is(true));
    }

    @Test
    public void matchesDoesNotCheckFieldInCustomlyAddedIngoreClass() {
        WithBaseTypes ignoredObjectWithEmptyProperties =
            new WithBaseTypes(null, null, null, null, null);

        ClassWithChildrenWithMultipleFields testObject =
            new ClassWithChildrenWithMultipleFields(SAMPLE_STRING, ignoredObjectWithEmptyProperties, SAMPLE_INT);
        assertThat(testObject, DoesNotHaveEmptyValues.doesNotHaveEmptyValuesIgnoringClasses(List.of(WithBaseTypes.class)));
    }

    private static JsonNode nonEmptyJsonNode() {
        ObjectNode node = new ObjectMapper().createObjectNode();
        node.put("someKey", "someValue");
        return node;
    }

    private WithBaseTypes objectMissingStringField() {
        return new WithBaseTypes(null, SAMPLE_INT, SAMPLE_LIST, SAMPLE_MAP, SAMPLE_JSON_NODE);
    }

    private static class WithBaseTypes {

        private final String stringField;
        private final Integer intField;
        private final List<String> list;
        private final Map<String, String> map;
        private final JsonNode jsonNode;

        public WithBaseTypes(String stringField, Integer intField, List<String> list,
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
        private final WithBaseTypes objectWithFields;
        private final Integer someIntField;

        public ClassWithChildrenWithMultipleFields(String someStringField,
                                                   WithBaseTypes objectWithFields, Integer someIntField) {
            this.someStringField = someStringField;
            this.objectWithFields = objectWithFields;
            this.someIntField = someIntField;
        }

        public String getSomeStringField() {
            return someStringField;
        }

        public WithBaseTypes getObjectWithFields() {
            return objectWithFields;
        }

        public Integer getSomeIntField() {
            return someIntField;
        }
    }

    private static class ClassWithUri {

        private final URI uri;

        private ClassWithUri(URI uri) {
            this.uri = uri;
        }

        public URI getUri() {
            return uri;
        }
    }
}
