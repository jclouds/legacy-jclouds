package org.jclouds.aws.s3.config;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.jclouds.http.HttpFutureCommandClient;

/**
 * designates the the module configures a {@link org.jclouds.aws.s3.S3Connection}
 *
 * @author Adrian Cole
 *
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface S3ConnectionModule {

}
