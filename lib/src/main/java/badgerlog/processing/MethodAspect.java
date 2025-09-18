package badgerlog.processing;

import badgerlog.Dashboard;
import badgerlog.annotations.Entry;
import badgerlog.annotations.EntryType;
import badgerlog.annotations.configuration.Configuration;
import badgerlog.networktables.EntryFactory;
import badgerlog.networktables.NTEntry;
import badgerlog.networktables.NTUpdatable;
import badgerlog.utilities.ErrorLogger;
import badgerlog.utilities.KeyParser;
import badgerlog.utilities.Methods;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Aspect("pertarget(getterMethodExecution(badgerlog.annotations.Entry) && onlyRobotCode())")
public class MethodAspect {
    private static final Set<String> fullyProcessedMethods = new HashSet<>();
    
    @Pointcut("!within(edu.wpi.first..*) && !within(badgerlog..*) && !within(java..*) && !within(javax..*)")
    public void onlyRobotCode() {}
    
    @Pointcut("@annotation(entry) && execution(* *()) && !execution(void *())")
    public void getterMethodExecution(Entry entry) {}

    @Pointcut("execution(*.new(..))")
    public void newInitialization() {}

    @After("onlyRobotCode() && newInitialization()")
    public void createFieldEntries(JoinPoint joinPoint){
        Object instance = joinPoint.getThis();
        Method[] methods = Methods.getMethodsWithAnnotation(instance.getClass(), Entry.class);
        Arrays.stream(methods)
                .filter(method -> !fullyProcessedMethods.contains(method.getName()))
                .filter(this::hasValidEntryAnnotation)
                .forEach(method -> createMethodEntry(method, instance));
    }
    
    private void createMethodEntry(Method method, Object instance){
        String name =  method.getName();
        Configuration config = Configuration.createConfigurationFromAnnotations(method);
        
        if(config.getKey() == null || !KeyParser.hasFieldKey(config.getKey())){
            fullyProcessedMethods.add(name);
        }
        
        KeyParser.createKeyFromMember(config, method, instance);

        if (!config.isValidConfiguration()) {
            ErrorLogger.methodError(method, "had an invalid configuration created");
            return;
        }
        
        NTEntry<Object> entry = EntryFactory.createNetworkTableEntryFromValue(config.getKey(), Methods.invokeMethod(method, instance), config);
        Dashboard.addNetworkTableEntry(config.getKey(), (NTUpdatable) () -> entry.publishValue(Methods.invokeMethod(method, instance)));
    }
    
    private boolean hasValidEntryAnnotation(Method method){
        Entry annotation = method.getAnnotation(Entry.class);
        return annotation != null && annotation.value() == EntryType.PUBLISHER;
    }
}
