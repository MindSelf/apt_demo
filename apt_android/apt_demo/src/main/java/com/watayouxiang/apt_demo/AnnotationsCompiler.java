package com.watayouxiang.apt_demo;

import com.google.auto.service.AutoService;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

/**
 * 注解处理器（APT）简单使用
 */
@AutoService(Processor.class)
public class AnnotationsCompiler extends AbstractProcessor {

    // 定义一个只能用来生成 APT 目录下面的文件的对象
    private Filer filer;

    // 支持的版本
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return super.getSupportedSourceVersion();
    }

    // 能用来处理哪些注解
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        // 只能处理 @Deprecated 类型的注解
        Set<String> types = new HashSet<>();
        types.add(Deprecated.class.getCanonicalName());
        return types;
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        filer = processingEnv.getFiler();
    }

    // 处理逻辑，生成所需的代码
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        // 日志打印
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "test ----------- " + annotations);

        return false;
    }
}