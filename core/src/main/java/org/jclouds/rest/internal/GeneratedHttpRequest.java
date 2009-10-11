package org.jclouds.rest.internal;

import java.lang.reflect.Method;
import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import org.jclouds.http.HttpRequest;

/**
 * Represents a request generated from annotations
 * 
 * @author Adrian Cole
 */
public class GeneratedHttpRequest<T> extends HttpRequest {
   private final Class<T> declaring;
   private final Method javaMethod;
   private final Object[] args;
   private final RestAnnotationProcessor<T> processor;

   GeneratedHttpRequest(String method, URI endPoint, RestAnnotationProcessor<T> processor,
            Class<T> declaring, Method javaMethod, Object... args) {
      super(method, endPoint);
      this.processor = processor;
      this.declaring = declaring;
      this.javaMethod = javaMethod;
      this.args = args;
   }

   public Class<T> getDeclaring() {
      return declaring;
   }

   public Method getJavaMethod() {
      return javaMethod;
   }

   public Object[] getArgs() {
      return args;
   }

   public RestAnnotationProcessor<T> getProcessor() {
      return processor;
   }

   public void replaceQueryParam(String name, Object... values) {
      UriBuilder builder = UriBuilder.fromUri(getEndpoint());
      builder.replaceQueryParam(name, values);
      URI newEndpoint = processor.replaceQuery(getEndpoint(), builder.build().getQuery());
      setEndpoint(newEndpoint);
   }
}
