package no.unit.nva.hamcrest;

import static java.util.Objects.isNull;
import com.fasterxml.jackson.databind.JsonNode;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

public class DoesNotHaveEmptyValues<T> extends BaseMatcher<T> {


    public static final String EMPTY_FIELD_ERROR = "Empty field found: ";
    public static final String FIELD_DELIMITER = ",";
    public static final String TEST_DESCRIPTION = "All fields of all included objects need to be non empty";

    private final List<PropertyValuePair>  emptyFields;
    private List<Class<?>> stopRecursionClasses;

    public DoesNotHaveEmptyValues() {
        super();
        stopRecursionClasses = doNotCheckRecursively();

        this.emptyFields = new ArrayList<>();
    }

    public static <R> DoesNotHaveEmptyValues<R> doesNotHaveEmptyValues() {
        return new DoesNotHaveEmptyValues<>();
    }

    /**
     * Stop the nested check for the classes in the ignore list. The fields of the specified types will be checked
     * whether they are null or not, but their fields will not be checked.
     *
     * @param ignoreList List of classes where the nested field check will stop.
     * @param <R>        the type of the object in test.
     * @return a matcher.
     */
    public static <R> DoesNotHaveEmptyValues<R> doesNotHaveEmptyValuesIgnoringClasses(List<Class<?>> ignoreList) {
        DoesNotHaveEmptyValues<R> matcher = new DoesNotHaveEmptyValues<>();
        ArrayList<Class<?>> newStopRecursionClasses = new ArrayList<>();
        newStopRecursionClasses.addAll(matcher.stopRecursionClasses);
        newStopRecursionClasses.addAll(ignoreList);
        matcher.stopRecursionClasses = newStopRecursionClasses;
        return matcher;
    }

    @Override
    public boolean matches(Object actual) {
        return check(PropertyValuePair.rootObject(actual));
    }

    public boolean check(PropertyValuePair fieldValue) {
        List<PropertyValuePair> fields = fieldValue.children();
        for (PropertyValuePair field : fields) {
            checkRecursivelyForEmptyFields(field);
        }
        emptyFields.addAll(collectEmptyFields(fields));
        return emptyFields.isEmpty();
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(TEST_DESCRIPTION);
    }

    @Override
    public void describeMismatch(Object item, Description description) {
        String emptyFieldNames = emptyFields.stream()
            .map(PropertyValuePair::getFieldPath)
            .collect(Collectors.joining(
                FIELD_DELIMITER));

        description.appendText(EMPTY_FIELD_ERROR)
            .appendText(emptyFieldNames);
    }

    private List<Class<?>> doNotCheckRecursively() {
        return List.of(URI.class);
    }

    private void checkRecursivelyForEmptyFields(PropertyValuePair propValue) {
        if (propValue.isNotBaseType() && shouldNotBeIgnored(propValue.getValue())) {
            check(propValue);
        }
    }

    private boolean shouldNotBeIgnored(Object value) {
        return
            stopRecursionClasses.stream()
                .noneMatch(stopRecursionClass -> stopRecursionClass.isInstance(value));
    }

    private List<PropertyValuePair> collectEmptyFields(List<PropertyValuePair> propertyValuePairs) {
        return propertyValuePairs.stream()
            .filter(propertyValue -> isEmpty(propertyValue.getValue()))
            .collect(Collectors.toList());
    }


    private boolean isEmpty(Object value) {
        if (isNull(value)) {
            return true;
        }
        return
            isBlankString(value)
            || isEmptyCollection(value)
            || isEmptyMap(value)
            || isEmptyJsonNode(value);
    }

    private boolean isEmptyMap(Object value) {
        if (value instanceof Map) {
            Map<?, ?> coll = (Map<?, ?>) value;
            return coll.isEmpty();
        }
        return false;
    }

    private boolean isEmptyJsonNode(Object value) {
        if (value instanceof JsonNode) {
            JsonNode node = (JsonNode) value;
            return node.isEmpty();
        }
        return false;
    }

    private boolean isEmptyCollection(Object value) {
        if (value instanceof Collection) {
            Collection<?> coll = (Collection<?>) value;
            return coll.isEmpty();
        }
        return false;
    }

    private boolean isBlankString(Object value) {
        if (value instanceof String) {
            String strValue = (String) value;
            return strValue.isBlank();
        }
        return false;
    }
}
