package com.github.jyoghurt.serverApi;

/**
 * Created by jtwu on 2016/4/1.
 */

import edu.emory.mathcs.backport.java.util.Arrays;
import org.apache.commons.lang3.ArrayUtils;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Stack;

public final class ExtClasspathLoader {

    private static Method addURL = initAddMethod();

    private static URLClassLoader classloader = (URLClassLoader) ClassLoader.getSystemClassLoader();

    /**
     * 初始化addUrl 方法.
     *
     * @return 可访问addUrl方法的Method对象
     */
    private static Method initAddMethod() {
        try {
            Method add = URLClassLoader.class.getDeclaredMethod("addURL", new Class[]{URL.class});
            add.setAccessible(true);
            return add;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 加载jar classpath。
     */
    public static void loadJarPath(List<String> jarFiles) {
        for (String f : jarFiles) {
            loadJarPath(f);
        }
    }

    private static void loadJarPath(String filepath) {
        File file = new File(filepath);
        loopFiles(file);
    }

    private static void loadResourceDir(String filepath) {
        File file = new File(filepath);
        loopDirs(file);
    }

    public static void loadClassPath(String classPath) throws Exception {
        Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
        method.setAccessible(true);
        // 设置类加载器
        URLClassLoader classLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        // 将当前类路径加入到类加载器中
        method.invoke(classLoader, new File(classPath).toURI().toURL());

    }

    /** */
    /**
     * 循环遍历目录，找出所有的资源路径。
     *
     * @param file 当前遍历文件
     */
    private static void loopDirs(File file) {
        // 资源文件只加载路径
        if (file.isDirectory()) {
            addURL(file);
            File[] tmps = file.listFiles();
            for (File tmp : tmps) {
                loopDirs(tmp);
            }
        }
    }

    /**
     * 循环遍历目录，找出所有的jar包。
     *
     * @param file 当前遍历文件
     */
    private static void loopFiles(File file) {
        if (file.isDirectory()) {
            File[] tmps = file.listFiles();
            for (File tmp : tmps) {
                loopFiles(tmp);
            }
        } else {
            if (file.getAbsolutePath().endsWith(".jar") || file.getAbsolutePath().endsWith(".zip")) {
                addURL(file);
                return;
            }
        }
    }

    /**
     * 通过filepath加载文件到classpath。
     *
     * @return URL
     * @throws Exception 异常
     */
    private static void addURL(File file) {
        try {
            addURL.invoke(classloader, new Object[]{file.toURI().toURL()});
        } catch (Exception e) {
        }
    }
}