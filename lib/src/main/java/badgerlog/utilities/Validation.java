package badgerlog.utilities;

import java.lang.reflect.Method;

public class Validation {
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

    public static boolean validateInterceptorMethod(Method method) {
        if (method.getReturnType().equals(void.class)) {
            ErrorLogger.memberError(method, " does not return a value");
            return false;
        }

        if (method.getParameterCount() != 1) {
            ErrorLogger.memberError(method, " cannot have more than one parameter");
            return false;
        }

        return true;
    }

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
