/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.rest.internal;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.Comparator;
import java.util.Map.Entry;

import javax.ws.rs.core.UriBuilder;

import org.jclouds.http.HttpRequest;

import com.google.inject.internal.Nullable;

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

   public void replaceMatrixParam(String name, Object... values) {
      UriBuilder builder = UriBuilder.fromUri(getEndpoint());
      builder.replaceMatrixParam(name, values);
      replacePath(builder.build().getPath());
   }

   public void addQueryParam(String name, String... values) {
      setEndpoint(processor.addQueryParam(getEndpoint(), name, values));
   }

   public void replaceQuery(String query, @Nullable Comparator<Entry<String, String>> sorter) {
      setEndpoint(processor.replaceQuery(getEndpoint(), query, sorter));
   }

   public void replacePath(String path) {
      UriBuilder builder = UriBuilder.fromUri(getEndpoint());
      builder.replacePath(path);
      setEndpoint(builder.build());
   }

   public void addFormParam(String name, String... values) {
      this
               .setPayload(processor.addFormParam(getPayload().getRawContent().toString(), name,
                        values));
   }
}
