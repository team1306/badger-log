package team1306;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTypesUtil;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class BadgerLogInspection extends AbstractBaseJavaLocalInspectionTool {

    private final Map<String, PsiElement> keyTracker = new HashMap<>();

    @Override
    public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        keyTracker.clear();

        return new JavaElementVisitor() {
            @Override
            public void visitAnnotation(@NotNull PsiAnnotation annotation) {
                super.visitAnnotation(annotation);

                String qualifiedName = annotation.getQualifiedName();
                if (!"badgerlog.annotations.Entry".equals(qualifiedName)) {
                    return;
                }

                PsiModifierListOwner owner = (PsiModifierListOwner) annotation.getParent().getParent();
                PsiAnnotationMemberValue entryTypeValue = annotation.findAttributeValue("value");

                if (entryTypeValue == null) {
                    return;
                }

                String entryType = entryTypeValue.getText();

                if (owner instanceof PsiMethod method) {
                    validateMethod(method, entryType, holder);
                } else if (owner instanceof PsiField field) {
                    validateField(field, entryType, holder);
                }

                checkDuplicateKeys(owner, holder);
            }
        };
    }

    private void validateMethod(PsiMethod method, String entryType, ProblemsHolder holder) {
        int paramCount = method.getParameterList().getParametersCount();
        PsiType returnType = method.getReturnType();

        if (entryType.contains("PUBLISHER")) {
            if (paramCount != 0) {
                holder.registerProblem(
                        Objects.requireNonNull(method.getNameIdentifier()),
                        String.format("Publisher @Entry method must have zero parameters, found %d", paramCount),
                        ProblemHighlightType.ERROR
                );
            }

            if (returnType != null && returnType.equals(PsiTypes.voidType())) {
                holder.registerProblem(
                        method.getNameIdentifier(),
                        "Publisher @Entry method must return a value",
                        ProblemHighlightType.ERROR
                );
            }

            if (returnType != null && returnType.equalsToText("java.lang.Object")) {
                holder.registerProblem(
                        method.getNameIdentifier(),
                        "Publisher @Entry method should return the final type and not Object",
                        ProblemHighlightType.WARNING
                );
            }
        } else if (entryType.contains("SUBSCRIBER")) {
            holder.registerProblem(
                    method.getNameIdentifier(),
                    "Subscriber @Entry cannot be used on methods",
                    ProblemHighlightType.ERROR
            );
        } else if (entryType.contains("SENDABLE")) {
            holder.registerProblem(
                    method.getNameIdentifier(),
                    "Sendable @Entry cannot be used on methods",
                    ProblemHighlightType.ERROR
            );
        } else if (entryType.contains("INTELLIGENT")) {
            holder.registerProblem(
                    method.getNameIdentifier(),
                    "Intelligent @Entry should not be used on methods",
                    ProblemHighlightType.ERROR
            );
        }
    }

    private void validateField(PsiField field, String entryType, ProblemsHolder holder) {
        boolean isFinal = field.hasModifierProperty(PsiModifier.FINAL);

        if (entryType.contains("PUBLISHER") || entryType.contains("SUBSCRIBER") || entryType.contains("INTELLIGENT")) {
            if (isFinal) {
                holder.registerProblem(
                        field.getNameIdentifier(),
                        "Non-Sendable @Entry field must be non-final",
                        ProblemHighlightType.ERROR
                );
            }
        } else if (entryType.contains("SENDABLE")) {
            PsiType fieldType = field.getType();
            if (!isImplementsSendable(fieldType)) {
                holder.registerProblem(
                        field.getNameIdentifier(),
                        "Sendable @Entry field must implement edu.wpi.first.util.sendable.Sendable",
                        ProblemHighlightType.ERROR
                );
            }
        }
    }

    private boolean isImplementsSendable(PsiType type) {
        PsiClass psiClass = PsiTypesUtil.getPsiClass(type);
        if (psiClass == null) {
            return false;
        }

        for (PsiClass interfaceClass : psiClass.getInterfaces()) {
            if ("edu.wpi.first.util.sendable.Sendable".equals(interfaceClass.getQualifiedName())) {
                return true;
            }
        }

        PsiClass superClass = psiClass.getSuperClass();
        if (superClass != null) {
            return isImplementsSendable(PsiTypesUtil.getClassType(superClass));
        }

        return false;
    }

    private void checkDuplicateKeys(PsiModifierListOwner element, ProblemsHolder holder) {
        String key = generateKey(element);

        if (keyTracker.containsKey(key)) {
            PsiElement duplicate = keyTracker.get(key);
            String message = String.format("Duplicate key '%s' found in %s and %s",
                    key,
                    getElementName(duplicate),
                    getElementName(element));

            holder.registerProblem(
                    element instanceof PsiMethod ? ((PsiMethod) element).getNameIdentifier() : ((PsiField) element).getNameIdentifier(),
                    message,
                    ProblemHighlightType.WARNING
            );
        } else {
            keyTracker.put(key, element);
        }
    }

    private String generateKey(PsiModifierListOwner element) {
        PsiAnnotation keyAnnotation = element.getAnnotation("badgerlog.annotations.Key");
        if (keyAnnotation != null) {
            PsiAnnotationMemberValue value = keyAnnotation.findAttributeValue("value");
            if (value != null) {
                return value.getText().replace("\"", "");
            }
        }

        String elementName = element instanceof PsiMethod ? ((PsiMethod) element).getName() : ((PsiField) element).getName();
        String className = ((PsiClass) element.getParent().getParent()).getName();
        return elementName + "/" + className;
    }

    private String getElementName(PsiElement element) {
        if (element instanceof PsiMethod method) {
            return method.getContainingClass().getName() + "." + method.getName();
        } else if (element instanceof PsiField field) {
            return field.getContainingClass().getName() + "." + field.getName();
        }
        return "unknown";
    }
}