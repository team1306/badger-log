package badgerlog;

import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.Diagnostic;
import java.util.Set;

/**
 * Annotation processor for both {@link badgerlog.entry.Entry} and {@link badgerlog.networktables.mappings.MappingType}
 */
@SupportedAnnotationTypes(
        "badgerlog.*")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@AutoService(Processor.class)
public class AnnotationProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement annotation : annotations) {
            Set<? extends Element> annotatedElements
                    = roundEnv.getElementsAnnotatedWith(annotation);

            for (Element element : annotatedElements) {
                var variableElement = (VariableElement) element;
                
                var modifiers = variableElement.getModifiers();
                var name = variableElement.getSimpleName().toString();

                if(!modifiers.contains(Modifier.STATIC)) 
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, name + " must be static", element);

                if(modifiers.contains(Modifier.FINAL))
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, name + " must be non final", element);
            }
        }
        return false;
    }
}
