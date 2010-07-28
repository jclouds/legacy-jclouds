package org.jclouds.ohai.config;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 
 * @author Adrian Cole
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface ConfiguresOhai {
}
