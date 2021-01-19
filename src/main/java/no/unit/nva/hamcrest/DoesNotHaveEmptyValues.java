package no.unit.nva.hamcrest;

import static java.util.Objects.isNull;
import com.fasterxml.jackson.databind.JsonNode;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

public class DoesNotHaveEmptyValues<T> extends BaseMatcher<T> {

    public static final String ERROR_INVOKING_GETTER = "Could not get value for method: ";
    public static final String EMPTY_FIELD_ERROR = "Empty field found: ";
    public static final String FIELD_DELIMITER = ",";
    public static final String FIELD_PATH_DELIMITER = ".";
    public static final String TEST_DESCRIPTION = "All fields of all included objects need to be non empty";

    private final List<PropertyValuePair> emptyFields;
    private List<Class<?>> ignoreList;

    public DoesNotHaveEmptyValues() {
        super();
        ignoreList = doNotCheckRecursively();

        this.emptyFields = new ArrayList<>();
    }

    public static <R> DoesNotHaveEmptyValues<R> doesNotHaveEmptyValuesIgnoringClasses() {
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
        ArrayList<Class<?>> newIgnoreList = new ArrayList<>();
        newIgnoreList.addAll(matcher.ignoreList);
        newIgnoreList.addAll(ignoreList);
        matcher.ignoreList = newIgnoreList;
        return matcher;
    }

    @Override
    public boolean matches(Object actual) {
        return check(actual, "");
    }

    public boolean check(Object fieldValue, String fieldPath) {
        List<PropertyDescriptor> properties = extractProperties(fieldValue);
        List<PropertyValuePair> propertyValuePairs = constructPropertyValuePairs(fieldValue, properties);
        for (PropertyValuePair propValue : propertyValuePairs) {
            checkRecursivelyForEmptyFields(fieldPath, propValue);
        }
        List<PropertyValuePair> emptyValues = collectEmptyFields(propertyValuePairs, fieldPath);
        emptyFields.addAll(emptyValues);
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
        return List.of(
            String.class,
            Integer.class,
            Double.class,
            Float.class,
            Boolean.class,
            Map.class,
            Collection.class,
            JsonNode.class,
            Class.class,
            URI.class
        );
    }

    private void checkRecursivelyForEmptyFields(String fieldPath, PropertyValuePair propValue) {
        if (isNotBaseType(propValue.getValue()) && shouldNotBeIgnored(propValue.getValue())) {
            String valueFieldPath = updateFieldPath(fieldPath, propValue);
            check(propValue.getValue(), valueFieldPath);
        }
    }

    private boolean shouldNotBeIgnored(Object value) {
        return
            ignoreList.stream()
                .noneMatch(ignoredClass -> ignoredClass.isInstance(value));
    }

    private List<PropertyValuePair> collectEmptyFields(List<PropertyValuePair> propertyValuePairs, String fieldPath) {
        return propertyValuePairs.stream()
            .filter(propertyValue -> isEmpty(propertyValue.getValue()))
            .map(propertyValuePair -> propertyValuePair.updateFieldPath(fieldPath))
            .collect(Collectors.toList());
    }

    private String updateFieldPath(String fieldPath, PropertyValuePair propValue) {
        return fieldPath + FIELD_PATH_DELIMITER + propValue.getPropertyName();
    }

    private List<PropertyValuePair> constructPropertyValuePairs(Object fieldValue,
                                                                List<PropertyDescriptor> properties) {
        return properties.stream()
            .map(getter -> extractFieldValues(fieldValue, getter))
            .collect(Collectors.toList());
    }

    private List<PropertyDescriptor> extractProperties(Object fieldValue) {
        BeanInfo beanInfo = getBeanInfo(fieldValue);
        return Arrays.stream(beanInfo.getPropertyDescriptors())
            .collect(Collectors.toList());
    }

    private boolean isNotBaseType(Object value) {

        return !
            (
                isNull(value)
                || value.getClass().isPrimitive()
                || value instanceof String
                || value instanceof Integer
                || value instanceof Double
                || value instanceof Float
                || value instanceof Boolean
                || value instanceof Map
                || value instanceof Collection
                || value instanceof JsonNode
                || value instanceof Class
            );
    }

    private BeanInfo getBeanInfo(Object actual) {
        try {
            return Introspector.getBeanInfo(actual.getClass());
        } catch (IntrospectionException e) {
            throw new RuntimeException(e);
        }
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

    private PropertyValuePair extractFieldValues(Object actual, PropertyDescriptor propertyDescriptor) {
        try {
            return new PropertyValuePair(
                propertyDescriptor.getName(),
                propertyDescriptor.getReadMethod().invoke(actual)
            );
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(ERROR_INVOKING_GETTER + propertyDescriptor.getName(), e);
        }
    }

    private static class PropertyValuePair {

        private final String propertyName;
        private final Object value;
        private String fieldPath;

        public PropertyValuePair(String propertyName, Object value) {
            this.propertyName = propertyName;
            this.value = value;
        }

        public String getPropertyName() {
            return propertyName;
        }

        public Object getValue() {
            return value;
        }

        public String getFieldPath() {
            return fieldPath;
        }

        public PropertyValuePair updateFieldPath(String parent) {
            this.fieldPath = parent + FIELD_PATH_DELIMITER + propertyName;
            return this;
        }
    }
}
