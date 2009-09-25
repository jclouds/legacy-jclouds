package org.jclouds.azure.storage.queue;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

/**
 * Related to a resource of type Azure Queue
 * 
 * @author Adrian Cole
 * 
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = { ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD })
@Qualifier
public @interface AzureQueue {

}