package com.watayouxiang.apt_annotation_compiler;

import com.google.auto.service.AutoService;
import com.watayouxiang.apt_annotation.BindView;
import com.watayouxiang.apt_annotation.IBinder;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

/**
 * 注解处理器（APT）简单使用
 * <p>
 * APT: Annotation Processing Tool
 */
@AutoService(Processor.class)
public class AnnotationsCompiler extends AbstractProcessor {

    // 定义一个只能用来生成 APT 目录下面的文件的对象
    private Filer filer;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        filer = processingEnv.getFiler();
    }

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
        types.add(BindView.class.getCanonicalName());
        return types;
    }

    /**
     * 目的是在 application 工程的 `/build/generated/ap_generated_sources/debug/out/...` 路径下生成如下代码：
     *
     * <blockquote><pre>
     * package com.example.dn_butterknife;
     * import com.example.dn_butterknife.IBinder;
     * public class MainActivity_ViewBinding implements IBinder<com.example.dn_butterknife.MainActivity> {
     *     @Override
     *     public void bind(com.example.dn_butterknife.MainActivity target) {
     *         target.textView = (android.widget.TextView) target.findViewById(2131165359);
     *     }
     * }
     * </pre></blockquote>
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        // 1.process是怎么回调的？     SPI机制
        // 2.调用的次数是怎么决定的？    和是否有生成文件有关系
        // 3.返回值有什么用？          注解是否往下传递，true表示不传递set
        if (annotations.isEmpty()) {
            return false;
        }

        // 日志打印
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "wataTAG: " + annotations);

        // 1、获取APP中所有用到了BindView注解的对象
        Set<? extends Element> elementsAnnotatedWith = roundEnv.getElementsAnnotatedWith(BindView.class);

        // 2、开始对elementsAnnotatedWith进行分类
        //
        // Element 的子类有如下：
        //
        // TypeElement //类
        // ExecutableElement //方法
        // VariableElement //属性
        //
        HashMap<String, List<VariableElement>> map = new HashMap<>();
        for (Element element : elementsAnnotatedWith) {
            // @BindView 是属性类型，所以直接强转
            VariableElement variableElement = (VariableElement) element;
            // 获取 @BindView 所在的作用域，也就是Activity类，拿到Activity的名称
            String activityName = variableElement.getEnclosingElement().getSimpleName().toString();
            // 获取 @BindView 所在的作用域，也就是Activity类，拿到Activity的字节码对象
            Class<? extends Element> activityClass = variableElement.getEnclosingElement().getClass();
            // [
            //   "MainActivity" : {VariableElement1, VariableElement2, ...}
            //   "TwoActivity" : {VariableElement1, VariableElement2}
            //   "ThreeActivity" : {VariableElement1}
            // ]
            List<VariableElement> variableElements = map.get(activityName);
            if (variableElements == null) {
                variableElements = new ArrayList<>();
                map.put(activityName, variableElements);
            }
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
        if (map.size() > 0) {
            Writer writer = null;
            for (String activityName : map.keySet()) {
                // 拿到某个 Activity 中的所有注解
                List<VariableElement> variableElements = map.get(activityName);
                // 拿到 Activity 包名
                Element enclosingElement = variableElements.get(0).getEnclosingElement();
                String packageName = processingEnv.getElementUtils().getPackageOf(enclosingElement).toString();
                try {
                    // 开始生成 MainActivity_ViewBinding.java 文件
                    // 创建名为 com.watayouxiang.aptdemo.MainActivity 的.java文件
                    JavaFileObject sourceFile = filer.createSourceFile(packageName + "." + activityName + "_ViewBinding");

                    writer = sourceFile.openWriter();
                    // package com.watayouxiang.aptdemo;
                    writer.write("package " + packageName + ";\n");
                    // import com.watayouxiang.apt_annotation.IBinder;
                    writer.write("import " + IBinder.class.getPackage().getName() + ".IBinder;\n\n");
                    // public class MainActivity_ViewBinding implements IBinder<com.watayouxiang.aptdemo.MainActivity>{
                    writer.write("public class " + activityName + "_ViewBinding implements IBinder<" + packageName + "." + activityName + ">{\n");
                    // @Override
                    // public void bind(com.watayouxiang.aptdemo.MainActivity target) {
                    writer.write("\t@Override\n");
                    writer.write("\tpublic void bind(" + packageName + "." + activityName + " target){\n");
                    // target.tvText=(android.widget.TextView)target.findViewById(2131165325);
                    for (VariableElement variableElement : variableElements) {
                        // 得到名字
                        String variableName = variableElement.getSimpleName().toString();
                        // 得到 ID
                        int id = variableElement.getAnnotation(BindView.class).value();
                        // 得到类型
                        TypeMirror typeMirror = variableElement.asType();
                        writer.write("\t\ttarget." + variableName + "=(" + typeMirror + ")target.findViewById(" + id + ");\n");
                    }
                    // }}
                    writer.write("\t}\n");
                    writer.write("}");
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (writer != null) {
                        try {
                            writer.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        return false;
    }
}