package badgerlog.processing;

import badgerlog.annotations.Entry;
import badgerlog.annotations.Key;
import com.google.auto.service.AutoService;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.Diagnostic.Kind;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Processes any annotations annotated with {@link Entry} and ensures that they match the requirements.
 */
@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@SupportedAnnotationTypes("badgerlog.annotations.Entry")
public class EntryAnnotationProcessor extends AbstractProcessor {

    private final Map<String, String> potentialKeys = new HashMap<>();
    
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Elements elementUtils = processingEnv.getElementUtils();

        TypeElement sendableElement = elementUtils.getTypeElement("edu.wpi.first.util.sendable.Sendable");
        TypeElement objectElement = elementUtils.getTypeElement("java.lang.Object");
        
        if (sendableElement == null) {
            printMessage(null, Diagnostic.Kind.ERROR,
                    "Sendable interface not found");
            return false;
        }

        if (objectElement == null) {
            printMessage(null, Diagnostic.Kind.ERROR,
                    "Object not found");
            return false;
        }

        TypeMirror sendableType = sendableElement.asType();
        TypeMirror objectType = objectElement.asType();

        for (Element element : roundEnv.getElementsAnnotatedWith(Entry.class)) {
            Entry annotation = element.getAnnotation(Entry.class);
            
            switch (element.getKind()) {
                case METHOD -> {
                    ExecutableElement method = (ExecutableElement) element;
                    validateMethod(objectType, method, annotation);
                }
                case FIELD -> {
                    VariableElement field = (VariableElement) element;
                    validateField(sendableType, field, annotation);
                }
                default -> {}
            }
            
            String potentialKey = generatePossibleKey(element);
            if(potentialKeys.containsKey(potentialKey)){
                printMessage(element, Kind.MANDATORY_WARNING, String.format("Duplicate key %s for '%s' and '%s'", potentialKey, potentialKeys.get(potentialKey), createElementName(element)));
            }
            else{
                potentialKeys.put(potentialKey, createElementName(element));
            }
        }
        return false;
    }
    
    private String createElementName(Element element) {
        String methodName = element.getSimpleName().toString();
        String className = element.getEnclosingElement().getSimpleName().toString();
        
        return String.format("%s.%s", className, methodName);
    }
    
    private String generatePossibleKey(Element element) {
        if(element.getAnnotation(Key.class) != null){
            return element.getAnnotation(Key.class).value();
        }
        return element.getSimpleName() + "/" + element.getEnclosingElement().getSimpleName();
    }

    private void validateMethod(TypeMirror objectType, ExecutableElement method, Entry annotation) {
        Types typeUtils =  processingEnv.getTypeUtils();
        int paramCount = method.getParameters().size();

        switch (annotation.value()) {
            case PUBLISHER -> {
                if (paramCount != 0) {
                    printMessage(method, Kind.ERROR, String.format("Publisher @Entry annotated method '%s()' must have exactly zero parameters, found %d",
                            createElementName(method), paramCount));
                }
                if (method.getReturnType().getKind() == TypeKind.VOID) {
                    printMessage(method, Kind.ERROR, String.format("Publisher @Entry annotated method '%s() must return a value",
                            createElementName(method)));
                }
                if(typeUtils.isSameType(method.getReturnType(), objectType)){
                    printMessage(method, Kind.WARNING, String.format("Publisher @Entry method '%s()' should return the final type and not object", 
                            createElementName(method)));
                }
            }
            case SUBSCRIBER -> printMessage(method, Kind.ERROR, String.format("Subscriber @Entry method '%s()' cannot be a Subscriber", createElementName(method)));
            case SENDABLE -> printMessage(method, Kind.ERROR, String.format("Sendable @Entry method '%s()' cannot be a Sendable", createElementName(method)));
            case INTELLIGENT -> printMessage(method, Kind.ERROR, String.format("Intelligent @Entry method '%s()' should not use intelligent", createElementName(method)));
        }
    }

    private void validateField(TypeMirror sendableMirror, VariableElement field, Entry annotation) {
        Types typeUtils =  processingEnv.getTypeUtils();
        
        switch (annotation.value()) {
            case PUBLISHER, SUBSCRIBER, INTELLIGENT -> {
                if(field.getModifiers().contains(Modifier.FINAL)){
                    printMessage(field, Kind.ERROR, String.format("Non-Sendable @Entry field '%s' must be non-final", createElementName(field)));
                }
            }
            case SENDABLE -> {
                if(!typeUtils.isSameType(sendableMirror, field.asType())) {
                    printMessage(field, Kind.ERROR, String.format("Sendable @Entry field '%s' must be a Sendable", createElementName(field)));
                }
            }
        }
    }

    private void printMessage(Element element, Kind messageType, String message) {
        if (element != null) {
            processingEnv.getMessager().printMessage(messageType, message, element);
        } else {
            processingEnv.getMessager().printMessage(messageType, message);
        }
    }
}
