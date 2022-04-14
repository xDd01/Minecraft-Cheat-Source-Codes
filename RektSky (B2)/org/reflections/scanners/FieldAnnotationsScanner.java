package org.reflections.scanners;

import org.reflections.*;
import java.util.*;

public class FieldAnnotationsScanner extends AbstractScanner
{
    @Override
    public void scan(final Object cls, final Store store) {
        final String className = this.getMetadataAdapter().getClassName(cls);
        final List<Object> fields = this.getMetadataAdapter().getFields(cls);
        for (final Object field : fields) {
            final List<String> fieldAnnotations = this.getMetadataAdapter().getFieldAnnotationNames(field);
            for (final String fieldAnnotation : fieldAnnotations) {
                if (this.acceptResult(fieldAnnotation)) {
                    final String fieldName = this.getMetadataAdapter().getFieldName(field);
                    this.put(store, fieldAnnotation, String.format("%s.%s", className, fieldName));
                }
            }
        }
    }
}
