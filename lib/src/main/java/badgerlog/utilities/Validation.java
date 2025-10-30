package badgerlog.utilities;

import java.lang.reflect.Method;

/**
 * Checks for violations of predefined rules for generation
 */
public class Validation {
    /**
     * Checks if the {@code method} returns void and has only one parameter. Prints issues using {@link ErrorLogger}
     *
     * @param method the method to validate
     *
     * @return if the method is void and has one parameter
     */
    public static boolean validateWatcherMethod(Method method) {
        if (!method.getReturnType().equals(void.class)) {
            ErrorLogger.memberError(method, " does not return void");
            return false;
        }

        if (method.getParameterCount() != 1) {
            ErrorLogger.memberError(method, " cannot have more than one parameter");
            return false;
        }

        return true;
    }

    /**
     * Checks if an expected {@link Enum} Class is a properly defined {@code enum} containing at least 1 constant.
     *
     * @param tEnum the enum to check
     *
     * @return if the class is an enum and has at least 1 defined constant
     *
     * @param <T> a defined enum type
     */
    public static <T extends Enum<T>> boolean validateEnum(Class<T> tEnum) {
        if (!tEnum.isEnum()) {
            ErrorLogger.normalError("Tried to create an enum selector, but the class was not an enum");
            return false;
        }
        if (tEnum.getEnumConstants().length == 0) {
            ErrorLogger.normalError("Tried to create an enum selector, but the enum had no values");
            return false;
        }
        return true;
    }
}
