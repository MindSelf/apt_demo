package com.watayouxiang.apt_annotation_compiler;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.watayouxiang.apt_annotation.BindView;
import com.watayouxiang.apt_annotation.IBinder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

/**
 * 注解处理器（APT）简单使用
 * <p>
 * APT: Annotation Processing Tool
 */
@AutoService(Processor.class)
@SupportedAnnotationTypes("com.watayouxiang.apt_annotation.BindView")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class AnnotationsCompiler2 extends AbstractProcessor {

    // 定义一个只能用来生成 APT 目录下面的文件的对象
    private Filer filer;
    //日志打印
    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
        filer = processingEnv.getFiler();
        messager.printMessage(Diagnostic.Kind.NOTE, "AnnotationsCompiler2 init");
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        messager.printMessage(Diagnostic.Kind.NOTE, "AnnotationsCompiler2 process: annotations = " + annotations + ", env = " + roundEnv);

        if (annotations.isEmpty()) {
            return false;
        }

        // 获取APP中所有用到了BindView注解的对象
        Set<? extends Element> elementsAnnotatedWith = roundEnv.getElementsAnnotatedWith(BindView.class);

        /*
            开始对elementsAnnotatedWith进行分类

            Element 的子类有如下：
            - TypeElement //类
            - ExecutableElement //方法
            - VariableElement //属性
         */
        HashMap<String, List<VariableElement>> map = new HashMap<>();
        for (Element element : elementsAnnotatedWith) {
            // @BindView 是属性类型，所以直接强转
            VariableElement variableElement = (VariableElement) element;
            // 获取 @BindView 所在的作用域，也就是Activity类，拿到Activity的名称
            String activityName = variableElement.getEnclosingElement().getSimpleName().toString();
            // [
            //   "MainActivity" : {VariableElement1, VariableElement2, ...}
            //   "TwoActivity" : {VariableElement1, VariableElement2}
            //   "ThreeActivity" : {VariableElement1}
            // ]
            List<VariableElement> variableElements = map.computeIfAbsent(activityName, k -> new ArrayList<>());
            variableElements.add(variableElement);
        }

        // 3、开始生成文件
        //
        // package com.watayouxiang.aptdemo;
        // import com.watayouxiang.apt_annotation.IBinder;
        //
        // public class MainActivity_ViewBinding implements IBinder<MainActivity> {
        //     @Override
        //     public void bind(com.watayouxiang.aptdemo.MainActivity target) {
        //         target.textView = (android.widget.TextView) target.findViewById(2131231118);
        //     }
        // }
        //
        for (Map.Entry<String, List<VariableElement>> entry : map.entrySet()) {
            // 拿到 Activity 包名
            Element enclosingElement = entry.getValue().get(0).getEnclosingElement();
            String packageName = processingEnv.getElementUtils().getPackageOf(enclosingElement).toString();
            TypeName activityClass = ClassName.get(packageName, entry.getKey());
            // 生成代码
            MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("bind")
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC)
                    .returns(TypeName.VOID)
                    .addParameter(activityClass, "target");
            for (VariableElement element : entry.getValue()) {
                String variableName = element.getSimpleName().toString();
                int id = element.getAnnotation(BindView.class).value();
                TypeMirror typeMirror = element.asType();
                methodBuilder.addStatement("target.$L = ($L)target.findViewById($L)", variableName, typeMirror.toString(), id);
            }

            TypeSpec typeSpec = TypeSpec.classBuilder(entry.getKey() + "_ViewBinding")
                    .addSuperinterface(ParameterizedTypeName.get(ClassName.get(IBinder.class), activityClass))
                    .addMethod(methodBuilder.build())
                    .build();
            JavaFile javaFile = JavaFile.builder(packageName, typeSpec).build();
            try {
                javaFile.writeTo(filer);
            } catch (IOException e) {
                messager.printMessage(Diagnostic.Kind.ERROR, e.getMessage());
            }
        }

        return false;
    }
}