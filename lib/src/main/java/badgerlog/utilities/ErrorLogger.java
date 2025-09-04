package badgerlog.utilities;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ErrorLogger {
    public static void methodError(Method method, String message){
        System.err.println(method.getDeclaringClass().getSimpleName() + "." + method
                .getName() + "() " + message + ".\nSKIPPING\n");
    }
    
    public static void fieldError(Field field, String message){
        System.err.println(field.getDeclaringClass().getSimpleName() + "." + field
                .getName() + " " + message + ".\nSKIPPING\n");
    }
    
    public static void normalError(String message){
        System.err.println(message + ". SKIPPING");
    }
    
    public static void customError(String message){
        System.err.println(message);
    }
}
