package no.unit.nva.hamcrest;

import static java.util.Objects.isNull;
import com.fasterxml.jackson.databind.JsonNode;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PropertyValuePair {

    public static final String ERROR_INVOKING_GETTER = "Could not get value for method: ";
    public static final String FIELD_PATH_DELIMITER = ".";
    public static final String ROOT_OBJECT_PATH = "";
    private final String propertyName;
    private final Object value;
    private final String fieldPath;

    public PropertyValuePair(String propertyName, Object value, String parentPath) {
        this.propertyName = propertyName;
        this.value = value;

        this.fieldPath = formatFieldPathInfo(propertyName, parentPath);
    }

    private String formatFieldPathInfo(String propertyName, String parentPath) {
        if (isRootObject()) {
            return ROOT_OBJECT_PATH;
        } else {
            return parentPath + FIELD_PATH_DELIMITER + propertyName;
        }
    }

    private boolean isRootObject() {
        return isNull(propertyName);
    }

    public static PropertyValuePair rootObject(Object object) {
        return new PropertyValuePair(null, object, ROOT_OBJECT_PATH);
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

    public List<PropertyValuePair> children() {
        BeanInfo beanInfo = getBeanInfo(value);
        List<PropertyDescriptor> properties = Arrays.stream(beanInfo.getPropertyDescriptors())
            .collect(Collectors.toList());

        List<PropertyValuePair> result = properties.stream()
            .map(getter -> extractFieldValues(value, getter))
            .collect(Collectors.toList());
        return result;
    }

    private BeanInfo getBeanInfo(Object actual) {
        try {
            return Introspector.getBeanInfo(actual.getClass());
        } catch (IntrospectionException e) {
            throw new RuntimeException(e);
        }
    }

    private PropertyValuePair extractFieldValues(Object actual, PropertyDescriptor propertyDescriptor) {
        try {
            return new PropertyValuePair(
                propertyDescriptor.getName(),
                propertyDescriptor.getReadMethod().invoke(actual),
                this.fieldPath
            );
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(ERROR_INVOKING_GETTER + propertyDescriptor.getName(), e);
        }
    }

    public boolean isNotBaseType() {
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
}