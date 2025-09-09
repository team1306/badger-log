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
import java.util.Set;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@SupportedAnnotationTypes("badgerlog.annotations.Entry")
public class AnnotationProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Elements elementUtils = processingEnv.getElementUtils();

        // Use string literals for class names
        TypeElement sendableElement = elementUtils.getTypeElement("edu.wpi.first.util.sendable.Sendable");
        if (sendableElement == null) {
            printError(null, Diagnostic.Kind.ERROR,
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
        }
        return false;
    }

    private void validateMethod(ExecutableElement method, Entry annotation) {
        int paramCount = method.getParameters().size();
        String methodName = method.getSimpleName().toString();
        String className = method.getEnclosingElement().getSimpleName().toString();

        switch (annotation.value()) {
            case PUBLISHER -> {
                if (paramCount != 0) {
                    printError(method, Kind.ERROR, String.format("Publisher @Entry annotated method '%s.%s()' must have exactly zero parameters, found %d",
                            className, methodName, paramCount));
                }
                if (method.getReturnType().getKind() == TypeKind.VOID) {
                    printError(method, Kind.ERROR, String.format("Publisher @Entry annotated method '%s.%s() must return a value",
                            className, methodName));
                }
            }
            case SUBSCRIBER -> {
                if (paramCount != 1) {
                    printError(method, Kind.ERROR, String.format("Subscriber @Entry annotated method '%s.%s()' must have exactly one parameters, found %d",
                            className, methodName, paramCount));
                }
                if (method.getReturnType().getKind() == TypeKind.VOID) {
                    printError(method, Kind.WARNING, String.format("Subscriber @Entry annotated method '%s.%s() should not return a value",
                            className, methodName));
                }
            }
            case SENDABLE ->
                    printError(method, Kind.ERROR, String.format("Sendable @Entry method '%s.%s()' cannot be a Sendable", className, methodName));
        }
    }

    private void validateField(TypeMirror sendableMirror, VariableElement field, Entry annotation) {
        String fieldName = field.getSimpleName().toString();
        String className = field.getEnclosingElement().getSimpleName().toString();

        switch (annotation.value()) {
            case PUBLISHER, SUBSCRIBER -> {
                if(field.getModifiers().contains(Modifier.FINAL)){
                    printError(field, Kind.ERROR, String.format("Non-Sendable @Entry field '%s.%s' must be non-final", className, fieldName));
                }
            }
            case SENDABLE -> {
                if(!processingEnv.getTypeUtils().isSameType(sendableMirror, field.asType())) {
                    printError(field, Kind.ERROR, String.format("Sendable @Entry field '%s.%s' must be a Sendable", className, fieldName));
                }
            }
        }
    }

    private void printError(Element element, Kind messageType, String message) {
        if (element != null) {
            processingEnv.getMessager().printMessage(messageType, message, element);
        } else {
            processingEnv.getMessager().printMessage(messageType, message);
        }
    }
}
