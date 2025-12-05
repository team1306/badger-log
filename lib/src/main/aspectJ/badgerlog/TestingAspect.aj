package badgerlog;

public aspect TestingAspect issingleton() {

    pointcut mainCalled() :
            execution(* robotInit(..));
    
    after() : mainCalled(){
        // Call any class / method from the other source set
        System.out.println("test");
    }
}
