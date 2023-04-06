package com.httpjserver.processors.core;

import com.httpjserver.processors.annoatations.HttpJ;
import com.httpjserver.processors.annoatations.HttpJController;
import com.squareup.javapoet.*;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class HttpJAnnotationProcessor extends AbstractProcessor {

    private static final AtomicReference<Map<String, String>> controllerMap = new AtomicReference<>(new HashMap<>());

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return new HashSet<>(Arrays.asList(HttpJ.class.getName(), HttpJController.class.getName()));
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            if (annotations.isEmpty()) return false;

            for (Element classElement : roundEnv.getElementsAnnotatedWith(HttpJController.class)) {
                HttpJController controllerAnnotation = classElement.getAnnotation(HttpJController.class);
                var clasName = ((TypeElement) classElement).getQualifiedName();
                for (Element methodElements : classElement.getEnclosedElements()) {
                    if (methodElements.getKind().equals(ElementKind.METHOD)) {
                        ExecutableElement method = (ExecutableElement) methodElements;
                        HttpJ httpMethodAnnotation = method.getAnnotation(HttpJ.class);
                        var methodFQN = clasName + "." + methodElements.getSimpleName();
                        registerControllers(controllerAnnotation, httpMethodAnnotation, methodFQN);
                    }
                }
            }
            createImmutableMap();
            return true;
        } catch (Exception e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
            return false;
        }
    }

    private void registerControllers(HttpJController controllerAnnotation, HttpJ httpMethodAnnotation, String methodFQN) {
        Map<String, String> newMap = new HashMap<>();
        String controllerKey = httpMethodAnnotation.method() + " " +
                controllerAnnotation.resourcePath() + httpMethodAnnotation.path();

        newMap.put(controllerKey, methodFQN);
        Map<String, String> oldMap = controllerMap.get();
        newMap.putAll(oldMap);
        controllerMap.compareAndSet(oldMap, newMap);
    }

    private void createImmutableMap() throws IOException {

        final var PKG_NAME = "com.httpjserver.processors.core";

        // First, create the map type:
        ParameterizedTypeName mapType = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ClassName.get(String.class));

        // Then, create the constant field with the map:
        CodeBlock.Builder codeBlockBuilder = CodeBlock.builder()
                .add("$T.unmodifiableMap(new $T<$T, $T>() {{\n", Collections.class, HashMap.class, String.class, String.class);


        for (var entry : controllerMap.get().entrySet()) {
            codeBlockBuilder.add("put($S,$S);\n", entry.getKey(), entry.getValue());
        }

        codeBlockBuilder.add("}})");

        FieldSpec constantField = FieldSpec.builder(mapType, "CONTROLLER_MAP")
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                .initializer(codeBlockBuilder.build()).build();

        MethodSpec constructorSpec = MethodSpec.constructorBuilder().addModifiers(Modifier.PRIVATE).build();

        MethodSpec methodSpec = MethodSpec.methodBuilder("getControllerMapper")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(ParameterizedTypeName.get(
                        ClassName.get(Map.class),
                        ClassName.get(String.class),
                        ClassName.get(String.class)))
                .addStatement("return CONTROLLER_MAP")
                .build();

        TypeSpec myClass = TypeSpec.classBuilder("ControllerRegistry")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addField(constantField)
                .addMethod(constructorSpec)
                .addMethod(methodSpec)
                .build();

        JavaFile javaFile = JavaFile.builder(PKG_NAME, myClass).build();

        Filer filer = processingEnv.getFiler();
        javaFile.writeTo(filer);
    }
}
