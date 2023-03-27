package com.httpjserver.processors.core;

import com.httpjserver.processors.annoatations.HttpJ;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import java.util.Set;

@SupportedAnnotationTypes("HttpJ")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class HttpJAnnotationProcessor extends AbstractProcessor {


    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement annotation : annotations) {
            for (Element element : roundEnv.getElementsAnnotatedWith(annotation)) {
                if (element.getKind().equals(ElementKind.METHOD)) {
                    HttpJ httpJAnnotations = annotation.getAnnotation(HttpJ.class);
                    System.err.println(httpJAnnotations.path());
                    System.err.println(httpJAnnotations.method());
                }
            }
        }
        return true;
    }
}
