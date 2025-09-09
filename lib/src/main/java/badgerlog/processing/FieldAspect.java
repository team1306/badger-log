package badgerlog.processing;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class FieldAspect {
    
    @Pointcut("!within(edu.wpi.first..*) && !within(badgerlog.processing..*))")
    public void onlyRobotCode() {}
    
    @Pointcut("initialization(*.new(..))")
    public void anyInitialization() {}
    
    @Pointcut("get(@badgerlog.annotations.Entry * *))")
    public void entryAccess(){}
    
    @Pointcut("set(@badgerlog.annotations.Entry * *)")
    public void entryUpdate(){}
}
