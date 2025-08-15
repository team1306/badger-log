package badgerlog;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class TestAspect {

    @Pointcut("execution(void badgerlog.Dashboard.initialize (..))")
    public void callAt() {

    }

    @Before("callAt()")
    public void beforeAnyVoidCall() {
        System.out.println("this is a test aspect");
    }
}
