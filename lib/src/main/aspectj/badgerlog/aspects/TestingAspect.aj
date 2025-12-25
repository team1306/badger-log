package badgerlog.aspects;

public aspect TestingAspect {

    pointcut onlyRobotCode() :
            !within(edu.wpi.first..*) &&
                    !within(badgerlog..*) &&
                    !within(java..*) &&
                    !within(javax..*);
    
    public interface TestingInterface{
    }
    
    public void TestingInterface.coolMethod(){
        System.out.println("Method in class with @Entry fields");
    }
    declare parents: hasfield(@badgerlog.annotations.Entry * *) && !is(AspectType) implements TestingInterface;
    
    
    before(TestingInterface cool) : onlyRobotCode() && set(!final * (@badgerlog.annotations.Entry *).*) && this(cool) {
        cool.coolMethod();
    }
    
}
