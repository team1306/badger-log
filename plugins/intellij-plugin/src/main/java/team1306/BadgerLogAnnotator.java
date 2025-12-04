package team1306;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiAnnotationMemberValue;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiJavaCodeReferenceElement;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.ui.JBColor;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class BadgerLogAnnotator implements Annotator {

    private static final TextAttributesKey ENTRY_ANNOTATION = TextAttributesKey.createTextAttributesKey(
            "BADGERLOG_ENTRY",
            DefaultLanguageHighlighterColors.METADATA
    );

    private static final TextAttributesKey KEY_ANNOTATION = TextAttributesKey.createTextAttributesKey(
            "BADGERLOG_KEY",
            DefaultLanguageHighlighterColors.METADATA
    );

    private static final TextAttributesKey ENTRY_TYPE = TextAttributesKey.createTextAttributesKey(
            "BADGERLOG_ENTRY_TYPE",
            DefaultLanguageHighlighterColors.CONSTANT
    );

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof PsiAnnotation annotation)) {
            return;
        }

        String qualifiedName = annotation.getQualifiedName();
        if (qualifiedName == null) {
            return;
        }

        // Highlight @Entry annotation
        if (qualifiedName.equals("badgerlog.annotations.Entry")) {
            highlightEntryAnnotation(annotation, holder);
        }

        // Highlight @Key annotation
        if (qualifiedName.equals("badgerlog.annotations.Key")) {
            highlightKeyAnnotation(annotation, holder);
        }
    }

    private void highlightEntryAnnotation(PsiAnnotation annotation, AnnotationHolder holder) {
        PsiJavaCodeReferenceElement nameRef = annotation.getNameReferenceElement();
        if (nameRef != null) {
            holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .range(nameRef.getTextRange())
                    .textAttributes(ENTRY_ANNOTATION)
                    .create();
        }

        // Highlight the entry type (PUBLISHER, SUBSCRIBER, etc.)
        PsiAnnotationMemberValue value = annotation.findAttributeValue("value");
        if (value instanceof PsiReferenceExpression) {
            TextAttributes attrs = new TextAttributes();
            attrs.setForegroundColor(JBColor.namedColor("BadgerLog.EntryType", new JBColor(new Color(152, 118, 170), new Color(204, 153, 204))));
            attrs.setFontType(Font.BOLD);

            holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .range(value.getTextRange())
                    .enforcedTextAttributes(attrs)
                    .create();
        }
    }

    private void highlightKeyAnnotation(PsiAnnotation annotation, AnnotationHolder holder) {
        PsiJavaCodeReferenceElement nameRef = annotation.getNameReferenceElement();
        if (nameRef != null) {
            TextAttributes attrs = new TextAttributes();
            attrs.setForegroundColor(JBColor.namedColor("BadgerLog.Key", new JBColor(new Color(0, 128, 128), new Color(102, 217, 239))));

            holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .range(nameRef.getTextRange())
                    .enforcedTextAttributes(attrs)
                    .create();
        }

        // Highlight the key value string
        PsiAnnotationMemberValue value = annotation.findAttributeValue("value");
        if (value != null) {
            TextAttributes attrs = new TextAttributes();
            attrs.setForegroundColor(JBColor.namedColor("BadgerLog.KeyValue", new JBColor(new Color(0, 100, 0), new Color(106, 135, 89))));
            attrs.setFontType(Font.ITALIC);

            holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .range(value.getTextRange())
                    .enforcedTextAttributes(attrs)
                    .create();
        }
    }
}
