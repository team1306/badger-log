package badgerlog.processing;

import badgerlog.annotations.RawWatcher;
import badgerlog.annotations.Watcher;
import com.google.auto.service.AutoService;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.Diagnostic.Kind;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Processes any annotated elements with {@link Watcher} or {@link RawWatcher} and ensures that they match the requirements.
 */
@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@SupportedAnnotationTypes({"badgerlog.annotations.Watcher", "badgerlog.annotations.RawWatcher"})
public class EventAnnotationProcessor extends AbstractProcessor {
    private final Map<String, String> eventNames = new HashMap<>();

    private Types typeUtils;
    private Elements elementUtils;
    
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.typeUtils = processingEnv.getTypeUtils();
        this.elementUtils = processingEnv.getElementUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        TypeElement eventDataElement = elementUtils.getTypeElement("badgerlog.events.EventData");
        TypeElement objectElement = elementUtils.getTypeElement("java.lang.Object");

        if (eventDataElement == null) {
            printMessage(null, Diagnostic.Kind.ERROR, "EventData object not found");
            return false;
        }

        if (objectElement == null) {
            printMessage(null, Diagnostic.Kind.ERROR, "Object not found");
            return false;
        }

        TypeMirror objectType = objectElement.asType();
        
        for(Element element : roundEnv.getElementsAnnotatedWith(Watcher.class)){
            Watcher watcher = element.getAnnotation(Watcher.class);
            
            TypeMirror annotationType = getTypeFromWatcherAnnotation(element);
            validateEvent((ExecutableElement) element, annotationType, eventDataElement, objectType);
                       
            
            String name = watcher.name();
            if (eventNames.containsKey(name)) {
                printMessage(element, Kind.MANDATORY_WARNING, String
                        .format("Duplicate name %s for '%s()' and '%s()'", name, eventNames.get(name), createElementName(element)));
            } else {
                eventNames.put(name, createElementName(element));
            }
        }

        for(Element element : roundEnv.getElementsAnnotatedWith(RawWatcher.class)){
            RawWatcher watcher = element.getAnnotation(RawWatcher.class);

            TypeMirror annotationType = getTypeFromRawWatcherAnnotation(element);
            validateEvent((ExecutableElement) element, annotationType, eventDataElement, objectType);
            
            validateRawEvent((ExecutableElement) element, watcher);
        }
        
        return false;
    }

    private void validateRawEvent(ExecutableElement method, RawWatcher annotation) {
        for(String key : annotation.keys()) {
            if(!key.startsWith("/")){
                printMessage(method, Kind.ERROR, String
                        .format("Key '%s' in @RawWatcher annotated method '%s()' must begin with '/'", key, createElementName(method)));
            }
        }
    }
    
    private void validateEvent(ExecutableElement method, TypeMirror annotationType, TypeElement eventType, TypeMirror objectType){
        List<?> parameters = method.getParameters();
        VariableElement variableElement = (VariableElement) parameters.get(0);
        TypeMirror parameterType = variableElement.asType();
        
        TypeMirror voidType = typeUtils.getNoType(TypeKind.VOID);
        
        if(parameters.size() != 1) {
            printMessage(method, Kind.ERROR, String
                    .format("Event annotated method '%s()' must have exactly one parameters, found %d", createElementName(method), parameters.size()));
        }
        
        if (!typeUtils.isSameType(voidType, annotationType)){
            if (!isEventDataType(parameterType, eventType.asType())) {
                printMessage(method, Kind.ERROR, String
                        .format("Event annotated method '%s()' must have a parameter of type EventData, found %s", createElementName(method), parameterType));
            }
            else{
                checkGenericType(method, parameterType, annotationType);
            }
        }else{
            checkGenericType(method, parameterType, objectType);
        }
        
    }

    private boolean isEventDataType(TypeMirror type, TypeMirror eventDataType) {
        TypeMirror erasedType = typeUtils.erasure(type);
        TypeMirror erasedEventType = typeUtils.erasure(eventDataType);
        
        return typeUtils.isSameType(erasedType, erasedEventType);
    }

    private void checkGenericType(ExecutableElement method, TypeMirror paramType, TypeMirror expectedType) {
        if (!(paramType instanceof DeclaredType declaredType)) {
            printMessage(method, Kind.ERROR, String.format("Event method '%s()' parameter EventData should be typed", createElementName(method)));
            return;
        }

        List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();

        if(typeArguments.size() != 1) {
            printMessage(method, Kind.ERROR, String.format("Event method '%s()' parameter EventData must have exactly 1 type parameter", createElementName(method)));
            return;
        }
        
        TypeMirror actualType = typeArguments.get(0);

        if (!typesMatch(actualType, expectedType)) {
            printMessage(method, Kind.ERROR, String.format("Event method '%s()' declared '%s' in annotation, but had '%s' in parameter", createElementName(method), expectedType, actualType));
        }
    }

    private boolean typesMatch(TypeMirror type1, TypeMirror type2) {
        if (typeUtils.isSameType(type1, type2)) {
            return true;
        }
        
        TypeMirror boxed1 = type1.getKind().isPrimitive() ? typeUtils.boxedClass((javax.lang.model.type.PrimitiveType) type1).asType() : type1;
        TypeMirror boxed2 = type2.getKind().isPrimitive() ? typeUtils.boxedClass((javax.lang.model.type.PrimitiveType) type2).asType() : type2;
        
        if (typeUtils.isSameType(boxed1, boxed2)) {
            return true;
        }
        
        TypeMirror unboxed1 = tryUnbox(type1);
        TypeMirror unboxed2 = tryUnbox(type2);

        if (unboxed1 != null && typeUtils.isSameType(unboxed1, type2)) {
            return true;
        }

        if (unboxed2 != null && typeUtils.isSameType(type1, unboxed2)) {
            return true;
        }

        return false;
    }

    private TypeMirror tryUnbox(TypeMirror type) {
        if (type.getKind().isPrimitive()) {
            return null;
        }

        try {
            return typeUtils.unboxedType(type);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private TypeMirror getTypeFromWatcherAnnotation(Element element) {
        for (AnnotationMirror mirror : element.getAnnotationMirrors()) {
            if (mirror.getAnnotationType().toString().equals("badgerlog.annotations.Watcher")) {
                for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry
                        : mirror.getElementValues().entrySet()) {
                    if (entry.getKey().getSimpleName().toString().equals("type")) {
                        return (TypeMirror) entry.getValue().getValue();
                    }
                }
            }
        }

        try {
            Watcher annotation = element.getAnnotation(Watcher.class);
            annotation.type();
        } catch (MirroredTypeException mte) {
            return mte.getTypeMirror();
        }

        return null;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private TypeMirror getTypeFromRawWatcherAnnotation(Element element) {
        for (AnnotationMirror mirror : element.getAnnotationMirrors()) {
            if (mirror.getAnnotationType().toString().equals("badgerlog.annotations.RawWatcher")) {
                for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry
                        : mirror.getElementValues().entrySet()) {
                    if (entry.getKey().getSimpleName().toString().equals("type")) {
                        return (TypeMirror) entry.getValue().getValue();
                    }
                }
            }
        }

        try {
            RawWatcher annotation = element.getAnnotation(RawWatcher.class);
            annotation.type(); 
        } catch (MirroredTypeException mte) {
            return mte.getTypeMirror();
        }

        return null;
    }

    private String createElementName(Element element) {
        String methodName = element.getSimpleName().toString();
        String className = element.getEnclosingElement().getSimpleName().toString();

        return String.format("%s.%s", className, methodName);
    }

    private void printMessage(Element element, Kind messageType, String message) {
        if (element != null) {
            processingEnv.getMessager().printMessage(messageType, message, element);
        } else {
            processingEnv.getMessager().printMessage(messageType, message);
        }
    }
}
