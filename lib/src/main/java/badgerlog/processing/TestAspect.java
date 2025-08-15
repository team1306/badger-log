package badgerlog.processing;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class TestAspect {

    @Pointcut("execution(badgerlog.networktables.mappings.Mapping.new (..))")
    public void callAt() {

    }

    @Before("callAt()")
    public void beforeAnyVoidCall(JoinPoint in) {
        System.out.println("this is a test aspect - " + in.getThis());
    }
}
