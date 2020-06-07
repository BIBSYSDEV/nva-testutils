package no.unit.nva.hamcrest;

import static no.unit.nva.hamcrest.DoesNotHaveNullOrEmptyFields.doesNotHaveNullOrEmptyFields;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

public class DoesNotHaveNullOrEmptyFieldsTest {

    public static final int SOME_BOXED_VALUE = 5;
    public static final String SOME_STRING_VALUE = "someStringValue";
    public static final Map<String, String> NON_EMPTY_MAP = Collections.singletonMap(SOME_STRING_VALUE,
        SOME_STRING_VALUE);
    public static final List<String> NON_EMPTY_COLLECTION = Collections.singletonList(SOME_STRING_VALUE);
    private static final TestClass SOME_OBJECT = new TestClass();

    private DoesNotHaveNullOrEmptyFields<TestClass> matcher;

    /**
     * Initialize.
     */
    @BeforeEach
    public void init() {
        matcher = new DoesNotHaveNullOrEmptyFields<>();
    }


    @Test
    public void matchesReturnsTrueWhenAllFieldsAreNotEmpty() {
        TestClass testObject = TestClass.newBuilder()
            .withBoxedField(SOME_BOXED_VALUE)
            .withStringField(SOME_STRING_VALUE)
            .withCollectionField(NON_EMPTY_COLLECTION)
            .withMapField(NON_EMPTY_MAP)
            .withClassObject(SOME_OBJECT)
            .build();
        assertTrue(matcher.matches(testObject));
        assertThat(testObject, doesNotHaveNullOrEmptyFields());
    }

    @ParameterizedTest
    @NullAndEmptySource
    public void matchesReturnsFalseWhenStringFieldIsEmpty(String input) {
        TestClass testObject = TestClass.newBuilder()
            .withBoxedField(SOME_BOXED_VALUE)
            .withStringField(input)
            .withCollectionField(NON_EMPTY_COLLECTION)
            .withMapField(NON_EMPTY_MAP)
            .withClassObject(SOME_OBJECT)
            .build();
        assertFalse(matcher.matches(testObject));
        assertErrorMessageContainsField(testObject, "stringField");
    }

    @ParameterizedTest
    @NullAndEmptySource
    public void matchesReturnsFalseWhenCollectionFieldIsEmpty(List<String> input) {
        TestClass testObject = TestClass.newBuilder()
            .withBoxedField(SOME_BOXED_VALUE)
            .withStringField(SOME_STRING_VALUE)
            .withCollectionField(input)
            .withMapField(NON_EMPTY_MAP)
            .withClassObject(SOME_OBJECT)
            .build();
        assertFalse(matcher.matches(testObject));
        assertErrorMessageContainsField(testObject, "collectionField");
    }

    @ParameterizedTest
    @NullAndEmptySource
    public void matchesReturnsFalseWhenCollectionFieldIsEmpty(Map<String, String> input) {
        TestClass testObject = TestClass.newBuilder()
            .withBoxedField(SOME_BOXED_VALUE)
            .withStringField(SOME_STRING_VALUE)
            .withCollectionField(NON_EMPTY_COLLECTION)
            .withMapField(input)
            .withClassObject(SOME_OBJECT)
            .build();
        assertFalse(matcher.matches(testObject));
        assertErrorMessageContainsField(testObject, "mapField");
    }

    @Test
    public void matchesReturnsFalseWhenBoxedFieldIsEmpty() {
        TestClass testObject = TestClass.newBuilder()
            .withBoxedField(null)
            .withStringField(SOME_STRING_VALUE)
            .withCollectionField(NON_EMPTY_COLLECTION)
            .withMapField(NON_EMPTY_MAP)
            .withClassObject(SOME_OBJECT)
            .build();
        assertFalse(matcher.matches(testObject));
        assertErrorMessageContainsField(testObject, "boxedField");
    }

    @Test
    public void matchesReturnsFalseWhenClassFieldIsEmpty() {
        TestClass testObject = TestClass.newBuilder()
            .withBoxedField(SOME_BOXED_VALUE)
            .withStringField(SOME_STRING_VALUE)
            .withCollectionField(NON_EMPTY_COLLECTION)
            .withMapField(NON_EMPTY_MAP)
            .withClassObject(null)
            .build();
        assertFalse(matcher.matches(testObject));
        assertErrorMessageContainsField(testObject, "classObject");
    }

    private void assertErrorMessageContainsField(TestClass testObject, String fieldName) {
        Executable test = () -> assertThat(testObject, doesNotHaveNullOrEmptyFields());
        AssertionError error = assertThrows(AssertionError.class, test);
        assertThat(error.getMessage(), containsString(fieldName));
    }

    private static class TestClass {

        private String stringField;

        private List<String> collectionField;
        private Map<String, String> mapField;
        private Integer boxedField = null;
        private TestClass classObject;

        public TestClass() {
        }

        private TestClass(Builder builder) {
            stringField = builder.stringField;
            collectionField = builder.collectionField;
            mapField = builder.mapField;
            boxedField = builder.boxedField;
            classObject = builder.classObject;
        }

        public static Builder newBuilder() {
            return new Builder();
        }

        public String getStringField() {
            return stringField;
        }

        public List<String> getCollectionField() {
            return collectionField;
        }

        public Map<String, String> getMapField() {
            return mapField;
        }

        public Integer getBoxedField() {
            return boxedField;
        }

        public TestClass getClassObject() {
            return classObject;
        }

        private static final class Builder {

            private String stringField;
            private List<String> collectionField;
            private Map<String, String> mapField;
            private Integer boxedField;
            private TestClass classObject;

            private Builder() {
            }

            public Builder withStringField(String val) {
                stringField = val;
                return this;
            }

            public Builder withCollectionField(List<String> val) {
                collectionField = val;
                return this;
            }

            public Builder withMapField(Map<String, String> val) {
                mapField = val;
                return this;
            }

            public Builder withBoxedField(Integer val) {
                boxedField = val;
                return this;
            }

            public Builder withClassObject(TestClass val) {
                classObject = val;
                return this;
            }

            public TestClass build() {
                return new TestClass(this);
            }
        }
    }
}