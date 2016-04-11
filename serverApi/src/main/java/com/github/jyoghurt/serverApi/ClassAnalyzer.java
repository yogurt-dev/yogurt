package com.github.jyoghurt.serverApi;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Doclet;
import com.sun.javadoc.RootDoc;

/**
 * Created by jtwu on 2016/3/30.
 */
public class ClassAnalyzer extends Doclet {
    public static boolean start(RootDoc root) {
        for (ClassDoc classDoc : root.classes()) {
            ServerApiPlugin.classDocMap.put(classDoc.qualifiedName(), classDoc);
        }
        return true;
    }
}
