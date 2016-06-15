package com.github.jyoghurt.serverApi;

import com.sun.javadoc.ClassDoc;

import com.sun.tools.javadoc.Main;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.compiler.CompilerMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import java.io.*;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@Mojo(name = "createApi", defaultPhase = LifecyclePhase.PACKAGE, threadSafe = true, requiresDependencyResolution = ResolutionScope.COMPILE)
public class ServerApiPlugin extends CompilerMojo {
    static Map<String, ClassDoc> classDocMap = new HashMap<>();
    private Set<String> packages = new HashSet<>();
    private List<String> javaFiles = new ArrayList<>();

    @Parameter(defaultValue = "${basedir}")
    private String sourceDir;

    @Parameter(defaultValue = "${project.compileClasspathElements}", readonly = true, required = true)
    protected static List<String> classpathElements;

    public void init() {
        getLog().info(classpathElements.toString());
        //获取所有java文件
        findJavaFiles(getSourceDir());
        //创建classDoc
        String options[] = ArrayUtils.addAll(new String[]{"-cp", StringUtils.join(getClasspathElements(),
                ";"), "-sourcepath", getSourceDir()}, packages.toArray(new String[]{}));
        Main.execute("", ClassAnalyzer.class.getName(), options);

//        classDocMap.keySet().forEach(System.out::println);
    }

    public void init(String sourceDir) {
        this.sourceDir = sourceDir;
        init();
    }

    @Override
    public void execute() throws MojoExecutionException {
        init(sourceDir + File.separator + "src" + File.separator + "main" + File.separator + "java" + File.separator);
        try {
            Map<String, ClassDoc> map = new HashMap<>();
            map.putAll(classDocMap);
            new ExcelBuild().buildExcel(map,getLog());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void findJavaFiles(String sourceDir) {
        if (StringUtils.isEmpty(sourceDir) || sourceDir.endsWith("META-INF")) {
            return;
        }
        if (!StringUtils.equals(sourceDir, getSourceDir())) {
            packages.add(sourceDir.replace(getSourceDir(), "").replace("\\", "."));
        }

        if (sourceDir.endsWith(".jar")) {
            try {
                JarFile file = new JarFile(sourceDir);
                Enumeration<JarEntry> jarEntrys = file.entries();
                while (jarEntrys.hasMoreElements()) {
                    javaFiles.add(StringUtils.substringBeforeLast(jarEntrys.nextElement().toString(), "/"));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        File file = new File(sourceDir);
        if(!file.exists()){
            getLog().error("file is null ,the path is " + sourceDir);
            return;
        }
        File[] tempList = file.listFiles(new JavaFileFilter());
        for (File javaFile : tempList) {
            if (javaFile.isFile()) {
                javaFiles.add(javaFile.getAbsolutePath());
                continue;
            }
            if (javaFile.isDirectory()) {
                findJavaFiles(javaFile.getAbsolutePath());
            }
        }
    }


    @Override
    public List<String> getClasspathElements() {
        return classpathElements;
    }

    public void setClasspathElements(List<String> classpathElements) {
        this.classpathElements = classpathElements;
    }

    public void setJavaFiles(List<String> javaFiles) {
        this.javaFiles = javaFiles;
    }

    public Set<String> getPackages() {
        return packages;
    }

    public Map<String, ClassDoc> getClassDocMap() {
        return classDocMap;
    }

    public String getSourceDir() {
        return sourceDir;
    }

    public void setSourceDir(String sourceDir) {
        this.sourceDir = sourceDir;
    }

    public List<String> getJavaFiles() {
        return javaFiles;
    }
}

class JavaFileFilter implements FileFilter {

    @Override
    public boolean accept(File pathname) {
        if (pathname.isDirectory()) {
            return true;
        }
        String name = pathname.getName();
        return name.endsWith(".java");
    }
}


