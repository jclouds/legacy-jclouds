/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

   GeneratedHttpRequest(String method, URI endpoint, Class<T> declaring, Method javaMethod,
            Object... args) {
      this(method, endpoint, new char[] {}, declaring, javaMethod, args);
   }

   GeneratedHttpRequest(String method, URI endpoint, char[] skips, Class<T> declaring,
            Method javaMethod, Object... args) {
      super(method, endpoint, skips);
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

}
