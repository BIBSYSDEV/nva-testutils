package no.unit.nva.hamcrest;

import static java.util.Objects.isNull;
import static no.unit.nva.hamcrest.PropertyValuePair.FIELD_PATH_DELIMITER;
import com.fasterxml.jackson.databind.JsonNode;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

public class DoesNotHaveEmptyValues<T> extends BaseMatcher<T> {

    public static final String EMPTY_FIELD_ERROR = "Empty field found: ";
    public static final String FIELD_DELIMITER = ",";
    public static final String TEST_DESCRIPTION = "All fields of all included objects need to be non empty";

    private final List<PropertyValuePair> emptyFields;
    private Set<Class<?>> stopRecursionClasses;
    private Set<String> ignoreFields;

    public DoesNotHaveEmptyValues() {
        super();
        stopRecursionClasses = classesWithNoPojoStructure();
        ignoreFields = Collections.emptySet();

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
    public static <R> DoesNotHaveEmptyValues<R> doesNotHaveEmptyValuesIgnoringClasses(Set<Class<?>> ignoreList) {
        DoesNotHaveEmptyValues<R> matcher = new DoesNotHaveEmptyValues<>();
        Set<Class<?>> newStopRecursionClasses = new HashSet<>();
        newStopRecursionClasses.addAll(matcher.stopRecursionClasses);
        newStopRecursionClasses.addAll(ignoreList);
        matcher.stopRecursionClasses = newStopRecursionClasses;
        return matcher;
    }

    public static <R> DoesNotHaveEmptyValues<R> doesNotHaveEmptyValuesIgnoringFields(Set<String> ignoreList) {
        DoesNotHaveEmptyValues<R> matcher = new DoesNotHaveEmptyValues<>();
        matcher.ignoreFields = addFieldPathDelimiterToRootField(ignoreList);
        return matcher;
    }

    private static Set<String> addFieldPathDelimiterToRootField(Set<String> ignoreList) {
        return ignoreList.stream()
            .map(DoesNotHaveEmptyValues::addPathDelimiterToFirstField)
            .collect(Collectors.toSet());
    }

    private static String addPathDelimiterToFirstField(String f) {
        if (f.startsWith(FIELD_PATH_DELIMITER)) {
            return f;
        } else {
            return FIELD_PATH_DELIMITER + f;
        }
    }

    @Override
    public boolean matches(Object actual) {
        return check(PropertyValuePair.rootObject(actual));
    }

    public boolean check(PropertyValuePair fieldValue) {
        List<PropertyValuePair> fields = fieldValue.children(ignoreFields);
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

    /*Classes that their fields do not have getters*/
    private Set<Class<?>> classesWithNoPojoStructure() {
        return Set.of(
            URI.class,
            URL.class
        );
    }

    private void checkRecursivelyForEmptyFields(PropertyValuePair propValue) {
        if (propValue.isCollection()) {
            List<PropertyValuePair> collectionElements =
                propValue.createPropertyValuePairsForEachCollectionItem();
            collectionElements.forEach(this::check);
        }
        if (propValue.isNotBaseType() && shouldNotBeIgnored(propValue.getValue())) {
            check(propValue);
        }
    }

    private boolean shouldNotBeIgnored(Object value) {
        return stopRecursionClasses
            .stream()
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
            return ((Map<?, ?>) value).isEmpty();
        }
        return false;
    }

    private boolean isEmptyJsonNode(Object value) {
        if (value instanceof JsonNode) {
            return ((JsonNode) value).isEmpty();
        }
        return false;
    }

    private boolean isEmptyCollection(Object value) {
        if (value instanceof Collection) {
            return ((Collection<?>) value).isEmpty();
        }
        return false;
    }

    private boolean isBlankString(Object value) {
        if (value instanceof String) {
            return ((String) value).isBlank();
        }
        return false;
    }
}
