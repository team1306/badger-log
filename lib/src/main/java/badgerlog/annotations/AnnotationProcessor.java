package badgerlog.annotations;

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
import javax.tools.Diagnostic;
import javax.tools.Diagnostic.Kind;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@SupportedAnnotationTypes("badgerlog.annotations.Entry")
public class AnnotationProcessor extends AbstractProcessor {

    public Map<String, String> potentialKeys = new HashMap<>();
    
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Elements elementUtils = processingEnv.getElementUtils();

        // Use string literals for class names
        TypeElement sendableElement = elementUtils.getTypeElement("edu.wpi.first.util.sendable.Sendable");
        if (sendableElement == null) {
            printMessage(null, Diagnostic.Kind.ERROR,
                    "Sendable interface not found");
            return false;
        }

        TypeMirror sendableType = sendableElement.asType();

        for (Element element : roundEnv.getElementsAnnotatedWith(Entry.class)) {
            Entry annotation = element.getAnnotation(Entry.class);

            switch (element.getKind()) {
                case METHOD -> {
                    ExecutableElement method = (ExecutableElement) element;
                    validateMethod(method, annotation);
                }
                case FIELD -> {
                    VariableElement field = (VariableElement) element;
                    validateField(sendableType, field, annotation);
                }
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

    private void validateMethod(ExecutableElement method, Entry annotation) {
        int paramCount = method.getParameters().size();
        String methodName = method.getSimpleName().toString();
        String className = method.getEnclosingElement().getSimpleName().toString();

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
            }
            case SUBSCRIBER -> {
                if (paramCount != 1) {
                    printMessage(method, Kind.ERROR, String.format("Subscriber @Entry annotated method '%s()' must have exactly one parameters, found %d",
                            createElementName(method), paramCount));
                }
                if (method.getReturnType().getKind() == TypeKind.VOID) {
                    printMessage(method, Kind.WARNING, String.format("Subscriber @Entry annotated method '%s() should not return a value",
                            createElementName(method)));
                }
            }
            case SENDABLE -> printMessage(method, Kind.ERROR, String.format("Sendable @Entry method '%s()' cannot be a Sendable", createElementName(method)));
        }
    }

    private void validateField(TypeMirror sendableMirror, VariableElement field, Entry annotation) {
        String fieldName = field.getSimpleName().toString();
        String className = field.getEnclosingElement().getSimpleName().toString();

        switch (annotation.value()) {
            case PUBLISHER, SUBSCRIBER -> {
                if(field.getModifiers().contains(Modifier.FINAL)){
                    printMessage(field, Kind.ERROR, String.format("Non-Sendable @Entry field '%s' must be non-final", createElementName(field)));
                }
            }
            case SENDABLE -> {
                if(!processingEnv.getTypeUtils().isSameType(sendableMirror, field.asType())) {
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
