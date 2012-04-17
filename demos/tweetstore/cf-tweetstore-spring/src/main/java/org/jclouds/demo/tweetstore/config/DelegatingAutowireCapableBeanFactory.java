package org.jclouds.demo.tweetstore.config;

import java.util.Set;

import org.springframework.beans.BeansException;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.DependencyDescriptor;

class DelegatingAutowireCapableBeanFactory implements AutowireCapableBeanFactory {
    private final AutowireCapableBeanFactory delegate;
    
    DelegatingAutowireCapableBeanFactory(AutowireCapableBeanFactory delegate) {
        this.delegate = delegate;
    }

    public <T> T createBean(Class<T> beanClass) throws BeansException {
        return delegate.createBean(beanClass);
    }

    public void autowireBean(Object existingBean) throws BeansException {
        delegate.autowireBean(existingBean);
    }

    public Object configureBean(Object existingBean, String beanName)
            throws BeansException {
        return delegate.configureBean(existingBean, beanName);
    }

    public Object getBean(String name) throws BeansException {
        return delegate.getBean(name);
    }

    public Object resolveDependency(DependencyDescriptor descriptor,
            String beanName) throws BeansException {
        return delegate.resolveDependency(descriptor, beanName);
    }

    public <T> T getBean(String name, Class<T> requiredType)
            throws BeansException {
        return delegate.getBean(name, requiredType);
    }

    @SuppressWarnings("rawtypes")
    public Object createBean(Class beanClass, int autowireMode,
            boolean dependencyCheck) throws BeansException {
        return delegate.createBean(beanClass, autowireMode, dependencyCheck);
    }

    public <T> T getBean(Class<T> requiredType) throws BeansException {
        return delegate.getBean(requiredType);
    }

    @SuppressWarnings("rawtypes")
    public Object autowire(Class beanClass, int autowireMode,
            boolean dependencyCheck) throws BeansException {
        return delegate.autowire(beanClass, autowireMode, dependencyCheck);
    }

    public Object getBean(String name, Object... args) throws BeansException {
        return delegate.getBean(name, args);
    }

    public void autowireBeanProperties(Object existingBean, int autowireMode,
            boolean dependencyCheck) throws BeansException {
        delegate.autowireBeanProperties(existingBean, autowireMode,
                dependencyCheck);
    }

    public boolean containsBean(String name) {
        return delegate.containsBean(name);
    }

    public boolean isSingleton(String name)
            throws NoSuchBeanDefinitionException {
        return delegate.isSingleton(name);
    }

    public void applyBeanPropertyValues(Object existingBean, String beanName)
            throws BeansException {
        delegate.applyBeanPropertyValues(existingBean, beanName);
    }

    public boolean isPrototype(String name)
            throws NoSuchBeanDefinitionException {
        return delegate.isPrototype(name);
    }

    @SuppressWarnings("rawtypes")
    public boolean isTypeMatch(String name, Class targetType)
            throws NoSuchBeanDefinitionException {
        return delegate.isTypeMatch(name, targetType);
    }

    public Object initializeBean(Object existingBean, String beanName)
            throws BeansException {
        return delegate.initializeBean(existingBean, beanName);
    }

    public Class<?> getType(String name) throws NoSuchBeanDefinitionException {
        return delegate.getType(name);
    }

    public Object applyBeanPostProcessorsBeforeInitialization(
            Object existingBean, String beanName) throws BeansException {
        return delegate.applyBeanPostProcessorsBeforeInitialization(
                existingBean, beanName);
    }

    public String[] getAliases(String name) {
        return delegate.getAliases(name);
    }

    public Object applyBeanPostProcessorsAfterInitialization(
            Object existingBean, String beanName) throws BeansException {
        return delegate.applyBeanPostProcessorsAfterInitialization(
                existingBean, beanName);
    }

    public Object resolveDependency(DependencyDescriptor descriptor,
            String beanName, Set<String> autowiredBeanNames,
            TypeConverter typeConverter) throws BeansException {
        return delegate.resolveDependency(descriptor, beanName,
                autowiredBeanNames, typeConverter);
    }


}
