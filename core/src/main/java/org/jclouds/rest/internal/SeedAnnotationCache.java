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
package org.jclouds.rest.internal;

import static com.google.common.collect.Sets.difference;
import static org.jclouds.rest.internal.RestAnnotationProcessor.getHttpMethods;
import static org.jclouds.rest.internal.RestAnnotationProcessor.methodToIndexOfParamToBinderParamAnnotation;
import static org.jclouds.rest.internal.RestAnnotationProcessor.methodToIndexOfParamToEndpointAnnotations;
import static org.jclouds.rest.internal.RestAnnotationProcessor.methodToIndexOfParamToEndpointParamAnnotations;
import static org.jclouds.rest.internal.RestAnnotationProcessor.methodToIndexOfParamToFormParamAnnotations;
import static org.jclouds.rest.internal.RestAnnotationProcessor.methodToIndexOfParamToHeaderParamAnnotations;
import static org.jclouds.rest.internal.RestAnnotationProcessor.methodToIndexOfParamToMatrixParamAnnotations;
import static org.jclouds.rest.internal.RestAnnotationProcessor.methodToIndexOfParamToParamParserAnnotations;
import static org.jclouds.rest.internal.RestAnnotationProcessor.methodToIndexOfParamToPartParamAnnotations;
import static org.jclouds.rest.internal.RestAnnotationProcessor.methodToIndexOfParamToPathParamAnnotations;
import static org.jclouds.rest.internal.RestAnnotationProcessor.methodToIndexOfParamToPostParamAnnotations;
import static org.jclouds.rest.internal.RestAnnotationProcessor.methodToIndexOfParamToQueryParamAnnotations;
import static org.jclouds.rest.internal.RestAnnotationProcessor.methodToIndexOfParamToWrapWithAnnotation;
import static org.jclouds.rest.internal.RestAnnotationProcessor.methodToIndexesOfOptions;

import java.lang.reflect.Method;
import java.util.concurrent.ExecutionException;

import javax.annotation.Resource;
import javax.inject.Singleton;
import javax.ws.rs.Path;

import org.jclouds.http.HttpRequest;
import org.jclouds.logging.Logger;
import org.jclouds.rest.annotations.Delegate;
import org.jclouds.rest.internal.RestAnnotationProcessor.MethodKey;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheLoader;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provides;

/**
 * seeds the annotation cache located at
 * {@link RestAnnotationProcessor#delegationMap}. Note this call is only
 * intended to be called once per {@link RestContext} and avoids expensive
 * lookups on each call.
 * 
 * @author Adrian Cole
 */
@Singleton
public class SeedAnnotationCache extends CacheLoader<Class<?>, Boolean> {
   @Resource
   protected Logger logger = Logger.NULL;

   protected final Injector injector;
   protected final Cache<MethodKey, Method> delegationMap;

   @Inject
   public SeedAnnotationCache(Injector injector, Cache<MethodKey, Method> delegationMap) {
      this.injector = injector;
      this.delegationMap = delegationMap;
   }

   @Override
   public Boolean load(Class<?> declaring) throws ExecutionException {
      for (Method method : difference(ImmutableSet.copyOf(declaring.getMethods()), ImmutableSet.copyOf(Object.class
               .getMethods()))) {
         if (isHttpMethod(method) || method.isAnnotationPresent(Delegate.class)) {
            for (int index = 0; index < method.getParameterTypes().length; index++) {
               methodToIndexOfParamToBinderParamAnnotation.get(method).get(index);
               methodToIndexOfParamToWrapWithAnnotation.get(method).get(index);
               methodToIndexOfParamToHeaderParamAnnotations.get(method).get(index);
               methodToIndexOfParamToMatrixParamAnnotations.get(method).get(index);
               methodToIndexOfParamToFormParamAnnotations.get(method).get(index);
               methodToIndexOfParamToQueryParamAnnotations.get(method).get(index);
               methodToIndexOfParamToEndpointAnnotations.get(method).get(index);
               methodToIndexOfParamToEndpointParamAnnotations.get(method).get(index);
               methodToIndexOfParamToPathParamAnnotations.get(method).get(index);
               methodToIndexOfParamToPostParamAnnotations.get(method).get(index);
               methodToIndexOfParamToParamParserAnnotations.get(method).get(index);
               methodToIndexOfParamToPartParamAnnotations.get(method).get(index);
               methodToIndexesOfOptions.get(method);
            }
            delegationMap.put(new MethodKey(method), method);
         } else if (!method.getDeclaringClass().equals(declaring)) {
            logger.trace("skipping potentially overridden method %s", method);
         } else if (method.isAnnotationPresent(Provides.class)) {
            logger.trace("skipping provider method %s", method);
         } else {
            logger.trace("Method is not annotated as either http or provider method: %s", method);
         }
      }
      return true;
   }

   public static boolean isHttpMethod(Method method) {
      return method.isAnnotationPresent(Path.class) || getHttpMethods(method) != null
               || ImmutableSet.copyOf(method.getParameterTypes()).contains(HttpRequest.class);
   }

}
