/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.demo.tweetstore.config;

import static com.google.common.base.Preconditions.checkArgument;
import static org.jclouds.logging.LoggingModules.firstOrJDKLoggingModule;

import java.util.Set;

import javax.annotation.PostConstruct;

import org.jclouds.logging.Logger;
import org.jclouds.logging.Logger.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.annotation.CommonAnnotationBeanPostProcessor;

/**
 * Spring config that sets up {@link CommonAnnotationBeanPostProcessor} support 
 * for injecting loggers.
 * 
 * @author Andrew Phillips
 */
abstract class LoggingConfig implements BeanFactoryAware {
    protected static final LoggerFactory LOGGER_FACTORY = firstOrJDKLoggingModule().createLoggerFactory();
    
    private static final Logger LOGGER = LOGGER_FACTORY.getLogger(LoggingConfig.class.getName());
    
    private AutowireCapableBeanFactory beanFactory;
    
    @PostConstruct
    public void initLoggerSupport() {
        CommonAnnotationBeanPostProcessor resourceProcessor = 
            (CommonAnnotationBeanPostProcessor) beanFactory.getBean(AnnotationConfigUtils.COMMON_ANNOTATION_PROCESSOR_BEAN_NAME);
        resourceProcessor.setResourceFactory(new LoggerResourceBeanFactory(beanFactory));
    }
    
    private static class LoggerResourceBeanFactory extends DelegatingAutowireCapableBeanFactory {
        
        LoggerResourceBeanFactory(AutowireCapableBeanFactory delegate) {
            super(delegate);
        }

        @Override
        public Object resolveDependency(DependencyDescriptor descriptor,
                String beanName, Set<String> autowiredBeanNames,
                TypeConverter typeConverter) throws BeansException {
            Object bean;
            if (descriptor.getDependencyType().equals(Logger.class)) {
                Class<?> requestingType = getType(beanName);
                LOGGER.trace("About to create logger for bean '%s' of type '%s'", 
                        beanName, requestingType);
                bean = LOGGER_FACTORY.getLogger(requestingType.getName());
                LOGGER.trace("Successfully created logger.");
                return bean;
            }
            return super.resolveDependency(descriptor, beanName, autowiredBeanNames, typeConverter);
        }
    }
    
    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        checkArgument(beanFactory instanceof AutowireCapableBeanFactory, "expected an instance of '%s' but was '%s'", 
                AutowireCapableBeanFactory.class, beanFactory.getClass());
        this.beanFactory = (AutowireCapableBeanFactory) beanFactory;
    }
}