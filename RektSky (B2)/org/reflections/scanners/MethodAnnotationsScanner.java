package org.reflections.scanners;

import org.reflections.*;
import java.util.*;

public class MethodAnnotationsScanner extends AbstractScanner
{
    @Override
    public void scan(final Object cls, final Store store) {
        for (final Object method : this.getMetadataAdapter().getMethods(cls)) {
            for (final String methodAnnotation : this.getMetadataAdapter().getMethodAnnotationNames(method)) {
                if (this.acceptResult(methodAnnotation)) {
                    this.put(store, methodAnnotation, this.getMetadataAdapter().getMethodFullKey(cls, method));
                }
            }
        }
    }
}
