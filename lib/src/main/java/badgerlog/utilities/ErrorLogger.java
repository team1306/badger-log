package badgerlog.utilities;

import java.lang.reflect.Member;

/**
 * Logs any errors that happen in entry creation or management.
 */
public class ErrorLogger {
    /**
     * Logs an error from a specific {@link Member}.
     * 
     * <p>It is in the form of: '{declaring class}.{member name} {message}. SKIPPING'</p>
     * @param member the member to log from
     * @param message the message to log
     */
    public static void memberError(Member member, String message){
        System.err.println(member.getDeclaringClass().getSimpleName() + "." + member
                .getName() + " " + message + ".\nSKIPPING\n");
    }

    /**
     * Logs a normal error without a specified source.
     * 
     * <p>It is in the form of: '{message}. SKIPPING'</p>
     * @param message the message to log
     */
    public static void normalError(String message){
        System.err.println(message + ". SKIPPING");
    }

    /**
     * Logs a completely custom error without any additional additions
     * 
     * @param message the message to log
     */
    public static void customError(String message){
        System.err.println(message);
    }
}
