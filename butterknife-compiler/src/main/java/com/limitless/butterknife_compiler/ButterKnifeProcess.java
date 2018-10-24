package com.limitless.butterknife_compiler;

import com.google.auto.service.AutoService;
import com.limitless.butterknife_annotations.BindView;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.JavaFileObject;

/**
 * 指定编译时的入口
 */
@AutoService(Processor.class)
public class ButterKnifeProcess extends AbstractProcessor {

    @Override
    public Set<String> getSupportedAnnotationTypes() {

        //指定处理的注解
        Set<String> types = new LinkedHashSet<>();
        types.add(BindView.class.getCanonicalName());
        return types;
    }


    @Override
    public SourceVersion getSupportedSourceVersion() {
        //指定JDK的版本
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {


        System.out.println("------------------------执行了该方法----------------");

        //拿到所有使用BindView的注解
        Set<? extends Element> elementsAnnotatedWith = roundEnvironment.getElementsAnnotatedWith(BindView.class);

        //把所有的注解进行分解处理          String 全类名          List 该类中的所有注解
        Map<String, List<VariableElement>> cacheMap = new HashMap<>();

        for (Element element : elementsAnnotatedWith) {

            VariableElement variableElement = (VariableElement) element;

            //获取类结点
            TypeElement typeElement = (TypeElement) variableElement.getEnclosingElement();

            //通过processingEnv提供的方法获取类节点所在的包
            String packageName = processingEnv.getElementUtils().getPackageOf(typeElement).getQualifiedName().toString();

            //获取类名
            String allClassName = packageName + "." + typeElement.getSimpleName().toString();

            List<VariableElement> variableElements = cacheMap.get(allClassName);

            if (variableElements == null) {
                variableElements = new ArrayList<>();
                cacheMap.put(allClassName, variableElements);
            }
            variableElements.add(variableElement);
        }


        for (String activityName : cacheMap.keySet()) {

            List<VariableElement> variableElements = cacheMap.get(activityName);

            String newActivityName = activityName + "_ViewBinder";

            Filer filer = processingEnv.getFiler();

            try {
                //创建文件
                JavaFileObject javaFileObject = filer.createSourceFile(newActivityName);

                Writer writer = javaFileObject.openWriter();

                //获取类结点
                TypeElement typeElement = (TypeElement) variableElements.get(0).getEnclosingElement();

                //通过processingEnv提供的方法获取类节点所在的包
                String packageName = processingEnv.getElementUtils().getPackageOf(typeElement).getQualifiedName().toString();

                String activitySimpleName = typeElement.getSimpleName().toString()+ "_ViewBinder";

                //写入头相关的部分
                writer.write("package " + packageName + ";");
                writer.write("\n");
                writer.write("import com.limitless.butter_knife.ViewBinder;");
                writer.write("\n");
                writer.write("public class " + activitySimpleName + " implements  ViewBinder<" + activityName + "> {");
                writer.write("\n");
                writer.write(" public void bind( " + activityName + " target) {");
                writer.write("\n");

                //写入中间部分
                for (VariableElement variableElement : variableElements) {
                    BindView bindView = variableElement.getAnnotation(BindView.class);
                    int id = bindView.value();
                    String fieldName = variableElement.getSimpleName().toString();
                    TypeMirror typeMirror = variableElement.asType();
                    writer.write("target." + fieldName + "=(" + typeMirror.toString() + ")target.findViewById(" + id + ");");
                    writer.write("\n");
                }

                //写入结尾部分
                writer.write("\n");
                writer.write("}");
                writer.write("\n");
                writer.write("}");
                writer.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
