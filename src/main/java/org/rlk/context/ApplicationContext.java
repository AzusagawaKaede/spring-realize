package org.rlk.context;

import org.apache.commons.lang3.StringUtils;
import org.rlk.annotation.Component;
import org.rlk.annotation.ComponentScan;
import org.rlk.annotation.Lazy;
import org.rlk.annotation.Scope;
import org.rlk.config.BeanDefinition;
import org.rlk.content.ScopeContent;

import java.io.File;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: rlk
 * @date: 2022/8/26
 * Description: Spring容器
 */
public class ApplicationContext {

    private ConcurrentHashMap<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

    //单例池
    private ConcurrentHashMap<String, Object> beanMap = new ConcurrentHashMap<>();

    /**
     * 构造函数：加载配置类，创建容器，扫描Bean并放入容器
     *
     * @param clazz
     */
    public ApplicationContext(Class clazz) {
        //读取配置类，获取扫描的包名 -- TODO 暂时指允许传入一个basePackage，后续优化为String[]允许传多个basePackage
        String basePackage = getBasePackage(clazz);
        //非空继续下一步
        if (!StringUtils.isEmpty(basePackage)) {
            //获取basePackage下面所有的类的字节码文件
            List<Class> classes = getClasses(basePackage);
            //遍历创建BeanDefinition
            createBeanDefinition(classes);
            //创建Bean并保存
            createBean();
        }

    }


    /**
     * 根据beanName获取Bean
     *
     * @param beanName
     * @return
     */
    public Object getBean(String beanName) {
        //判断单例池是否存在
        if (beanMap.containsKey(beanName)) {
            return beanMap.get(beanName);
        }
        //单例池不存在，再判断beanDefinitionMap是否有对应的BeanDefinition
        if (beanDefinitionMap.containsKey(beanName)) {
            BeanDefinition beanDefinition = (BeanDefinition) beanDefinitionMap.get(beanName);
            //获取构造器暴力反射创建对象
            Class clazz = beanDefinition.getClazz();
            try {
                Constructor constructor = clazz.getDeclaredConstructor();
                constructor.setAccessible(true);
                Object bean =constructor.newInstance();
                if(ScopeContent.SINGLETON_CONTENT.equalsIgnoreCase(beanDefinition.getScope())){
                    beanMap.put(beanName, bean);
                }
                return bean;
            } catch (NoSuchMethodException e) {
                throw new NullPointerException("No args constructor is not exist");
            } catch (Exception e){
                return null;
            }
        }
        return null;
    }


    /**
     * 获取配置类上的@ComponentScan注解返回要扫描的包名
     *
     * @param clazz
     * @return
     */
    private String getBasePackage(Class clazz) {
        String basePackage = null;
        //尝试获取@ComponentScan注解
        if (clazz.isAnnotationPresent(ComponentScan.class)) {
            //获取注解
            ComponentScan componentScan = (ComponentScan) clazz.getAnnotation(ComponentScan.class);
            //获取要扫描的包名
            basePackage = componentScan.value();
        }
        //返回包名
        return basePackage;
    }

    /**
     * 加载扫描包下所有文件为class对象
     *
     * @param basePackage
     * @return
     */
    private List<Class> getClasses(String basePackage) {
        ClassLoader classLoader = ApplicationContext.class.getClassLoader();
        //TODO -- 这里不能使用 File.separator，显示的是 %5
        URL url = classLoader.getResource(basePackage.replace(".", "/"));
        //获取包在磁盘上的全路径
        String basePackagePath = url.getFile();
        //获取包对应的File对象
        File basePackageFile = new File(basePackagePath);
        //遍历包下所有的子文件（夹），即所有的.class。放入集合返回
        List<Class> classes = new ArrayList<Class>();
        File[] files = basePackageFile.listFiles();
        for (File file : files) {
            if (file.isFile()) {
                //如果是文件则加载
                String path = basePackage + "." + file.getName();
                path = path.substring(0, path.lastIndexOf("."));
                try {
                    Class<?> loadClass = classLoader.loadClass(path);
                    classes.add(loadClass);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e.getMessage());
                }
            }
            //TODO -- 不是文件说明是文件夹，应该逐层遍历
        }
        return classes;
    }

    /**
     * 遍历classes创建BeanDefinition
     *
     * @param classes
     */
    private void createBeanDefinition(List<Class> classes) {
        classes.stream().forEach(loadClass -> {
            //判断类对象是否包含注解@Component
            if (loadClass.isAnnotationPresent(Component.class)) {
                //包含注解，保存一个BeanDefinition
                BeanDefinition beanDefinition = new BeanDefinition();
                Component component = (Component) loadClass.getAnnotation(Component.class);
                String beanName = component.value();
                //如果Component注解中没有写value，默认beanName为类名首字符小写
                if (StringUtils.isEmpty(beanName)) {
                    beanName = firstCharToLowCase(loadClass.getSimpleName());
                }
                //设置beanName
                beanDefinition.setBeanName(beanName);
                //设置字节码
                beanDefinition.setClazz(loadClass);
                //设置懒加载
                if (loadClass.isAnnotationPresent(Lazy.class)) {
                    beanDefinition.setLazy(true);
                } else {
                    beanDefinition.setLazy(false);
                }
                //设置Scope
                if (loadClass.isAnnotationPresent(Scope.class)) {
                    //有Scope注解
                    Scope scope = (Scope) loadClass.getAnnotation(Scope.class);
                    if (ScopeContent.PROTOTYPE_CONTENT.equalsIgnoreCase(scope.value())) {
                        beanDefinition.setScope(ScopeContent.PROTOTYPE_CONTENT);
                    }
                } else {
                    beanDefinition.setScope(ScopeContent.SINGLETON_CONTENT);
                }
                //保存BeanDefinition
                beanDefinitionMap.put(beanName, beanDefinition);
            }
        });
    }

    /**
     * 根据BeanDefinition创建非懒加载单例Bean
     *
     * @return
     */
    private void createBean() {
        //遍历beanDefinitionMap创建非懒加载单例Bean
        beanDefinitionMap.entrySet().stream().forEach(entry -> {
            BeanDefinition beanDefinition = (BeanDefinition) entry.getValue();
            if (!beanDefinition.getLazy() &&
                    ScopeContent.SINGLETON_CONTENT.equalsIgnoreCase(beanDefinition.getScope())) {
                //创建Bean放到容器中
                Class clazz = beanDefinition.getClazz();
                try {
                    //获取无参构造
                    Constructor constructor = clazz.getDeclaredConstructor();
                    //开启暴力反射
                    constructor.setAccessible(true);
                    Object bean = constructor.newInstance();
                    beanMap.put(beanDefinition.getBeanName(), bean);
                } catch (NoSuchMethodException e) {
                    throw new NullPointerException("No args constructor is not exist");
                } catch (Exception e) {
                    throw new RuntimeException("Init instance failed");
                }
            }
        });
    }

    /**
     * 将字符串首字母小写
     *
     * @param name
     * @return
     */
    private String firstCharToLowCase(String name) {
        //获取首字符
        String first = name.substring(0, 1);
        if (first.matches("[A-Z]")) {
            //说明是大写字母，需要转小写
            first = first.toLowerCase();
        }
        return first + name.substring(1);
    }

}
