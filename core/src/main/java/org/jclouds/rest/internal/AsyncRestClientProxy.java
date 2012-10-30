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

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.concurrent.ExecutionException;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Qualifier;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.concurrent.ExceptionParsingListenableFuture;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.TransformingHttpCommand;
import org.jclouds.internal.ClassMethodArgs;
import org.jclouds.internal.ClassMethodArgsAndReturnVal;
import org.jclouds.logging.Logger;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.InvocationContext;
import org.jclouds.rest.annotations.Delegate;
import org.jclouds.util.Optionals2;
import org.jclouds.util.Throwables2;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.base.Throwables;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Binding;
import com.google.inject.ConfigurationException;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provides;
import com.google.inject.ProvisionException;
import com.google.inject.TypeLiteral;
import com.google.inject.util.Types;

/**
 * Generates RESTful clients from appropriately annotated interfaces.
 * <p/>
 * Particularly, this code delegates calls to other things.
 * <ol>
 * <li>if the method has a {@link Provides} annotation, it responds via a
 * {@link Injector} lookup</li>
 * <li>if the method has a {@link Delegate} annotation, it responds with an
 * instance of interface set in returnVal, adding the current JAXrs annotations
 * to whatever are on that class.</li>
 * <ul>
 * <li>ex. if the method with {@link Delegate} has a {@link Path} annotation,
 * and the returnval interface also has {@link Path}, these values are combined.
 * </li>
 * </ul>
 * <li>if {@link RestAnnotationProcessor#delegationMap} contains a mapping for
 * this, and the returnVal is properly assigned as a {@link ListenableFuture},
 * it responds with an http implementation.</li>
 * <li>otherwise a RuntimeException is thrown with a message including:
 * {@code method is intended solely to set constants}</li>
 * </ol>
 * 
 * @author Adrian Cole
 */
@Singleton
public class AsyncRestClientProxy<T> implements InvocationHandler {
   public Class<T> getDeclaring() {
      return declaring;
   }

   private final Injector injector;
   private final RestAnnotationProcessor<T> annotationProcessor;
   private final Class<T> declaring;
   private final Factory commandFactory;

   /**
    * maximum duration of an unbackend http Request
    */
   @Inject(optional = true)
   @Named(Constants.PROPERTY_REQUEST_TIMEOUT)
   protected long requestTimeoutMilliseconds = 30000;

   @Resource
   protected Logger logger = Logger.NULL;
   private final Function<ClassMethodArgsAndReturnVal, Optional<Object>> optionalConverter;
   private final LoadingCache<ClassMethodArgs, Object> delegateMap;

   @SuppressWarnings("unchecked")
   @Inject
   public AsyncRestClientProxy(Injector injector, Factory factory, RestAnnotationProcessor<T> util,
         TypeLiteral<T> typeLiteral, @Named("async") LoadingCache<ClassMethodArgs, Object> delegateMap) {
      this.injector = injector;
      this.optionalConverter = injector.getInstance(Key
            .get(new TypeLiteral<Function<ClassMethodArgsAndReturnVal, Optional<Object>>>() {
            }));
      this.annotationProcessor = util;
      this.declaring = (Class<T>) typeLiteral.getRawType();
      this.commandFactory = factory;
      this.delegateMap = delegateMap;
   }

   private static final Predicate<Annotation> isQualifierPresent = new Predicate<Annotation>() {

      @Override
      public boolean apply(Annotation input) {
         return input.annotationType().isAnnotationPresent(Qualifier.class);
      }

   };

   public Object invoke(Object o, Method method, Object[] args) throws Throwable {
      if (method.getName().equals("equals")) {
         return this.equals(o);
      } else if (method.getName().equals("toString")) {
         return this.toString();
      } else if (method.getName().equals("hashCode")) {
         return this.hashCode();
      } else if (method.isAnnotationPresent(Provides.class)) {
         return lookupValueFromGuice(method);
      } else if (method.isAnnotationPresent(Delegate.class)) {
         return propagateContextToDelegate(method, args);
      } else if (isRestCall(method)) {
         return createListenableFutureForHttpRequestMappedToMethodAndArgs(method, args);
      } else {
         throw new RuntimeException(String.format("Method is not annotated as either http or provider method: %s",
               method));
      }
   }

   public boolean isRestCall(Method method) {
      return annotationProcessor.getDelegateOrNull(method) != null
            && ListenableFuture.class.isAssignableFrom(method.getReturnType());
   }

   public Object propagateContextToDelegate(Method method, Object[] args) throws ExecutionException {
      Class<?> asyncClass = Optionals2.returnTypeOrTypeOfOptional(method);
      ClassMethodArgs cma = new ClassMethodArgs(asyncClass, method, args);
      Object returnVal = delegateMap.get(cma);
      if (Optionals2.isReturnTypeOptional(method)) {
         ClassMethodArgsAndReturnVal cmar = ClassMethodArgsAndReturnVal.builder().fromClassMethodArgs(cma)
               .returnVal(returnVal).build();
         return optionalConverter.apply(cmar);
      }
      return returnVal;
   }

   public Object lookupValueFromGuice(Method method) {
      try {
         // TODO: tidy
         Type genericReturnType = method.getGenericReturnType();
         try {
            Annotation qualifier = Iterables.find(ImmutableList.copyOf(method.getAnnotations()), isQualifierPresent);
            return getInstanceOfTypeWithQualifier(genericReturnType, qualifier);
         } catch (ProvisionException e) {
            throw Throwables.propagate(e.getCause());
         } catch (RuntimeException e) {
            return instanceOfTypeOrPropagate(genericReturnType, e);
         }
      } catch (ProvisionException e) {
         AuthorizationException aex = Throwables2.getFirstThrowableOfType(e, AuthorizationException.class);
         if (aex != null)
            throw aex;
         throw e;
      }
   }

   // TODO: tidy
   private Object instanceOfTypeOrPropagate(Type genericReturnType, RuntimeException e) {
      try {
         // look for an existing binding
         Binding<?> binding = injector.getExistingBinding(Key.get(genericReturnType));
         if (binding != null)
            return binding.getProvider().get();

         // then, try looking via supplier
         binding = injector.getExistingBinding(Key.get(Types.newParameterizedType(Supplier.class, genericReturnType)));
         if (binding != null)
            return Supplier.class.cast(binding.getProvider().get()).get();

         // else try to create an instance
         return injector.getInstance(Key.get(genericReturnType));
      } catch (ConfigurationException ce) {
         throw e;
      }
   }

   // TODO: tidy
   private Object getInstanceOfTypeWithQualifier(Type genericReturnType, Annotation qualifier) {
      // look for an existing binding
      Binding<?> binding = injector.getExistingBinding(Key.get(genericReturnType, qualifier));
      if (binding != null)
         return binding.getProvider().get();

      // then, try looking via supplier
      binding = injector.getExistingBinding(Key.get(Types.newParameterizedType(Supplier.class, genericReturnType),
            qualifier));
      if (binding != null)
         return Supplier.class.cast(binding.getProvider().get()).get();

      // else try to create an instance
      return injector.getInstance(Key.get(genericReturnType, qualifier));
   }

   @SuppressWarnings({ "unchecked", "rawtypes" })
   private ListenableFuture<?> createListenableFutureForHttpRequestMappedToMethodAndArgs(Method method, Object[] args)
         throws ExecutionException {
      method = annotationProcessor.getDelegateOrNull(method);
      logger.trace("Converting %s.%s", declaring.getSimpleName(), method.getName());
      Function<Exception, ?> exceptionParser = annotationProcessor
            .createExceptionParserOrThrowResourceNotFoundOn404IfNoAnnotation(method);
      // in case there is an exception creating the request, we should at least
      // pass in args
      if (exceptionParser instanceof InvocationContext) {
         ((InvocationContext) exceptionParser).setContext((HttpRequest) null);
      }
      ListenableFuture<?> result;
      try {
         GeneratedHttpRequest request = annotationProcessor.createRequest(method, args);
         if (exceptionParser instanceof InvocationContext) {
            ((InvocationContext) exceptionParser).setContext(request);
         }
         logger.trace("Converted %s.%s to %s", declaring.getSimpleName(), method.getName(), request.getRequestLine());

         Function<HttpResponse, ?> transformer = annotationProcessor.createResponseParser(method, request);
         logger.trace("Response from %s.%s is parsed by %s", declaring.getSimpleName(), method.getName(), transformer
               .getClass().getSimpleName());

         logger.debug("Invoking %s.%s", declaring.getSimpleName(), method.getName());
         result = commandFactory.create(request, transformer).execute();

      } catch (RuntimeException e) {
         AuthorizationException aex = Throwables2.getFirstThrowableOfType(e, AuthorizationException.class);
         if (aex != null)
            e = aex;
         if (exceptionParser != null) {
            try {
               return Futures.immediateFuture(exceptionParser.apply(e));
            } catch (Exception ex) {
               return Futures.immediateFailedFuture(ex);
            }
         }
         return Futures.immediateFailedFuture(e);
      }

      if (exceptionParser != null) {
         logger.trace("Exceptions from %s.%s are parsed by %s", declaring.getSimpleName(), method.getName(),
               exceptionParser.getClass().getSimpleName());
         result = new ExceptionParsingListenableFuture(result, exceptionParser);
      }
      return result;
   }

   public static interface Factory {
      public TransformingHttpCommand<?> create(HttpRequest request, Function<HttpResponse, ?> transformer);
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == null || !(obj instanceof AsyncRestClientProxy<?>))
         return false;
      AsyncRestClientProxy<?> other = (AsyncRestClientProxy<?>) obj;
      if (other == this)
         return true;
      if (other.declaring != this.declaring)
         return false;
      return super.equals(obj);
   }

   @Override
   public int hashCode() {
      return declaring.hashCode();
   }

   public String toString() {
      return "Client Proxy for :" + declaring.getName();
   }
}