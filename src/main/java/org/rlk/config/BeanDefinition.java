package org.rlk.config;

import java.util.Objects;

/**
 * @author: rlk
 * @date: 2022/8/26
 * Description: BeanDefinition定义
 */
public class BeanDefinition {

    private String beanName;
    private Class clazz;
    private Boolean isLazy;
    private String scope;

    public BeanDefinition() {
    }

    public BeanDefinition(String beanName, Class clazz, Boolean isLazy) {
        this.beanName = beanName;
        this.clazz = clazz;
        this.isLazy = isLazy;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public Class getClazz() {
        return clazz;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

    public Boolean getLazy() {
        return isLazy;
    }

    public void setLazy(Boolean lazy) {
        isLazy = lazy;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BeanDefinition that = (BeanDefinition) o;
        return Objects.equals(beanName, that.beanName) &&
                Objects.equals(clazz, that.clazz) &&
                Objects.equals(isLazy, that.isLazy) &&
                Objects.equals(scope, that.scope);
    }

    @Override
    public int hashCode() {
        return Objects.hash(beanName, clazz, isLazy, scope);
    }

    @Override
    public String toString() {
        return "BeanDefinition{" +
                "beanName='" + beanName + '\'' +
                ", clazz=" + clazz +
                ", isLazy=" + isLazy +
                ", scope='" + scope + '\'' +
                '}';
    }
}
