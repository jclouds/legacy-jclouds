package org.jclouds.aws.ec2.config;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * designates the the module configures a {@link org.jclouds.aws.ec2.EC2Connection}
 * 
 * @author Adrian Cole
 * 
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface EC2ConnectionModule {

}
