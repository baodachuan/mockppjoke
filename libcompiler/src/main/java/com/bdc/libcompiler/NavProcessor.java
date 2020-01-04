package com.bdc.libcompiler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bdc.libannotation.ActivityDestination;
import com.bdc.libannotation.FragmentDestination;
import com.google.auto.service.AutoService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.util.HashMap;
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
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({"com.bdc.libannotation.FragmentDestination", "com.bdc.libannotation.ActivityDestination"})
public class NavProcessor extends AbstractProcessor {
    private Messager messager;
    private Filer filer;
    private static final String OUTPUT_FILE_NAME = "destnation.json";

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
        filer = processingEnv.getFiler();


    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Set<? extends Element> fragmentElements = roundEnvironment.getElementsAnnotatedWith(FragmentDestination.class);
        Set<? extends Element> activityElements = roundEnvironment.getElementsAnnotatedWith(ActivityDestination.class);

        if(!fragmentElements.isEmpty() || !activityElements.isEmpty()){
            Map<String, JSONObject > destMap = new HashMap();
            handleElements(fragmentElements,FragmentDestination.class,destMap);
            handleElements(activityElements, ActivityDestination.class,destMap);
            writeToJsonFile(destMap);
        }
        return true;
    }

    private void writeToJsonFile(Map<String, JSONObject> destMap){
        FileOutputStream fos=null;
        OutputStreamWriter writer=null;
        try {
            FileObject resource = filer.createResource(StandardLocation.CLASS_OUTPUT, "", OUTPUT_FILE_NAME);
            String path = resource.toUri().getPath();
            messager.printMessage(Diagnostic.Kind.NOTE,"resourcePath is "+path);
            String appPath=path.substring(0,path.indexOf("app")+4);
            String assetPath=appPath+"src/main/assets";

            File file=new File(assetPath);
            if(!file.exists()){
                file.mkdirs();
            }

            File outputFile=new File(file,OUTPUT_FILE_NAME);
            if(outputFile.exists()){
                outputFile.delete();
            }
            outputFile.createNewFile();
            String content= JSON.toJSONString(destMap);
            fos=new FileOutputStream(outputFile);
            writer=new OutputStreamWriter(fos,"UTF-8");
            writer.write(content);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(fos!=null){
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void handleElements(Set<? extends Element> elements, Class<? extends Annotation> annotationClass, Map<String,JSONObject> destMap) {
        for(Element element:elements){
            TypeElement typeElement= (TypeElement) element;
            String pageUrl=null;
            String clazzName=typeElement.getQualifiedName().toString();

            int id=Math.abs(clazzName.hashCode());
            boolean needLogin=false;
            boolean asStarter=false;
            boolean isFragment=false;

            Annotation annotation=typeElement.getAnnotation(annotationClass);
            if(annotation instanceof FragmentDestination){
                FragmentDestination dest= (FragmentDestination) annotation;
                pageUrl=dest.pageUrl();
                needLogin=dest.needLogin();
                asStarter=dest.asStarter();
                isFragment=true;

            }else if(annotation instanceof ActivityDestination){
                ActivityDestination dest= (ActivityDestination) annotation;
                pageUrl=dest.pageUrl();
                needLogin=dest.needLogin();
                asStarter=dest.asStarter();
                isFragment=false;
            }

            if(destMap.containsKey(pageUrl)){
                messager.printMessage(Diagnostic.Kind.ERROR, "不同的页面不允许使用相同的pageUrl：" + clazzName);
            }else {
                JSONObject object=new JSONObject();
                object.put("id",id);
                object.put("pageUrl",pageUrl);
                object.put("needLogin",needLogin);
                object.put("asStarter",asStarter);
                object.put("className",clazzName);
                object.put("isFragment",isFragment);
                destMap.put(pageUrl,object);
            }
        }
    }
}
