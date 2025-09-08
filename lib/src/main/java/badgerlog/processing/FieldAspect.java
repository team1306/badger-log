package badgerlog.processing;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.DeclareError;
import org.aspectj.lang.annotation.DeclareWarning;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class FieldAspect {

    @DeclareError("get(@Entry final * *) || set(@Entry final * *)")
    public static final String finalFieldError =
            "ERROR: @Entry fields cannot be final - they must be mutable for entry management";

    @DeclareWarning("get(@Entry static * *) || set(@Entry static * *)")
    public static final String staticFieldWarning =
            "WARNING: @Entry fields should not be static";

    @Pointcut("!within(edu.wpi.first..*) && !within(badgerlog.processing..*))")
    public void onlyRobotCode() {}
    
    @Pointcut("initialization(*.new(..))")
    public void anyInitialization() {}
}
