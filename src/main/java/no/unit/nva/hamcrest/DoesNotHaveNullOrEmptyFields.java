package no.unit.nva.hamcrest;

import static java.util.Objects.isNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

public class DoesNotHaveNullOrEmptyFields<T> extends BaseMatcher<T> {

    public static final String GETTER_GET_PREFIX = "get";
    public static final String GETTER_IS_PREFIX = "is";
    public static final String FIELD_DELIMITER = ",";

    private List<MethodInvocationResult> emptyFields;

    public static <R> DoesNotHaveNullOrEmptyFields<R> doesNotHaveNullOrEmptyFields() {
        return new DoesNotHaveNullOrEmptyFields<>();
    }

    @Override
    public boolean matches(Object actual) {
        return assertThatNoPublicFieldIsNull(actual);
    }

    @Override
    public void describeTo(Description description) {

        description.appendText("All fields to be non empty");
    }

    @Override
    public void describeMismatch(Object item, Description description) {
        String emptyFieldNames = emptyFields
            .stream()
            .map(res->res.methodName)
            .collect(Collectors.joining(
                FIELD_DELIMITER));

        description.appendText("The following fields were found empty:")
            .appendText(emptyFieldNames);
    }



    private boolean assertThatNoPublicFieldIsNull(Object insertedUser) {
        Method[] methods = insertedUser.getClass().getMethods();
        Stream<MethodInvocationResult> getterInvocations = Arrays.stream(methods)
            .filter(this::isAGetter)
            .map((method -> invokeMethodWithRuntimeException(insertedUser, method)));

        List<MethodInvocationResult> emptyFields = getterInvocations.filter(this::isEmpty).collect(
            Collectors.toList());
        this.emptyFields =emptyFields;
        return emptyFields.isEmpty();
    }

    private MethodInvocationResult invokeMethodWithRuntimeException(Object insertedUser, Method method) {
        try {
            return invokeMethod(insertedUser, method);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private MethodInvocationResult invokeMethod(Object insertedUser, Method method)
        throws IllegalAccessException, InvocationTargetException {
        Object result = method.invoke(insertedUser);
        return new MethodInvocationResult(method.getName(), result);
    }

    private boolean isAGetter(Method m) {
        return m.getName().startsWith(GETTER_GET_PREFIX) || m.getName().startsWith(GETTER_IS_PREFIX);
    }

    private static class MethodInvocationResult {

        public final String methodName;
        public final Object result;

        public MethodInvocationResult(String methodName, Object result) {
            this.methodName = methodName;
            this.result = result;
        }

        public String toString() {
            return this.methodName;
        }
    }

    private boolean isEmpty(MethodInvocationResult mir) {
        if (isNull(mir.result)) {
            return true;
        } else {
            if (mir.result instanceof Collection<?>) {
                Collection col = (Collection) mir.result;
                return col.isEmpty();
            } else if (mir.result instanceof Map<?, ?>) {
                Map map = (Map) mir.result;
                return map.isEmpty();
            } else if(mir.result instanceof String){
                String str = (String)mir.result;
                return str.isBlank();
            }
            else {
                return false;
            }
        }
    }
}
