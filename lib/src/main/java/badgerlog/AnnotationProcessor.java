package badgerlog;

import badgerlog.entry.Entry;
import badgerlog.networktables.mappings.MappingType;
import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.util.Set;

/**
 * Annotation processor for BadgerLog. Verifies that each field annotated with {@link Entry} or {@link MappingType} is static, non-final, and conforms to the type restriction
 */
@SupportedAnnotationTypes(
        "badgerlog.*")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@AutoService(Processor.class)
public final class AnnotationProcessor extends AbstractProcessor {
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

                Types typeUtils = processingEnv.getTypeUtils();
                Elements elementUtils = processingEnv.getElementUtils();

                var mappingType = elementUtils.getTypeElement("badgerlog.networktables.mappings.Mapping");
                var mappingAnnotation = elementUtils.getTypeElement("badgerlog.networktables.mappings.MappingType");

                if (typeUtils.isSameType(annotation.asType(), mappingAnnotation.asType()))
                    if (!typeUtils.isSameType(variableElement.asType(), mappingType.asType()))
                        printErrorMessage(String.format("%s in %s must be of type Mapping if annotated with MappingType", name, className), element);


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
        return false;
    }

    private void printErrorMessage(String message, Element element) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, message, element);
    }
}
