/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.rest.internal;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.Future;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriBuilderException;

import org.jboss.resteasy.util.IsHttpMethod;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ParseURIFromListOrLocationHeaderIf20x;
import org.jclouds.http.functions.ReturnInputStream;
import org.jclouds.http.functions.ReturnStringIf200;
import org.jclouds.http.functions.ReturnTrueIf2xx;
import org.jclouds.http.functions.ReturnVoidIf2xx;
import org.jclouds.http.functions.ParseSax.HandlerWithResult;
import org.jclouds.http.options.HttpRequestOptions;
import org.jclouds.logging.Logger;
import org.jclouds.rest.Binder;
import org.jclouds.rest.InvocationContext;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Endpoint;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.Headers;
import org.jclouds.rest.annotations.HostPrefixParam;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.MapEntityParam;
import org.jclouds.rest.annotations.MatrixParams;
import org.jclouds.rest.annotations.OverrideRequestFilters;
import org.jclouds.rest.annotations.ParamParser;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.rest.annotations.VirtualHost;
import org.jclouds.rest.annotations.XMLResponseParser;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.internal.Lists;

/**
 * Tests behavior of JaxrsUtil
 * 
 * @author Adrian Cole
 */
@Singleton
public class RestAnnotationProcessor<T> {

   @Resource
   protected Logger logger = Logger.NULL;

   private final Class<T> declaring;

   private final Map<Method, Map<Integer, Set<Annotation>>> methodToIndexOfParamToDecoratorParamAnnotation = createMethodToIndexOfParamToAnnotation(BinderParam.class);
   private final Map<Method, Map<Integer, Set<Annotation>>> methodToIndexOfParamToHeaderParamAnnotations = createMethodToIndexOfParamToAnnotation(HeaderParam.class);
   private final Map<Method, Map<Integer, Set<Annotation>>> methodToIndexOfParamToHostPrefixParamAnnotations = createMethodToIndexOfParamToAnnotation(HostPrefixParam.class);
   private final Map<Method, Map<Integer, Set<Annotation>>> methodToindexOfParamToEndpointAnnotations = createMethodToIndexOfParamToAnnotation(Endpoint.class);
   private final Map<Method, Map<Integer, Set<Annotation>>> methodToindexOfParamToMatrixParamAnnotations = createMethodToIndexOfParamToAnnotation(MatrixParam.class);
   private final Map<Method, Map<Integer, Set<Annotation>>> methodToindexOfParamToQueryParamAnnotations = createMethodToIndexOfParamToAnnotation(QueryParam.class);
   private final Map<Method, Map<Integer, Set<Annotation>>> methodToindexOfParamToPathParamAnnotations = createMethodToIndexOfParamToAnnotation(PathParam.class);
   private final Map<Method, Map<Integer, Set<Annotation>>> methodToindexOfParamToPostParamAnnotations = createMethodToIndexOfParamToAnnotation(MapEntityParam.class);
   private final Map<Method, Map<Integer, Set<Annotation>>> methodToindexOfParamToParamParserAnnotations = createMethodToIndexOfParamToAnnotation(ParamParser.class);
   private final Map<MethodKey, Method> delegationMap = Maps.newHashMap();

   static Map<Method, Map<Integer, Set<Annotation>>> createMethodToIndexOfParamToAnnotation(
            final Class<? extends Annotation> annotation) {
      return new MapMaker().makeComputingMap(new Function<Method, Map<Integer, Set<Annotation>>>() {
         public Map<Integer, Set<Annotation>> apply(final Method method) {
            return new MapMaker().makeComputingMap(new GetAnnotationsForMethodParameterIndex(
                     method, annotation));
         }
      });
   }

   static class GetAnnotationsForMethodParameterIndex implements Function<Integer, Set<Annotation>> {
      private final Method method;
      private final Class<?> clazz;

      protected GetAnnotationsForMethodParameterIndex(Method method, Class<?> clazz) {
         this.method = method;
         this.clazz = clazz;
      }

      public Set<Annotation> apply(final Integer index) {
         Set<Annotation> keys = new HashSet<Annotation>();
         List<Annotation> parameterAnnotations = Lists.newArrayList(method
                  .getParameterAnnotations()[index]);
         Collection<Annotation> filtered = Collections2.filter(parameterAnnotations,
                  new Predicate<Annotation>() {
                     public boolean apply(Annotation input) {
                        return input.annotationType().equals(clazz);
                     }
                  });
         for (Annotation annotation : filtered) {
            keys.add(annotation);
         }
         return keys;
      }

   }

   private static final Class<? extends HttpRequestOptions[]> optionsVarArgsClass = new HttpRequestOptions[] {}
            .getClass();

   private final Map<Method, Set<Integer>> methodToIndexesOfOptions = new MapMaker()
            .makeComputingMap(new Function<Method, Set<Integer>>() {
               public Set<Integer> apply(final Method method) {
                  Set<Integer> toReturn = Sets.newHashSet();
                  for (int index = 0; index < method.getParameterTypes().length; index++) {
                     Class<?> type = method.getParameterTypes()[index];
                     if (HttpRequestOptions.class.isAssignableFrom(type)
                              || optionsVarArgsClass.isAssignableFrom(type))
                        toReturn.add(index);
                  }
                  return toReturn;
               }
            });

   private final ParseSax.Factory parserFactory;

   @VisibleForTesting
   public Function<HttpResponse, ?> createResponseParser(Method method,
            GeneratedHttpRequest<T> request, Object... args) {
      Function<HttpResponse, ?> transformer;
      Class<? extends HandlerWithResult<?>> handler = getXMLTransformerOrNull(method);
      if (handler != null) {
         transformer = parserFactory.create(injector.getInstance(handler));
      } else {
         transformer = injector.getInstance(getParserOrThrowException(method));
      }
      if (transformer instanceof InvocationContext) {
         ((InvocationContext) transformer).setContext(request);
      }
      return transformer;
   }

   @VisibleForTesting
   public Function<Exception, ?> createExceptionParserOrNullIfNotFound(Method method) {
      ExceptionParser annotation = method.getAnnotation(ExceptionParser.class);
      if (annotation != null) {
         return injector.getInstance(annotation.value());
      }
      return null;
   }

   @SuppressWarnings("unchecked")
   @Inject
   public RestAnnotationProcessor(Injector injector, ParseSax.Factory parserFactory,
            TypeLiteral<T> typeLiteral) {
      this.declaring = (Class<T>) typeLiteral.getRawType();
      this.injector = injector;
      this.parserFactory = parserFactory;
      seedCache(declaring);
      if (declaring.isAnnotationPresent(SkipEncoding.class)) {
         skipEncode = declaring.getAnnotation(SkipEncoding.class).value();
      } else {
         skipEncode = new char[] {};
      }
   }

   public Method getDelegateOrNull(Method in) {
      return delegationMap.get(new MethodKey(in));
   }

   private void seedCache(Class<?> declaring) {
      Set<Method> methods = Sets.newHashSet(declaring.getMethods());
      methods = Sets.difference(methods, Sets.newHashSet(Object.class.getMethods()));
      for (Method method : methods) {
         if (isHttpMethod(method)) {
            for (int index = 0; index < method.getParameterTypes().length; index++) {
               methodToIndexOfParamToDecoratorParamAnnotation.get(method).get(index);
               methodToIndexOfParamToHeaderParamAnnotations.get(method).get(index);
               methodToIndexOfParamToHostPrefixParamAnnotations.get(method).get(index);
               methodToindexOfParamToMatrixParamAnnotations.get(method).get(index);
               methodToindexOfParamToQueryParamAnnotations.get(method).get(index);
               methodToindexOfParamToEndpointAnnotations.get(method).get(index);
               methodToindexOfParamToPathParamAnnotations.get(method).get(index);
               methodToindexOfParamToPostParamAnnotations.get(method).get(index);
               methodToindexOfParamToParamParserAnnotations.get(method).get(index);
               methodToIndexesOfOptions.get(method);
            }
            delegationMap.put(new MethodKey(method), method);
         } else if (isConstantDeclaration(method)) {
            bindConstant(method);
         } else if (!method.getDeclaringClass().equals(declaring)) {
            logger.debug("skipping potentially overridden method %s", method);
         } else {
            throw new RuntimeException("Method is not annotated as either http or constant: "
                     + method);
         }
      }
   }

   public static class MethodKey {

      @Override
      public int hashCode() {
         final int prime = 31;
         int result = 1;
         result = prime * result + ((name == null) ? 0 : name.hashCode());
         result = prime * result + parameterCount;
         return result;
      }

      @Override
      public boolean equals(Object obj) {
         if (this == obj)
            return true;
         if (obj == null)
            return false;
         if (getClass() != obj.getClass())
            return false;
         MethodKey other = (MethodKey) obj;
         if (name == null) {
            if (other.name != null)
               return false;
         } else if (!name.equals(other.name))
            return false;
         if (parameterCount != other.parameterCount)
            return false;
         return true;
      }

      private final String name;
      private final int parameterCount;

      public MethodKey(Method method) {
         this.name = method.getName();
         this.parameterCount = method.getParameterTypes().length;
      }

   }

   final Injector injector;
   final char[] skipEncode;

   public GeneratedHttpRequest<T> createRequest(Method method, Object... args) {
      URI endpoint = getEndpointFor(method, args);

      String httpMethod = getHttpMethodOrConstantOrThrowException(method);

      UriBuilder builder = addHostPrefixIfPresent(endpoint, method, args);
      builder.path(declaring);
      builder.path(method);

      Multimap<String, String> tokenValues = encodeValues(getPathParamKeyValues(method, args),
               skipEncode);

      addQueryParams(builder, tokenValues.entries(), method, args);
      addMatrixParams(builder, tokenValues.entries(), method, args);

      Multimap<String, String> headers = buildHeaders(tokenValues.entries(), method, args);

      String stringEntity = null;
      HttpRequestOptions options = findOptionsIn(method, args);
      if (options != null) {
         injector.injectMembers(options);// TODO test case
         for (Entry<String, String> header : options.buildRequestHeaders().entries()) {
            headers.put(header.getKey(), replaceTokens(header.getValue(), tokenValues.entries()));
         }
         for (Entry<String, String> query : options.buildQueryParameters().entries()) {
            builder.queryParam(query.getKey(), replaceTokens(query.getValue(), tokenValues
                     .entries()));
         }
         for (Entry<String, String> matrix : options.buildMatrixParameters().entries()) {
            builder.matrixParam(matrix.getKey(), replaceTokens(matrix.getValue(), tokenValues
                     .entries()));
         }
         String pathSuffix = options.buildPathSuffix();
         if (pathSuffix != null) {
            builder.path(pathSuffix);
         }
         stringEntity = options.buildStringEntity();
         if (stringEntity != null) {
            headers.put(HttpHeaders.CONTENT_LENGTH, stringEntity.getBytes().length + "");
         }
      }

      URI endPoint;
      try {
         endPoint = builder.buildFromEncodedMap(convertUnsafe(tokenValues));
      } catch (IllegalArgumentException e) {
         throw new IllegalStateException(e);
      } catch (UriBuilderException e) {
         throw new IllegalStateException(e);
      }

      endPoint = replaceQuery(endPoint, endPoint.getQuery());

      GeneratedHttpRequest<T> request = new GeneratedHttpRequest<T>(httpMethod, endPoint, this,
               declaring, method, args);
      request.setHeaders(headers);
      addHostHeaderIfAnnotatedWithVirtualHost(headers, request.getEndpoint().getHost(), method);
      addFiltersIfAnnotated(method, request);
      if (stringEntity != null) {
         request.setEntity(stringEntity);
         if (headers.get(HttpHeaders.CONTENT_TYPE) != null)
            headers.put(HttpHeaders.CONTENT_TYPE, "application/unknown");
      }
      decorateRequest(request);
      return request;
   }

   @VisibleForTesting
   URI replaceQuery(URI endPoint, String query) {
      return replaceQuery(endPoint, query, skipEncode);
   }

   @VisibleForTesting
   static URI replaceQuery(URI endPoint, String query, char... skipEncode) {
      UriBuilder qbuilder = UriBuilder.fromUri(endPoint);
      String unencodedQuery = query == null ? null : unEncode(query, skipEncode);
      qbuilder.replaceQuery(unencodedQuery);
      endPoint = qbuilder.build();
      return endPoint;
   }

   private void addMatrixParams(UriBuilder builder, Collection<Entry<String, String>> tokenValues,
            Method method, Object... args) {
      if (declaring.isAnnotationPresent(MatrixParams.class)) {
         MatrixParams query = declaring.getAnnotation(MatrixParams.class);
         addMatrix(builder, query, tokenValues);
      }

      if (method.isAnnotationPresent(MatrixParams.class)) {
         MatrixParams query = method.getAnnotation(MatrixParams.class);
         addMatrix(builder, query, tokenValues);
      }

      for (Entry<String, String> query : getMatrixParamKeyValues(method, args).entries()) {
         builder.queryParam(query.getKey(), replaceTokens(query.getValue(), tokenValues));
      }
   }

   private void addQueryParams(UriBuilder builder, Collection<Entry<String, String>> tokenValues,
            Method method, Object... args) {
      if (declaring.isAnnotationPresent(QueryParams.class)) {
         QueryParams query = declaring.getAnnotation(QueryParams.class);
         addQuery(builder, query, tokenValues);
      }

      if (method.isAnnotationPresent(QueryParams.class)) {
         QueryParams query = method.getAnnotation(QueryParams.class);
         addQuery(builder, query, tokenValues);
      }

      for (Entry<String, String> query : getQueryParamKeyValues(method, args).entries()) {
         builder.queryParam(query.getKey(), replaceTokens(query.getValue(), tokenValues));
      }
   }

   private void addQuery(UriBuilder builder, QueryParams query,
            Collection<Entry<String, String>> tokenValues) {
      for (int i = 0; i < query.keys().length; i++) {
         if (query.values()[i].equals(QueryParams.NULL)) {
            builder.replaceQuery(query.keys()[i]);
         } else {
            builder.queryParam(query.keys()[i], replaceTokens(query.values()[i], tokenValues));
         }
      }
   }

   private void addMatrix(UriBuilder builder, MatrixParams matrix,
            Collection<Entry<String, String>> tokenValues) {
      for (int i = 0; i < matrix.keys().length; i++) {
         if (matrix.values()[i].equals(MatrixParams.NULL)) {
            builder.replaceMatrix(matrix.keys()[i]);
         } else {
            builder.matrixParam(matrix.keys()[i], replaceTokens(matrix.values()[i], tokenValues));
         }
      }
   }

   private void addFiltersIfAnnotated(Method method, HttpRequest request) {
      if (declaring.isAnnotationPresent(RequestFilters.class)) {
         for (Class<? extends HttpRequestFilter> clazz : declaring.getAnnotation(
                  RequestFilters.class).value()) {
            HttpRequestFilter instance = injector.getInstance(clazz);
            request.getFilters().add(instance);
            logger.trace("%s - adding filter  %s from annotation on %s", request, instance,
                     declaring.getName());
         }
      }
      if (method.isAnnotationPresent(RequestFilters.class)) {
         if (method.isAnnotationPresent(OverrideRequestFilters.class))
            request.getFilters().clear();
         for (Class<? extends HttpRequestFilter> clazz : method.getAnnotation(RequestFilters.class)
                  .value()) {
            HttpRequestFilter instance = injector.getInstance(clazz);
            request.getFilters().add(instance);
            logger.trace("%s - adding filter  %s from annotation on %s", request, instance, method
                     .getName());
         }
      }
   }

   @VisibleForTesting
   URI getEndpointInParametersOrNull(Method method, Object... args) {
      Map<Integer, Set<Annotation>> map = indexWithOnlyOneAnnotation(method, "@Endpoint",
               methodToindexOfParamToEndpointAnnotations);
      if (map.size() == 1 && args.length > 0) {
         Endpoint annotation = (Endpoint) map.values().iterator().next().iterator().next();
         int index = map.keySet().iterator().next();
         checkState(
                  annotation.value() == Endpoint.NONE.class,
                  String
                           .format(
                                    "@Endpoint annotation at index %d on method %s should not have a value() except Endpoint.NONE ",
                                    index, method));
         Object arg = checkNotNull(args[index], String.format("argument at index %d on method %s",
                  index, method));
         checkArgument(arg instanceof URI, String.format(
                  "argument at index %d must be a URI for method %s", index, method));
         return (URI) arg;
      }
      return null;
   }

   private UriBuilder addHostPrefixIfPresent(URI endpoint, Method method, Object... args) {
      Map<Integer, Set<Annotation>> map = indexWithOnlyOneAnnotation(method, "@HostPrefixParam",
               methodToIndexOfParamToHostPrefixParamAnnotations);
      UriBuilder builder = UriBuilder.fromUri(endpoint);
      if (map.size() == 1) {
         HostPrefixParam param = (HostPrefixParam) map.values().iterator().next().iterator().next();
         int index = map.keySet().iterator().next();

         String prefix = checkNotNull(args[index],
                  String.format("argument at index %d on method %s", index, method)).toString();
         checkArgument(!prefix.equals(""), String.format(
                  "argument at index %d must be a valid hostname for method %s", index, method));
         String joinOn = param.value();
         String host = endpoint.getHost();

         builder.host(prefix + joinOn + host);
      }
      return builder;
   }

   public static final TypeLiteral<Future<Boolean>> futureBooleanLiteral = new TypeLiteral<Future<Boolean>>() {
   };

   public static final TypeLiteral<Future<String>> futureStringLiteral = new TypeLiteral<Future<String>>() {
   };

   public static final TypeLiteral<Future<Void>> futureVoidLiteral = new TypeLiteral<Future<Void>>() {
   };
   public static final TypeLiteral<Future<URI>> futureURILiteral = new TypeLiteral<Future<URI>>() {
   };
   public static final TypeLiteral<Future<InputStream>> futureInputStreamLiteral = new TypeLiteral<Future<InputStream>>() {
   };

   public static Class<? extends Function<HttpResponse, ?>> getParserOrThrowException(Method method) {
      ResponseParser annotation = method.getAnnotation(ResponseParser.class);
      if (annotation == null) {
         if (method.getReturnType().equals(boolean.class)
                  || method.getReturnType().equals(Boolean.class)
                  || TypeLiteral.get(method.getGenericReturnType()).equals(futureBooleanLiteral)) {
            return ReturnTrueIf2xx.class;
         } else if (method.getReturnType().equals(String.class)
                  || TypeLiteral.get(method.getGenericReturnType()).equals(futureStringLiteral)) {
            return ReturnStringIf200.class;
         } else if (method.getReturnType().equals(void.class)
                  || TypeLiteral.get(method.getGenericReturnType()).equals(futureVoidLiteral)) {
            return ReturnVoidIf2xx.class;
         } else if (method.getReturnType().equals(URI.class)
                  || TypeLiteral.get(method.getGenericReturnType()).equals(futureURILiteral)) {
            return ParseURIFromListOrLocationHeaderIf20x.class;
         } else if (method.getReturnType().equals(InputStream.class)
                  || TypeLiteral.get(method.getGenericReturnType())
                           .equals(futureInputStreamLiteral)) {
            return ReturnInputStream.class;
         } else {
            throw new IllegalStateException(
                     "You must specify a ResponseTransformer annotation on: " + method.toString());
         }
      }
      return annotation.value();
   }

   public static Class<? extends HandlerWithResult<?>> getXMLTransformerOrNull(Method method) {
      XMLResponseParser annotation = method.getAnnotation(XMLResponseParser.class);
      if (annotation != null) {
         return annotation.value();
      }
      return null;
   }

   public org.jclouds.rest.MapBinder getMapEntityBinderOrNull(Method method, Object... args) {
      if (args != null) {
         for (Object arg : args) {
            if (arg instanceof Object[]) {
               Object[] postBinders = (Object[]) arg;
               if (postBinders.length == 0) {
               } else if (postBinders.length == 1) {
                  if (postBinders[0] instanceof org.jclouds.rest.MapBinder) {
                     org.jclouds.rest.MapBinder binder = (org.jclouds.rest.MapBinder) postBinders[0];
                     injector.injectMembers(binder);
                     return binder;
                  }
               } else {
                  if (postBinders[0] instanceof org.jclouds.rest.MapBinder) {
                     throw new IllegalArgumentException(
                              "we currently do not support multiple varargs postBinders in: "
                                       + method.getName());
                  }
               }
            } else if (arg instanceof org.jclouds.rest.MapBinder) {
               org.jclouds.rest.MapBinder binder = (org.jclouds.rest.MapBinder) arg;
               injector.injectMembers(binder);
               return binder;
            }
         }
      }
      MapBinder annotation = method.getAnnotation(MapBinder.class);
      if (annotation != null) {
         return injector.getInstance(annotation.value());
      }
      return null;
   }

   private Multimap<String, String> constants = HashMultimap.create();

   public boolean isHttpMethod(Method method) {
      return IsHttpMethod.getHttpMethods(method) != null;
   }

   public boolean isConstantDeclaration(Method method) {
      return method.isAnnotationPresent(PathParam.class) && method.isAnnotationPresent(Named.class);
   }

   public void bindConstant(Method method) {
      String key = method.getAnnotation(PathParam.class).value();
      String value = injector.getInstance(Key.get(String.class, method.getAnnotation(Named.class)));
      constants.put(key, value);
   }

   public String getHttpMethodOrConstantOrThrowException(Method method) {
      Set<String> httpMethods = IsHttpMethod.getHttpMethods(method);
      if (httpMethods == null || httpMethods.size() != 1) {
         throw new IllegalStateException(
                  "You must use at least one, but no more than one http method or pathparam annotation on: "
                           + method.toString());
      }
      return httpMethods.iterator().next();
   }

   public void addHostHeaderIfAnnotatedWithVirtualHost(Multimap<String, String> headers,
            String host, Method method) {
      if (declaring.isAnnotationPresent(VirtualHost.class)
               || method.isAnnotationPresent(VirtualHost.class)) {
         headers.put(HttpHeaders.HOST, host);
      }
   }

   public void decorateRequest(GeneratedHttpRequest<T> request) {
      org.jclouds.rest.MapBinder mapBinder = getMapEntityBinderOrNull(request.getJavaMethod(),
               request.getArgs());
      Map<String, String> mapParams = buildPostParams(request.getJavaMethod(), request.getArgs());
      // MapEntityBinder is only useful if there are parameters. We guard here in case the
      // MapEntityBinder is also an EntityBinder. If so, it can be used with or without
      // parameters.
      if (mapBinder != null) {
         mapBinder.bindToRequest(request, mapParams);
         return;
      }

      for (Entry<Integer, Set<Annotation>> entry : Maps.filterValues(
               methodToIndexOfParamToDecoratorParamAnnotation.get(request.getJavaMethod()),
               new Predicate<Set<Annotation>>() {
                  public boolean apply(Set<Annotation> input) {
                     return input.size() >= 1;
                  }
               }).entrySet()) {
         BinderParam entityAnnotation = (BinderParam) entry.getValue().iterator().next();
         Binder binder = injector.getInstance(entityAnnotation.value());
         Object input = request.getArgs()[entry.getKey()];
         if (input.getClass().isArray()) {
            Object[] entityArray = (Object[]) input;
            input = entityArray.length > 0 ? entityArray[0] : null;
         }
         Object oldEntity = request.getEntity();
         binder.bindToRequest(request, input);
         if (oldEntity != null && !oldEntity.equals(request.getEntity())) {
            throw new IllegalStateException(String.format(
                     "binder %s replaced the previous entity on request: %s", binder, request));
         }
      }
      if (request.getMethod().equals("PUT") && request.getEntity() == null) {
         request.getHeaders().replaceValues(HttpHeaders.CONTENT_LENGTH,
                  Collections.singletonList(0 + ""));
      }
   }

   protected Map<Integer, Set<Annotation>> indexWithOnlyOneAnnotation(Method method,
            String description, Map<Method, Map<Integer, Set<Annotation>>> toRefine) {
      Map<Integer, Set<Annotation>> indexToEntityAnnotation = Maps.filterValues(toRefine
               .get(method), new Predicate<Set<Annotation>>() {
         public boolean apply(Set<Annotation> input) {
            return input.size() == 1;
         }
      });

      if (indexToEntityAnnotation.size() > 1) {
         throw new IllegalStateException(String.format(
                  "You must not specify more than one %s annotation on: %s; found %s", description,
                  method.toString(), indexToEntityAnnotation));
      }
      return indexToEntityAnnotation;
   }

   private HttpRequestOptions findOptionsIn(Method method, Object... args) {
      for (int index : methodToIndexesOfOptions.get(method)) {
         if (args.length >= index + 1) {// accomodate varargs
            if (args[index] instanceof Object[]) {
               Object[] options = (Object[]) args[index];
               if (options.length == 0) {
               } else if (options.length == 1) {
                  if (options[0] instanceof HttpRequestOptions) {
                     HttpRequestOptions binder = (HttpRequestOptions) options[0];
                     injector.injectMembers(binder);
                     return binder;
                  }
               } else {
                  if (options[0] instanceof HttpRequestOptions) {
                     throw new IllegalArgumentException(
                              "we currently do not support multiple varargs options in: "
                                       + method.getName());
                  }
               }
            } else {
               return (HttpRequestOptions) args[index];
            }
         }
      }
      return null;
   }

   public Multimap<String, String> buildHeaders(Collection<Entry<String, String>> tokenValues,
            Method method, final Object... args) {
      Multimap<String, String> headers = HashMultimap.create();
      addHeaderIfAnnotationPresentOnMethod(headers, method, tokenValues);
      Map<Integer, Set<Annotation>> indexToHeaderParam = methodToIndexOfParamToHeaderParamAnnotations
               .get(method);
      for (Entry<Integer, Set<Annotation>> entry : indexToHeaderParam.entrySet()) {
         for (Annotation key : entry.getValue()) {
            String value = args[entry.getKey()].toString();
            value = replaceTokens(value, tokenValues);
            headers.put(((HeaderParam) key).value(), value);
         }
      }
      addProducesIfPresentOnTypeOrMethod(headers, method);
      addConsumesIfPresentOnTypeOrMethod(headers, method);
      return headers;
   }

   void addConsumesIfPresentOnTypeOrMethod(Multimap<String, String> headers, Method method) {
      if (declaring.isAnnotationPresent(Consumes.class)) {
         Consumes header = declaring.getAnnotation(Consumes.class);
         headers.replaceValues(HttpHeaders.ACCEPT, Arrays.asList(header.value()));
      }
      if (method.isAnnotationPresent(Consumes.class)) {
         Consumes header = method.getAnnotation(Consumes.class);
         headers.replaceValues(HttpHeaders.ACCEPT, Arrays.asList(header.value()));
      }
   }

   void addProducesIfPresentOnTypeOrMethod(Multimap<String, String> headers, Method method) {
      if (declaring.isAnnotationPresent(Produces.class)) {
         Produces header = declaring.getAnnotation(Produces.class);
         headers.replaceValues(HttpHeaders.CONTENT_TYPE, Arrays.asList(header.value()));
      }
      if (method.isAnnotationPresent(Produces.class)) {
         Produces header = method.getAnnotation(Produces.class);
         headers.replaceValues(HttpHeaders.CONTENT_TYPE, Arrays.asList(header.value()));
      }
   }

   public void addHeaderIfAnnotationPresentOnMethod(Multimap<String, String> headers,
            Method method, Collection<Entry<String, String>> tokenValues) {
      if (declaring.isAnnotationPresent(Headers.class)) {
         Headers header = declaring.getAnnotation(Headers.class);
         addHeader(headers, header, tokenValues);
      }
      if (method.isAnnotationPresent(Headers.class)) {
         Headers header = method.getAnnotation(Headers.class);
         addHeader(headers, header, tokenValues);
      }
   }

   private void addHeader(Multimap<String, String> headers, Headers header,
            Collection<Entry<String, String>> tokenValues) {
      for (int i = 0; i < header.keys().length; i++) {
         String value = header.values()[i];
         value = replaceTokens(value, tokenValues);
         headers.put(header.keys()[i], value);
      }

   }

   private String replaceTokens(String value, Collection<Entry<String, String>> tokenValues) {
      for (Entry<String, String> tokenValue : tokenValues) {
         value = value.replaceAll("\\{" + tokenValue.getKey() + "\\}", tokenValue.getValue());
      }
      return value;
   }

   private Map<String, String> convertUnsafe(Multimap<String, String> in) {
      Map<String, String> out = Maps.newHashMap();
      for (Entry<String, String> entry : in.entries()) {
         out.put(entry.getKey(), entry.getValue());
      }
      return out;
   }

   private Multimap<String, String> getPathParamKeyValues(Method method, Object... args) {
      Multimap<String, String> pathParamValues = HashMultimap.create();
      pathParamValues.putAll(constants);
      Map<Integer, Set<Annotation>> indexToPathParam = methodToindexOfParamToPathParamAnnotations
               .get(method);

      Map<Integer, Set<Annotation>> indexToParamExtractor = methodToindexOfParamToParamParserAnnotations
               .get(method);
      for (Entry<Integer, Set<Annotation>> entry : indexToPathParam.entrySet()) {
         for (Annotation key : entry.getValue()) {
            Set<Annotation> extractors = indexToParamExtractor.get(entry.getKey());
            String paramKey = ((PathParam) key).value();
            String paramValue;
            if (extractors != null && extractors.size() > 0) {
               ParamParser extractor = (ParamParser) extractors.iterator().next();
               paramValue = injector.getInstance(extractor.value()).apply(args[entry.getKey()]);
            } else {
               paramValue = args[entry.getKey()].toString();
            }
            pathParamValues.put(paramKey, paramValue);
         }
      }

      if (method.isAnnotationPresent(PathParam.class)
               && method.isAnnotationPresent(ParamParser.class)) {
         String paramKey = method.getAnnotation(PathParam.class).value();
         String paramValue = injector.getInstance(method.getAnnotation(ParamParser.class).value())
                  .apply(args);
         pathParamValues.put(paramKey, paramValue);

      }
      return pathParamValues;
   }

   private Multimap<String, String> encodeValues(Multimap<String, String> unencoded,
            final char... skipEncode) {
      Multimap<String, String> encoded = HashMultimap.create();
      for (Entry<String, String> entry : unencoded.entries()) {
         try {
            String value = URLEncoder.encode(entry.getValue(), "UTF-8");
            // Web browsers do not always handle '+' characters well, use the well-supported
            // '%20' instead.
            value = value.replaceAll("\\+", "%20");
            if (skipEncode.length > 0) {
               value = unEncode(value, skipEncode);
            }
            encoded.put(entry.getKey(), value);
         } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("jclouds only supports UTF-8", e);
         }
      }
      return encoded;
   }

   @VisibleForTesting
   static String unEncode(String value, final char... skipEncode) {
      for (char c : skipEncode) {
         String toSkip = Character.toString(c);
         try {
            String encodedValueToSkip = URLEncoder.encode(toSkip, "UTF-8");
            value = value.replaceAll(encodedValueToSkip, toSkip);
         } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("jclouds only supports UTF-8", e);
         }
      }
      return value;
   }

   private Multimap<String, String> getMatrixParamKeyValues(Method method, Object... args) {
      Multimap<String, String> queryParamValues = HashMultimap.create();
      queryParamValues.putAll(constants);
      Map<Integer, Set<Annotation>> indexToMatrixParam = methodToindexOfParamToMatrixParamAnnotations
               .get(method);
      for (Entry<Integer, Set<Annotation>> entry : indexToMatrixParam.entrySet()) {
         for (Annotation key : entry.getValue()) {
            String paramKey = ((MatrixParam) key).value();
            String paramValue = args[entry.getKey()].toString();
            queryParamValues.put(paramKey, paramValue);
         }
      }
      return queryParamValues;
   }

   private Multimap<String, String> getQueryParamKeyValues(Method method, Object... args) {
      Multimap<String, String> queryParamValues = HashMultimap.create();
      queryParamValues.putAll(constants);
      Map<Integer, Set<Annotation>> indexToQueryParam = methodToindexOfParamToQueryParamAnnotations
               .get(method);
      for (Entry<Integer, Set<Annotation>> entry : indexToQueryParam.entrySet()) {
         for (Annotation key : entry.getValue()) {
            String paramKey = ((QueryParam) key).value();
            String paramValue = args[entry.getKey()].toString();
            queryParamValues.put(paramKey, paramValue);
         }
      }
      return queryParamValues;
   }

   private Map<String, String> buildPostParams(Method method, Object... args) {
      Map<String, String> postParams = Maps.newHashMap();
      Map<Integer, Set<Annotation>> indexToPathParam = methodToindexOfParamToPostParamAnnotations
               .get(method);
      Map<Integer, Set<Annotation>> indexToParamExtractor = methodToindexOfParamToParamParserAnnotations
               .get(method);
      for (Entry<Integer, Set<Annotation>> entry : indexToPathParam.entrySet()) {
         for (Annotation key : entry.getValue()) {
            Set<Annotation> extractors = indexToParamExtractor.get(entry.getKey());

            if (extractors != null && extractors.size() > 0) {
               ParamParser extractor = (ParamParser) extractors.iterator().next();
               postParams.put(((PathParam) key).value(), injector.getInstance(extractor.value())
                        .apply(args[entry.getKey()]));
            } else {
               String paramKey = ((MapEntityParam) key).value();
               String paramValue = args[entry.getKey()].toString();
               postParams.put(paramKey, paramValue);
            }
         }
      }
      return postParams;
   }

   public URI getEndpointFor(Method method, Object... args) {
      URI endpoint = getEndpointInParametersOrNull(method, args);
      if (endpoint == null) {
         Endpoint annotation;
         if (method.isAnnotationPresent(Endpoint.class)) {
            annotation = method.getAnnotation(Endpoint.class);
            checkState(annotation.value() != Endpoint.NONE.class, String.format(
                     "@Endpoint annotation at method %s must have a value() of valid Qualifier",
                     method));
         } else if (declaring.isAnnotationPresent(Endpoint.class)) {
            annotation = declaring.getAnnotation(Endpoint.class);
            checkState(annotation.value() != Endpoint.NONE.class, String.format(
                     "@Endpoint annotation at type %s must have a value() of valid Qualifier",
                     declaring));
         } else {
            throw new IllegalStateException(
                     "There must be an @Endpoint annotation on parameter, method or type: "
                              + method);
         }
         return injector.getInstance(Key.get(URI.class, annotation.value()));
      }
      return endpoint;
   }
}
