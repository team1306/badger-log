package badgerlog.utilities;

import java.lang.reflect.Member;

public class ErrorLogger {
    public static void memberError(Member method, String message){
        System.err.println(method.getDeclaringClass().getSimpleName() + "." + method
                .getName() + "() " + message + ".\nSKIPPING\n");
    }
    
    public static void normalError(String message){
        System.err.println(message + ". SKIPPING");
    }
    
    public static void customError(String message){
        System.err.println(message);
    }
}
