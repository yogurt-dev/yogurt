package pub.utils;

import com.github.jyoghurt.security.securityUnitT.domain.SecurityUnitT;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

/**
 * @Project: 驴鱼社区-车险帮
 * @Package: com.df.motorInsurance.pub.utils
 * @Description: 加密工具
 * @author: baoxiaobing@lvyushequ.com
 * @date: 2015-09-02 15:00
 */
public class Encrypter {

    //获取日志对象
    public static final Logger logger = LoggerFactory.getLogger(Encrypter.class);

    public static final String MD5(String s) {
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            byte[] strTemp = s.getBytes();
            //使用MD5创建MessageDigest对象
            MessageDigest mdTemp = MessageDigest.getInstance("MD5");
            mdTemp.update(strTemp);
            byte[] md = mdTemp.digest();
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte b = md[i];
                //System.out.println((int)b);
                //将没个数(int)b进行双字节加密
                str[k++] = hexDigits[b >> 4 & 0xf];
                str[k++] = hexDigits[b & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 递归容器，查询出当前组织机构的上级公司
     *
     * @param securityUnitTList
     * @param securityUnitT
     */
    public static final SecurityUnitT getFathCompanyInfo(List<SecurityUnitT> securityUnitTList, SecurityUnitT securityUnitT) {



        SecurityUnitT fatherComanyInfo = new SecurityUnitT();

        if (CollectionUtils.isEmpty(securityUnitTList) || null == securityUnitT) {
            logger.error("入参错误");
            return null;
        }

        if(SysVarEnum.COMPANY_COMPTYPE.getCode().equals(securityUnitT.getCompType())){
            return securityUnitT;
        }

        for (SecurityUnitT unitT : securityUnitTList) {
            if (securityUnitT.getParentId().equals(unitT.getUnitId())) {
                //如果当前组织机构是公司
                if (SysVarEnum.COMPANY_COMPTYPE.getCode().equals(unitT.getCompType())) {
                    return unitT;
                }
                fatherComanyInfo = getFathCompanyInfo(securityUnitTList, unitT);
            }
        }

        return fatherComanyInfo;
    }

    public static List<Class> getAllClassByInterface(Class c) {
        List<Class>  returnClassList = null;

        if(c.isInterface()) {
            // 获取当前的包名
//            String packageName = c.getPackage().getName();
            String packageName = "com.df";
            try {
                // 获取当前包下以及子包下所以的类
                List<Class> allClass = getClasses(packageName);
                if(allClass != null) {
                    returnClassList = new ArrayList<Class>();
                    for(Class classes : allClass) {
                        // 判断是否是同一个接口
                        if(c.isAssignableFrom(classes)) {
                            // 本身不加入进去
                            if(!c.equals(classes)) {
                                returnClassList.add(classes);
                            }
                        }
                    }
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        return returnClassList;
    }


    public static List<Class> getClasses(String pckgname)
            throws ClassNotFoundException {
        ArrayList<Class> classes = new ArrayList<Class>();
        // Get a File object for the package
        File directory = null;
        try {
            ClassLoader cld = Thread.currentThread().getContextClassLoader();
            if (cld == null)
                throw new ClassNotFoundException("Can't get class loader.");
            String path = '/' + pckgname.replace('.', '/');
            URL resource = cld.getResource(path);
            if (resource == null)
                throw new ClassNotFoundException("No resource for " + path);
            directory = new File(resource.getFile());
        } catch (NullPointerException x) {
            throw new ClassNotFoundException(pckgname + " (" + directory
                    + ") does not appear to be a valid package a");
        }
        if (directory.exists()) {
            // Get the list of the files contained in the package
            String[] files = directory.list();
            for (int i = 0; i < files.length; i++) {
                // we are only interested in .class files
                if (files[i].endsWith(".class")) {
                    // removes the .class extension
                    classes.add(Class.forName(pckgname + '.'
                            + files[i].substring(0, files[i].length() - 6)));
                }
            }
        } else
            throw new ClassNotFoundException(pckgname
                    + " does not appear to be a valid package b");
        Class[] classesA = new Class[classes.size()];
        classes.toArray(classesA);
        return classes;
    }

//    /**
//     * 取得当前类路径下的所有类
//     *
//     * @param cls
//     * @return
//     * @throws IOException
//     * @throws ClassNotFoundException
//     */
//    public static List<Class<?>> getClasses(Class<?> cls) throws IOException,
//            ClassNotFoundException {
//        String pk = cls.getPackage().getName();
//        String path = pk.replace('.', '/');
//        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
//        URL url = classloader.getResource(path);
//        return getClasses(new File(url.getFile()), pk);
//    }

//    /**
//     * 获取同一路径下所有子类或接口实现类
//     *
//     * @param intf
//     * @return
//     * @throws IOException
//     * @throws ClassNotFoundException
//     */
//    public static List<Class<?>> getAllAssignedClass(Class<?> cls) throws IOException,
//            ClassNotFoundException {
//        List<Class<?>> classes = new ArrayList<Class<?>>();
//        for (Class<?> c : getClasses(cls)) {
//            if (cls.isAssignableFrom(c) && !cls.equals(c)) {
//                classes.add(c);
//            }
//        }
//        return classes;
//    }



    public static void main(String args[]) {
        System.out.println(MD5("1"));
    }
}
