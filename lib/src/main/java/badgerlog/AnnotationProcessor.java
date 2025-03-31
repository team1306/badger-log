package badgerlog;

import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
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
                var className = variableElement.getEnclosingElement().toString();
                
                int flags = 0;
                if (!modifiers.contains(Modifier.STATIC)) flags += 1;
                if (modifiers.contains(Modifier.FINAL)) flags += 2;
                    
                switch (flags) {
                    case 1:
                        printErrorMessage(String.format("%s in %s must be static", name, className), element);
                        break;
                    case 2:
                        printErrorMessage(String.format("%s in %s must be non final", name, className), element);
                        break;
                    case 3:
                        printErrorMessage(String.format("%s in %s must be static and non-final", name, className), element);
                        break;
                }
            }
        }
        return true;
    }
    
    private void printErrorMessage(String message, Element element) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, message, element);
    }
}
