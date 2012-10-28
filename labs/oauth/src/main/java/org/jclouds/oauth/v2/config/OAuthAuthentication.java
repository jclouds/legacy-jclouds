package org.jclouds.oauth.v2.config;


import javax.inject.Qualifier;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to annotate REST methods that use OAuthAuthentication.
 * <p/>
 * TODO: use this to have fine-grained specification of scopes, etc.
 *
 * @author David Alves
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = {ElementType.TYPE, ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
@Qualifier
public @interface OAuthAuthentication {

   String[] scopes();

   String signatureAlgorithm() default "RS256";

   String assertionDescription();

   String[] additionalClaims();
}
