package org.jclouds.vcloud.director.v1_5.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

/**
 * The login url for the vCloud, typically {@code https://vdc_host/api/sessions}
 * 
 * @author Adrian Cole
 * 
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = { ElementType.METHOD, ElementType.PARAMETER })
@Qualifier
public @interface Login {

}
