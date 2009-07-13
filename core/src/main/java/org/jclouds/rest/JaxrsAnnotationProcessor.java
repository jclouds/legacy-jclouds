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
package org.jclouds.rest;

import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.Future;

import javax.annotation.Resource;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriBuilder;

import org.jboss.resteasy.util.IsHttpMethod;
import org.jclouds.http.HttpMethod;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ReturnStringIf200;
import org.jclouds.http.functions.ReturnTrueIf2xx;
import org.jclouds.http.functions.ParseSax.HandlerWithResult;
import org.jclouds.http.options.HttpRequestOptions;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.internal.Lists;
import com.google.inject.name.Named;

/**
 * Tests behavior of JaxrsUtil
 * 
 * @author Adrian Cole
 */
@Singleton
public class JaxrsAnnotationProcessor {

   @Resource
   protected Logger logger = Logger.NULL;

   private final Class<?> declaring;

   private final Map<Method, Map<Integer, Set<Annotation>>> methodToIndexOfParamToEntityAnnotation = createMethodToIndexOfParamToAnnotation(EntityParam.class);
   private final Map<Method, Map<Integer, Set<Annotation>>> methodToIndexOfParamToHeaderParamAnnotations = createMethodToIndexOfParamToAnnotation(HeaderParam.class);
   private final Map<Method, Map<Integer, Set<Annotation>>> methodToIndexOfParamToHostPrefixParamAnnotations = createMethodToIndexOfParamToAnnotation(HostPrefixParam.class);
   private final Map<Method, Map<Integer, Set<Annotation>>> methodToindexOfParamToPathParamAnnotations = createMethodToIndexOfParamToAnnotation(PathParam.class);
   private final Map<Method, Map<Integer, Set<Annotation>>> methodToindexOfParamToPathParamParserAnnotations = createMethodToIndexOfParamToAnnotation(PathParamParser.class);

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

   private final Map<Method, Set<Integer>> methodToIndexesOfOptions = new MapMaker()
            .makeComputingMap(new Function<Method, Set<Integer>>() {
               public Set<Integer> apply(final Method method) {
                  Set<Integer> toReturn = Sets.newHashSet();
                  for (int index = 0; index < method.getParameterTypes().length; index++) {
                     Class<?> type = method.getParameterTypes()[index];
                     if (HttpRequestOptions.class.isAssignableFrom(type))
                        toReturn.add(index);
                  }
                  return toReturn;
               }
            });

   public static interface Factory {
      JaxrsAnnotationProcessor create(Class<?> declaring);
   }

   private final ParseSax.Factory parserFactory;

   Function<HttpResponse, ?> createResponseParser(Method method) {
      Function<HttpResponse, ?> transformer;
      Class<? extends HandlerWithResult<?>> handler = getXMLTransformerOrNull(method);
      if (handler != null) {
         transformer = parserFactory.create(injector.getInstance(handler));
      } else {
         transformer = injector.getInstance(getParserOrThrowException(method));
      }
      return transformer;
   }

   Function<Exception, ?> createExceptionParserOrNullIfNotFound(Method method) {
      ExceptionParser annotation = method.getAnnotation(ExceptionParser.class);
      if (annotation != null) {
         return injector.getInstance(annotation.value());
      }
      return null;
   }

   @Inject
   public JaxrsAnnotationProcessor(Injector injector, ParseSax.Factory parserFactory,
            @Assisted Class<?> declaring) {
      this.declaring = declaring;
      this.injector = injector;
      this.parserFactory = parserFactory;
      this.optionsBinder = injector.getInstance(HttpRequestOptionsBinder.class);
      seedCache(declaring);
   }

   private void seedCache(Class<?> declaring) {
      Set<Method> methods = Sets.newHashSet(declaring.getMethods());
      for (Method method : Sets.difference(methods, Sets.newHashSet(Object.class.getMethods()))) {
         if (isHttpMethod(method)) {
            for (int index = 0; index < method.getParameterTypes().length; index++) {
               methodToIndexOfParamToEntityAnnotation.get(method).get(index);
               methodToIndexOfParamToHeaderParamAnnotations.get(method).get(index);
               methodToIndexOfParamToHostPrefixParamAnnotations.get(method).get(index);
               methodToindexOfParamToPathParamAnnotations.get(method).get(index);
               methodToindexOfParamToPathParamParserAnnotations.get(method).get(index);
               methodToIndexesOfOptions.get(method);
            }
         } else if (isConstantDeclaration(method)) {
            bindConstant(method);
         } else {
            throw new RuntimeException("Method is not annotated as either http or constant");
         }
      }
   }

   final Injector injector;

   private HttpRequestOptionsBinder optionsBinder;

   public HttpRequest createRequest(URI endpoint, Method method, Object[] args) {
      HttpMethod httpMethod = getHttpMethodOrConstantOrThrowException(method);

      UriBuilder builder = addHostPrefixIfPresent(endpoint, method, args);
      builder.path(declaring);
      builder.path(method);

      if (method.isAnnotationPresent(Query.class)) {
         Query query = method.getAnnotation(Query.class);
         if (query.value().equals(Query.NULL))
            builder.replaceQuery(query.key());
         else
            builder.queryParam(query.key(), query.value());
      }

      Multimap<String, String> headers = buildHeaders(method, args);

      HttpRequestOptions options = findOptionsIn(method, args);
      if (options != null) {
         headers.putAll(options.buildRequestHeaders());
         for (Entry<String, String> query : options.buildQueryParameters().entries()) {
            builder.queryParam(query.getKey(), query.getValue());
         }
         for (Entry<String, String> matrix : options.buildMatrixParameters().entries()) {
            builder.matrixParam(matrix.getKey(), matrix.getValue());
         }
      }

      URI endPoint;
      try {
         addHeaderIfAnnotationPresentOnMethod(headers, method, args);
         if (declaring.isAnnotationPresent(SkipEncoding.class)) {
            endPoint = builder.buildFromEncodedMap(getEncodedPathParamKeyValues(method, args,
                     declaring.getAnnotation(SkipEncoding.class).value()));
         } else {
            endPoint = builder.buildFromEncodedMap(getEncodedPathParamKeyValues(method, args));
         }
      } catch (Exception e) {
         throw new IllegalStateException("problem encoding parameters", e);
      }
      HttpRequest request = new HttpRequest(httpMethod, endPoint, headers);
      addHostHeaderIfAnnotatedWithVirtualHost(headers, request.getEndpoint().getHost(), method);
      addFiltersIfAnnotated(method, request);

      buildEntityIfPostOrPutRequest(method, args, request);
      return request;
   }

   private void addFiltersIfAnnotated(Method method, HttpRequest request) {
      if (declaring.isAnnotationPresent(RequestFilters.class)) {
         for (Class<? extends HttpRequestFilter> clazz : declaring.getAnnotation(
                  RequestFilters.class).value()) {
            request.getFilters().add(injector.getInstance(clazz));
         }
      }
      if (method.isAnnotationPresent(RequestFilters.class)) {
         for (Class<? extends HttpRequestFilter> clazz : method.getAnnotation(RequestFilters.class)
                  .value()) {
            request.getFilters().add(injector.getInstance(clazz));
         }
      }
   }

   private UriBuilder addHostPrefixIfPresent(URI endpoint, Method method, Object[] args) {
      Map<Integer, Set<Annotation>> map = getIndexToHostPrefixAnnotation(method);
      UriBuilder builder = UriBuilder.fromUri(endpoint);
      if (map.size() == 1) {
         HostPrefixParam param = (HostPrefixParam) map.values().iterator().next().iterator().next();
         int index = map.keySet().iterator().next();

         String prefix = args[index].toString();
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

   private Map<String, String> constants = Maps.newHashMap();

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

   public HttpMethod getHttpMethodOrConstantOrThrowException(Method method) {
      Set<String> httpMethods = IsHttpMethod.getHttpMethods(method);
      if (httpMethods == null || httpMethods.size() != 1) {
         throw new IllegalStateException(
                  "You must use at least one, but no more than one http method or pathparam annotation on: "
                           + method.toString());
      }
      return HttpMethod.valueOf(httpMethods.iterator().next());
   }

   public void addHostHeaderIfAnnotatedWithVirtualHost(Multimap<String, String> headers,
            String host, Method method) {
      if (declaring.isAnnotationPresent(VirtualHost.class)
               || method.isAnnotationPresent(VirtualHost.class)) {
         headers.put(HttpHeaders.HOST, host);
      }
   }

   public HttpRequest buildEntityIfPostOrPutRequest(Method method, Object[] args,
            HttpRequest request) {
      switch (request.getMethod()) {
         case PUT:
         case POST:

            HttpRequestOptions options = findOptionsIn(method, args);
            if (options != null) {
               optionsBinder.addEntityToRequest(options, request);
            }
            if (request.getEntity() == null) {

               Map<Integer, Set<Annotation>> indexToEntityAnnotation = getIndexToEntityAnnotation(method);

               if (indexToEntityAnnotation.size() == 1) {
                  Entry<Integer, Set<Annotation>> entry = indexToEntityAnnotation.entrySet()
                           .iterator().next();
                  EntityParam entityAnnotation = (EntityParam) entry.getValue().iterator().next();

                  Object entity = args[entry.getKey()];
                  EntityBinder binder = injector.getInstance(entityAnnotation.value());

                  binder.addEntityToRequest(entity, request);
               } else if (indexToEntityAnnotation.size() > 1) {
                  throw new IllegalStateException("cannot have multiple @Entity annotations on "
                           + method);
               } else {
                  request.getHeaders().replaceValues(HttpHeaders.CONTENT_LENGTH,
                           Lists.newArrayList("0"));
               }
            }
            break;
      }
      return request;
   }

   private Map<Integer, Set<Annotation>> getIndexToEntityAnnotation(Method method) {
      Map<Integer, Set<Annotation>> indexToEntityAnnotation = Maps.filterValues(
               methodToIndexOfParamToEntityAnnotation.get(method),
               new Predicate<Set<Annotation>>() {
                  public boolean apply(Set<Annotation> input) {
                     return input.size() == 1;
                  }
               });

      if (indexToEntityAnnotation.size() > 1) {
         throw new IllegalStateException(String.format(
                  "You must not specify more than one @Entity annotation on: %s; found %s", method
                           .toString(), indexToEntityAnnotation));
      }
      return indexToEntityAnnotation;
   }

   private Map<Integer, Set<Annotation>> getIndexToHostPrefixAnnotation(Method method) {
      Map<Integer, Set<Annotation>> indexToHostPrefixAnnotation = Maps.filterValues(
               methodToIndexOfParamToHostPrefixParamAnnotations.get(method),
               new Predicate<Set<Annotation>>() {
                  public boolean apply(Set<Annotation> input) {
                     return input.size() == 1;
                  }
               });

      if (indexToHostPrefixAnnotation.size() > 1) {
         throw new IllegalStateException(
                  String
                           .format(
                                    "You must not specify more than one @HostPrefixParam annotation on: %s; found %s",
                                    method.toString(), indexToHostPrefixAnnotation));
      }
      return indexToHostPrefixAnnotation;
   }

   private HttpRequestOptions findOptionsIn(Method method, Object[] args) {
      for (int index : methodToIndexesOfOptions.get(method)) {
         return (HttpRequestOptions) args[index];
      }
      return null;
   }

   public Multimap<String, String> buildHeaders(Method method, final Object[] args) {
      Multimap<String, String> headers = HashMultimap.create();
      Map<Integer, Set<Annotation>> indexToHeaderParam = methodToIndexOfParamToHeaderParamAnnotations
               .get(method);
      for (Entry<Integer, Set<Annotation>> entry : indexToHeaderParam.entrySet()) {
         for (Annotation key : entry.getValue()) {
            headers.put(((HeaderParam) key).value(), args[entry.getKey()].toString());
         }
      }
      return headers;
   }

   public void addHeaderIfAnnotationPresentOnMethod(Multimap<String, String> headers,
            Method method, Object[] args, char... skipEncode) throws UnsupportedEncodingException {
      if (method.isAnnotationPresent(Header.class)) {
         Header header = method.getAnnotation(Header.class);
         String value = header.value();
         for (Entry<String, Object> tokenValue : getEncodedPathParamKeyValues(method, args)
                  .entrySet()) {
            value = value.replaceAll("\\{" + tokenValue.getKey() + "\\}", tokenValue.getValue()
                     .toString());
         }
         headers.put(header.key(), value);
      }
   }

   private Map<String, Object> getEncodedPathParamKeyValues(Method method, Object[] args,
            char... skipEncode) throws UnsupportedEncodingException {
      Map<String, Object> pathParamValues = Maps.newHashMap();
      pathParamValues.putAll(constants);
      Map<Integer, Set<Annotation>> indexToPathParam = methodToindexOfParamToPathParamAnnotations
               .get(method);
      Map<Integer, Set<Annotation>> indexToPathParamExtractor = methodToindexOfParamToPathParamParserAnnotations
               .get(method);
      for (Entry<Integer, Set<Annotation>> entry : indexToPathParam.entrySet()) {
         for (Annotation key : entry.getValue()) {
            Set<Annotation> extractors = indexToPathParamExtractor.get(entry.getKey());

            if (extractors != null && extractors.size() > 0) {
               PathParamParser extractor = (PathParamParser) extractors.iterator().next();
               pathParamValues.put(((PathParam) key).value(), injector.getInstance(
                        extractor.value()).apply(args[entry.getKey()]));
            } else {
               String paramKey = ((PathParam) key).value();
               String paramValue = URLEncoder.encode(args[entry.getKey()].toString(), "UTF-8");
               for (char c : skipEncode) {
                  String value = Character.toString(c);
                  String encoded = URLEncoder.encode(value, "UTF-8");
                  paramValue = paramValue.replaceAll(encoded, value);
               }
               pathParamValues.put(paramKey, paramValue);
            }
         }
      }
      return pathParamValues;
   }
}
