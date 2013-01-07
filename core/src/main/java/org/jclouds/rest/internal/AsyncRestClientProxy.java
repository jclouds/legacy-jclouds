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

import static com.google.common.base.Functions.compose;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Predicates.in;
import static com.google.common.base.Predicates.not;
import static com.google.common.base.Throwables.propagate;
import static com.google.common.collect.Iterables.any;
import static com.google.common.collect.Iterables.find;
import static com.google.common.util.concurrent.Futures.immediateFailedFuture;
import static com.google.common.util.concurrent.Futures.transform;
import static com.google.common.util.concurrent.Futures.withFallback;
import static com.google.inject.util.Types.newParameterizedType;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import static org.jclouds.concurrent.Futures.makeListenable;
import static org.jclouds.http.HttpUtils.tryFindHttpMethod;
import static org.jclouds.util.Optionals2.isReturnTypeOptional;
import static org.jclouds.util.Optionals2.unwrapIfOptional;
import static org.jclouds.util.Throwables2.getFirstThrowableOfType;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.net.URI;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Qualifier;
import javax.inject.Singleton;
import javax.lang.model.type.NullType;
import javax.ws.rs.Path;

import org.jclouds.Constants;
import org.jclouds.fallbacks.MapHttp4xxCodesToExceptions;
import org.jclouds.functions.IdentityFunction;
import org.jclouds.functions.OnlyElementOrNull;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpCommandExecutorService;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseFirstJsonValueNamed;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ParseSax.HandlerWithResult;
import org.jclouds.http.functions.ParseURIFromListOrLocationHeaderIf20x;
import org.jclouds.http.functions.ParseXMLWithJAXB;
import org.jclouds.http.functions.ReleasePayloadAndReturn;
import org.jclouds.http.functions.ReturnInputStream;
import org.jclouds.http.functions.ReturnStringIf2xx;
import org.jclouds.http.functions.ReturnTrueIf2xx;
import org.jclouds.http.functions.UnwrapOnlyJsonValue;
import org.jclouds.json.internal.GsonWrapper;
import org.jclouds.logging.Logger;
import org.jclouds.reflect.FunctionalReflection;
import org.jclouds.reflect.Invocation;
import org.jclouds.reflect.Invocation.Result;
import org.jclouds.reflect.InvocationSuccess;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.InvocationContext;
import org.jclouds.rest.annotations.Delegate;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.JAXBResponseParser;
import org.jclouds.rest.annotations.OnlyElement;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.annotations.Transform;
import org.jclouds.rest.annotations.Unwrap;
import org.jclouds.rest.annotations.XMLResponseParser;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.Invokable;
import com.google.common.reflect.Parameter;
import com.google.common.reflect.TypeToken;
import com.google.common.util.concurrent.FutureFallback;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Binding;
import com.google.inject.ConfigurationException;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provides;
import com.google.inject.ProvisionException;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.Assisted;

/**
 * Generates RESTful clients from appropriately annotated interfaces.
 * <p/>
 * Particularly, this code delegates calls to other things.
 * <ol>
 * <li>if the invoked has a {@link Provides} annotation, it responds via a {@link Injector} lookup</li>
 * <li>if the invoked has a {@link Delegate} annotation, it responds with an instance of interface set in returnVal,
 * adding the current JAXrs annotations to whatever are on that class.</li>
 * <ul>
 * <li>ex. if the invoked with {@link Delegate} has a {@code Path} annotation, and the returnval interface also has
 * {@code Path}, these values are combined.</li>
 * </ul>
 * <li>if {@link RestAnnotationProcessor#delegationMap} contains a mapping for this, and the returnVal is properly
 * assigned as a {@link ListenableFuture}, it responds with an http implementation.</li>
 * <li>otherwise a RuntimeException is thrown with a message including:
 * {@code invoked is intended solely to set constants}</li>
 * </ol>
 * 
 * @author Adrian Cole
 */
@Singleton
public class AsyncRestClientProxy implements Function<Invocation, Result> {

   public final static class Caller extends AsyncRestClientProxy {

      public static interface Factory {
         Caller caller(Invocation caller, Class<?> interfaceType);
      }

      @Inject
      private Caller(Injector injector, Function<InvocationSuccess, Optional<Object>> optionalConverter,
            HttpCommandExecutorService http, @Named(Constants.PROPERTY_USER_THREADS) ExecutorService userThreads,
            Caller.Factory factory, RestAnnotationProcessor.Caller.Factory rap, ParseSax.Factory parserFactory,
            @Assisted Invocation caller) {
         super(injector, optionalConverter, http, userThreads, factory, rap.caller(caller), parserFactory);
      }
   }

   @Resource
   private Logger logger = Logger.NULL;

   private final Injector injector;
   private final HttpCommandExecutorService http;
   private final ExecutorService userThreads;
   private final Function<InvocationSuccess, Optional<Object>> optionalConverter;
   private final Caller.Factory factory;
   private final RestAnnotationProcessor annotationProcessor;
   private final ParseSax.Factory parserFactory;

   private static final LoadingCache<Class<?>, Set<InterfaceNameAndParameters>> delegationMapCache = CacheBuilder
         .newBuilder().build(new CacheLoader<Class<?>, Set<InterfaceNameAndParameters>>() {
            public Set<InterfaceNameAndParameters> load(final Class<?> interfaceType) throws ExecutionException {
               return FluentIterable.from(ImmutableSet.copyOf(interfaceType.getMethods()))
                     .filter(not(in(ImmutableSet.copyOf(Object.class.getMethods()))))
                     .transform(new Function<Method, Invokable<?, ?>>() {
                        public Invokable<?, ?> apply(Method in) {
                           return Invokable.from(in);
                        }
                     }).filter(new Predicate<Invokable<?, ?>>() {
                        public boolean apply(Invokable<?, ?> in) {
                           return in.isAnnotationPresent(Path.class) || tryFindHttpMethod(in).isPresent()
                                 || any(in.getParameters(), new Predicate<Parameter>() {
                                    public boolean apply(Parameter in) {
                                       return in.getType().getRawType().isAssignableFrom(HttpRequest.class);
                                    }
                                 });
                        }
                     }).filter(new Predicate<Invokable<?, ?>>() {
                        public boolean apply(Invokable<?, ?> in) {
                           return in.getReturnType().getRawType().isAssignableFrom(ListenableFuture.class);
                        }
                     }).transform(new Function<Invokable<?, ?>, InterfaceNameAndParameters>() {
                        public InterfaceNameAndParameters apply(Invokable<?, ?> in) {
                           return new InterfaceNameAndParameters(interfaceType, in.getName(), in.getParameters());
                        }
                     }).toSet();
            }
         });

   private static final class InterfaceNameAndParameters {
      private final Class<?> interfaceType;
      private final String name;
      private final int parametersTypeHashCode;

      private InterfaceNameAndParameters(Class<?> interfaceType, String name, ImmutableList<Parameter> parameters) {
         this.interfaceType = interfaceType;
         this.name = name;
         int parametersTypeHashCode = 0;
         for (Parameter param : parameters)
            parametersTypeHashCode += param.getType().hashCode();
         this.parametersTypeHashCode = parametersTypeHashCode;
      }

      public int hashCode() {
         return Objects.hashCode(interfaceType, name, parametersTypeHashCode);
      }

      @Override
      public boolean equals(Object o) {
         if (this == o)
            return true;
         if (o == null || getClass() != o.getClass())
            return false;
         InterfaceNameAndParameters that = InterfaceNameAndParameters.class.cast(o);
         return equal(this.interfaceType, that.interfaceType) && equal(this.name, that.name)
               && equal(this.parametersTypeHashCode, that.parametersTypeHashCode);
      }
   }

   @Inject
   private AsyncRestClientProxy(Injector injector, Function<InvocationSuccess, Optional<Object>> optionalConverter,
         HttpCommandExecutorService http, @Named(Constants.PROPERTY_USER_THREADS) ExecutorService userThreads,
         Caller.Factory factory, RestAnnotationProcessor annotationProcessor, ParseSax.Factory parserFactory) {
      this.injector = injector;
      this.optionalConverter = optionalConverter;
      this.http = http;
      this.userThreads = userThreads;
      this.factory = factory;
      this.annotationProcessor = annotationProcessor;
      this.parserFactory = parserFactory;
   }

   private static final Predicate<Annotation> isQualifierPresent = new Predicate<Annotation>() {
      public boolean apply(Annotation input) {
         return input.annotationType().isAnnotationPresent(Qualifier.class);
      }
   };

   @Override
   public Result apply(Invocation invocation) {
      if (invocation.getInvokable().isAnnotationPresent(Provides.class)) {
         return Result.success(lookupValueFromGuice(invocation.getInvokable()));
      } else if (invocation.getInvokable().isAnnotationPresent(Delegate.class)) {
         return Result.success(propagateContextToDelegate(invocation));
      } else if (isAsyncOrDelegate(invocation)) {
         return Result.success(createListenableFutureForHttpRequestMappedToMethodAndArgs(invocation));
      } else {
         return Result.fail(new IllegalStateException(String.format(
               "Method is not annotated as either http or provider invoked: %s", invocation.getInvokable())));
      }
   }

   private boolean isAsyncOrDelegate(Invocation invocation) {
      return delegationMapCache.getUnchecked(invocation.getInterfaceType()).contains(
            new InterfaceNameAndParameters(invocation.getInterfaceType(), invocation.getInvokable().getName(),
                  invocation.getInvokable().getParameters()));
   }

   private Object propagateContextToDelegate(Invocation invocation) {
      Class<?> returnType = unwrapIfOptional(invocation.getInvokable().getReturnType());
      Object result = FunctionalReflection.newProxy(returnType, factory.caller(invocation, returnType));
      if (isReturnTypeOptional(invocation.getInvokable())) {
         return optionalConverter.apply(InvocationSuccess.create(invocation, result));
      }
      return result;
   }

   private Object lookupValueFromGuice(Invokable<?, ?> invoked) {
      try {
         Type genericReturnType = invoked.getReturnType().getType();
         try {
            Annotation qualifier = find(ImmutableList.copyOf(invoked.getAnnotations()), isQualifierPresent);
            return getInstanceOfTypeWithQualifier(genericReturnType, qualifier);
         } catch (ProvisionException e) {
            throw propagate(e.getCause());
         } catch (RuntimeException e) {
            return instanceOfTypeOrPropagate(genericReturnType, e);
         }
      } catch (ProvisionException e) {
         AuthorizationException aex = getFirstThrowableOfType(e, AuthorizationException.class);
         if (aex != null)
            throw aex;
         throw e;
      }
   }

   private Object instanceOfTypeOrPropagate(Type genericReturnType, RuntimeException e) {
      try {
         // look for an existing binding
         Binding<?> binding = injector.getExistingBinding(Key.get(genericReturnType));
         if (binding != null)
            return binding.getProvider().get();

         // then, try looking via supplier
         binding = injector.getExistingBinding(Key.get(newParameterizedType(Supplier.class, genericReturnType)));
         if (binding != null)
            return Supplier.class.cast(binding.getProvider().get()).get();

         // else try to create an instance
         return injector.getInstance(Key.get(genericReturnType));
      } catch (ConfigurationException ce) {
         throw e;
      }
   }

   private Object getInstanceOfTypeWithQualifier(Type genericReturnType, Annotation qualifier) {
      // look for an existing binding
      Binding<?> binding = injector.getExistingBinding(Key.get(genericReturnType, qualifier));
      if (binding != null)
         return binding.getProvider().get();

      // then, try looking via supplier
      binding = injector
            .getExistingBinding(Key.get(newParameterizedType(Supplier.class, genericReturnType), qualifier));
      if (binding != null)
         return Supplier.class.cast(binding.getProvider().get()).get();

      // else try to create an instance
      return injector.getInstance(Key.get(genericReturnType, qualifier));
   }

   @SuppressWarnings("unchecked")
   @VisibleForTesting
   static Function<HttpResponse, ?> createResponseParser(ParseSax.Factory parserFactory, Injector injector,
         Invocation invocation, HttpRequest request) {
      Function<HttpResponse, ?> transformer;
      Class<? extends HandlerWithResult<?>> handler = getSaxResponseParserClassOrNull(invocation.getInvokable());
      if (handler != null) {
         transformer = parserFactory.create(injector.getInstance(handler));
      } else {
         transformer = getTransformerForMethod(invocation, injector);
      }
      if (transformer instanceof InvocationContext<?>) {
         ((InvocationContext<?>) transformer).setContext(request);
      }
      if (invocation.getInvokable().isAnnotationPresent(Transform.class)) {
         Function<?, ?> wrappingTransformer = injector.getInstance(invocation.getInvokable()
               .getAnnotation(Transform.class).value());
         if (wrappingTransformer instanceof InvocationContext<?>) {
            ((InvocationContext<?>) wrappingTransformer).setContext(request);
         }
         transformer = compose(Function.class.cast(wrappingTransformer), transformer);
      }
      return transformer;
   }

   private static Class<? extends HandlerWithResult<?>> getSaxResponseParserClassOrNull(Invokable<?, ?> invoked) {
      XMLResponseParser annotation = invoked.getAnnotation(XMLResponseParser.class);
      if (annotation != null) {
         return annotation.value();
      }
      return null;
   }

   // TODO: refactor this out of here
   @VisibleForTesting
   @SuppressWarnings({ "rawtypes", "unchecked" })
   public static Function<HttpResponse, ?> getTransformerForMethod(Invocation invocation, Injector injector) {
      Invokable<?, ?> invoked = invocation.getInvokable();
      Function<HttpResponse, ?> transformer;
      if (invoked.isAnnotationPresent(SelectJson.class)) {
         Type returnVal = getReturnTypeFor(invoked.getReturnType());
         if (invoked.isAnnotationPresent(OnlyElement.class))
            returnVal = newParameterizedType(Set.class, returnVal);
         transformer = new ParseFirstJsonValueNamed(injector.getInstance(GsonWrapper.class),
               TypeLiteral.get(returnVal), invoked.getAnnotation(SelectJson.class).value());
         if (invoked.isAnnotationPresent(OnlyElement.class))
            transformer = compose(new OnlyElementOrNull(), transformer);
      } else {
         transformer = injector.getInstance(getParserOrThrowException(invocation));
      }
      return transformer;
   }

   private ListenableFuture<?> createListenableFutureForHttpRequestMappedToMethodAndArgs(Invocation invocation) {
      String name = invocation.getInterfaceType().getSimpleName() + "." + invocation.getInvokable().getName();
      logger.trace(">> converting %s", name);
      FutureFallback<?> fallback = fallbacks.getUnchecked(invocation.getInvokable());
      // in case there is an exception creating the request, we should at least pass in args
      if (fallback instanceof InvocationContext) {
         InvocationContext.class.cast(fallback).setContext((HttpRequest) null);
      }
      ListenableFuture<?> result;
      try {
         GeneratedHttpRequest request = annotationProcessor.apply(invocation);
         if (fallback instanceof InvocationContext) {
            InvocationContext.class.cast(fallback).setContext(request);
         }
         logger.trace("<< converted %s to %s", name, request.getRequestLine());

         Function<HttpResponse, ?> transformer = createResponseParser(parserFactory, injector, invocation, request);
         logger.trace("<< response from %s is parsed by %s", name, transformer.getClass().getSimpleName());

         logger.debug(">> invoking %s", name);
         result = transform(makeListenable(http.submit(new HttpCommand(request)), userThreads), transformer);
      } catch (RuntimeException e) {
         AuthorizationException aex = getFirstThrowableOfType(e, AuthorizationException.class);
         if (aex != null)
            e = aex;
         try {
            return fallback.create(e);
         } catch (Exception ex) {
            return immediateFailedFuture(ex);
         }
      }
      logger.trace("<< exceptions from %s are parsed by %s", name, fallback.getClass().getSimpleName());
      return withFallback(result, fallback);
   }

   @Override
   public String toString() {
      return String.format("async->http");
   }

   private final LoadingCache<Invokable<?, ?>, FutureFallback<?>> fallbacks = CacheBuilder.newBuilder().build(
         new CacheLoader<Invokable<?, ?>, FutureFallback<?>>() {

            @Override
            public FutureFallback<?> load(Invokable<?, ?> key) throws Exception {
               Fallback annotation = key.getAnnotation(Fallback.class);
               if (annotation != null) {
                  return injector.getInstance(annotation.value());
               }
               return injector.getInstance(MapHttp4xxCodesToExceptions.class);
            }

         });

   private static final TypeToken<ListenableFuture<Boolean>> futureBooleanLiteral = new TypeToken<ListenableFuture<Boolean>>() {
      private static final long serialVersionUID = 1L;
   };
   private static final TypeToken<ListenableFuture<String>> futureStringLiteral = new TypeToken<ListenableFuture<String>>() {
      private static final long serialVersionUID = 1L;
   };
   private static final TypeToken<ListenableFuture<Void>> futureVoidLiteral = new TypeToken<ListenableFuture<Void>>() {
      private static final long serialVersionUID = 1L;
   };
   private static final TypeToken<ListenableFuture<URI>> futureURILiteral = new TypeToken<ListenableFuture<URI>>() {
      private static final long serialVersionUID = 1L;
   };
   private static final TypeToken<ListenableFuture<InputStream>> futureInputStreamLiteral = new TypeToken<ListenableFuture<InputStream>>() {
      private static final long serialVersionUID = 1L;
   };
   private static final TypeToken<ListenableFuture<HttpResponse>> futureHttpResponseLiteral = new TypeToken<ListenableFuture<HttpResponse>>() {
      private static final long serialVersionUID = 1L;
   };

   @SuppressWarnings("unchecked")
   @VisibleForTesting
   static Key<? extends Function<HttpResponse, ?>> getParserOrThrowException(Invocation invocation) {
      Invokable<?, ?> invoked = invocation.getInvokable();
      ResponseParser annotation = invoked.getAnnotation(ResponseParser.class);
      if (annotation == null) {
         if (invoked.getReturnType().equals(void.class) || invoked.getReturnType().equals(futureVoidLiteral)) {
            return Key.get(ReleasePayloadAndReturn.class);
         } else if (invoked.getReturnType().equals(boolean.class) || invoked.getReturnType().equals(Boolean.class)
               || invoked.getReturnType().equals(futureBooleanLiteral)) {
            return Key.get(ReturnTrueIf2xx.class);
         } else if (invoked.getReturnType().equals(InputStream.class)
               || invoked.getReturnType().equals(futureInputStreamLiteral)) {
            return Key.get(ReturnInputStream.class);
         } else if (invoked.getReturnType().equals(HttpResponse.class)
               || invoked.getReturnType().equals(futureHttpResponseLiteral)) {
            return Key.get(Class.class.cast(IdentityFunction.class));
         } else if (RestAnnotationProcessor.getAcceptHeaders(invocation).contains(APPLICATION_JSON)) {
            return getJsonParserKeyForMethod(invoked);
         } else if (RestAnnotationProcessor.getAcceptHeaders(invocation).contains(APPLICATION_XML)
               || invoked.isAnnotationPresent(JAXBResponseParser.class)) {
            return getJAXBParserKeyForMethod(invoked);
         } else if (invoked.getReturnType().equals(String.class) || invoked.getReturnType().equals(futureStringLiteral)) {
            return Key.get(ReturnStringIf2xx.class);
         } else if (invoked.getReturnType().equals(URI.class) || invoked.getReturnType().equals(futureURILiteral)) {
            return Key.get(ParseURIFromListOrLocationHeaderIf20x.class);
         } else {
            throw new IllegalStateException("You must specify a ResponseParser annotation on: " + invoked.toString());
         }
      }
      return Key.get(annotation.value());
   }

   @SuppressWarnings("unchecked")
   private static Key<? extends Function<HttpResponse, ?>> getJAXBParserKeyForMethod(Invokable<?, ?> invoked) {
      Optional<Type> configuredReturnVal = Optional.absent();
      if (invoked.isAnnotationPresent(JAXBResponseParser.class)) {
         Type configuredClass = invoked.getAnnotation(JAXBResponseParser.class).value();
         configuredReturnVal = configuredClass.equals(NullType.class) ? Optional.<Type> absent() : Optional
               .<Type> of(configuredClass);
      }
      Type returnVal = configuredReturnVal.or(getReturnTypeFor(invoked.getReturnType()));
      Type parserType = newParameterizedType(ParseXMLWithJAXB.class, returnVal);
      return (Key<? extends Function<HttpResponse, ?>>) Key.get(parserType);
   }

   @SuppressWarnings({ "unchecked" })
   private static Key<? extends Function<HttpResponse, ?>> getJsonParserKeyForMethod(Invokable<?, ?> invoked) {
      ParameterizedType parserType;
      if (invoked.isAnnotationPresent(Unwrap.class)) {
         parserType = newParameterizedType(UnwrapOnlyJsonValue.class, getReturnTypeFor(invoked.getReturnType()));
      } else {
         parserType = newParameterizedType(ParseJson.class, getReturnTypeFor(invoked.getReturnType()));
      }
      return (Key<? extends Function<HttpResponse, ?>>) Key.get(parserType);
   }

   private static Type getReturnTypeFor(TypeToken<?> typeToken) {
      Type returnVal = typeToken.getType();
      if (typeToken.getRawType().getTypeParameters().length == 0) {
         returnVal = typeToken.getRawType();
      } else if (typeToken.getRawType().equals(ListenableFuture.class)) {
         ParameterizedType futureType = (ParameterizedType) typeToken.getType();
         returnVal = futureType.getActualTypeArguments()[0];
         if (returnVal instanceof WildcardType)
            returnVal = WildcardType.class.cast(returnVal).getUpperBounds()[0];
      }
      return returnVal;
   }

}
