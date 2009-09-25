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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

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
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriBuilder;

import org.jboss.resteasy.util.IsHttpMethod;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ReturnStringIf200;
import org.jclouds.http.functions.ReturnTrueIf2xx;
import org.jclouds.http.functions.ParseSax.HandlerWithResult;
import org.jclouds.http.options.HttpRequestOptions;
import org.jclouds.logging.Logger;
import org.jclouds.rest.binders.EntityBinder;
import org.jclouds.rest.binders.HttpRequestOptionsBinder;
import org.jclouds.rest.binders.MapEntityBinder;

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
import com.google.inject.assistedinject.Assisted;
import com.google.inject.internal.Lists;

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

   public static interface Factory {
      JaxrsAnnotationProcessor create(Class<?> declaring);
   }

   private final ParseSax.Factory parserFactory;

   @VisibleForTesting
   public Function<HttpResponse, ?> createResponseParser(Method method) {
      Function<HttpResponse, ?> transformer;
      Class<? extends HandlerWithResult<?>> handler = getXMLTransformerOrNull(method);
      if (handler != null) {
         transformer = parserFactory.create(injector.getInstance(handler));
      } else {
         transformer = injector.getInstance(getParserOrThrowException(method));
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

   @Inject
   public JaxrsAnnotationProcessor(Injector injector, ParseSax.Factory parserFactory,
            @Assisted Class<?> declaring) {
      this.declaring = declaring;
      this.injector = injector;
      this.parserFactory = parserFactory;
      this.optionsBinder = injector.getInstance(HttpRequestOptionsBinder.class);
      seedCache(declaring);
   }

   protected Method getDelegateOrNull(Method in) {
      return delegationMap.get(new MethodKey(in));
   }

   private void seedCache(Class<?> declaring) {
      Set<Method> methods = Sets.newHashSet(declaring.getMethods());
      methods = Sets.difference(methods, Sets.newHashSet(Object.class.getMethods()));
      for (Method method : methods) {
         if (isHttpMethod(method)) {
            for (int index = 0; index < method.getParameterTypes().length; index++) {
               methodToIndexOfParamToEntityAnnotation.get(method).get(index);
               methodToIndexOfParamToHeaderParamAnnotations.get(method).get(index);
               methodToIndexOfParamToHostPrefixParamAnnotations.get(method).get(index);
               methodToindexOfParamToPathParamAnnotations.get(method).get(index);
               methodToindexOfParamToPostParamAnnotations.get(method).get(index);
               methodToindexOfParamToParamParserAnnotations.get(method).get(index);
               methodToIndexesOfOptions.get(method);
            }
            delegationMap.put(new MethodKey(method), method);
         } else if (isConstantDeclaration(method)) {
            bindConstant(method);
         } else if (!method.getDeclaringClass().equals(declaring)) {
            logger.debug("skipping potentially overridden method", method);
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

   private HttpRequestOptionsBinder optionsBinder;

   public HttpRequest createRequest(Method method, Object[] args) {
      URI endpoint = getEndpointFor(method);

      String httpMethod = getHttpMethodOrConstantOrThrowException(method);

      UriBuilder builder = addHostPrefixIfPresent(endpoint, method, args);
      builder.path(declaring);
      builder.path(method);

      if (declaring.isAnnotationPresent(QueryParams.class)) {
         QueryParams query = declaring.getAnnotation(QueryParams.class);
         addQuery(builder, query);
      }

      if (method.isAnnotationPresent(QueryParams.class)) {
         QueryParams query = method.getAnnotation(QueryParams.class);
         addQuery(builder, query);
      }

      Multimap<String, String> headers = buildHeaders(method, args);

      HttpRequestOptions options = findOptionsIn(method, args);
      if (options != null) {
         injector.injectMembers(options);// TODO test case
         headers.putAll(options.buildRequestHeaders());
         for (Entry<String, String> query : options.buildQueryParameters().entries()) {
            builder.queryParam(query.getKey(), query.getValue());
         }
         for (Entry<String, String> matrix : options.buildMatrixParameters().entries()) {
            builder.matrixParam(matrix.getKey(), matrix.getValue());
         }
         String pathSuffix = options.buildPathSuffix();
         if (pathSuffix != null) {
            builder.path(pathSuffix);
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

   private void addQuery(UriBuilder builder, QueryParams query) {
      for (int i = 0; i < query.keys().length; i++) {
         if (query.values()[i].equals(QueryParams.NULL)) {
            builder.replaceQuery(query.keys()[i]);
         } else {
            builder.queryParam(query.keys()[i], query.values()[i]);
         }
      }
   }

   private void addFiltersIfAnnotated(Method method, HttpRequest request) {
      if (declaring.isAnnotationPresent(RequestFilters.class)) {
         for (Class<? extends HttpRequestFilter> clazz : declaring.getAnnotation(
                  RequestFilters.class).value()) {
            HttpRequestFilter instance = injector.getInstance(clazz);
            request.getFilters().add(instance);
            logger.debug("%s - adding filter  %s from annotation on %s", request, instance,
                     declaring.getName());
         }
      }
      if (method.isAnnotationPresent(RequestFilters.class)) {
         for (Class<? extends HttpRequestFilter> clazz : method.getAnnotation(RequestFilters.class)
                  .value()) {
            HttpRequestFilter instance = injector.getInstance(clazz);
            request.getFilters().add(instance);
            logger.debug("%s - adding filter  %s from annotation on %s", request, instance, method
                     .getName());
         }
      }
   }

   private UriBuilder addHostPrefixIfPresent(URI endpoint, Method method, Object[] args) {
      Map<Integer, Set<Annotation>> map = getIndexToHostPrefixAnnotation(method);
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

   public MapEntityBinder getMapEntityBinderOrNull(Method method, Object[] args) {
      if (args != null) {
         for (Object arg : args) {
            if (arg instanceof Object[]) {
               Object[] postBinders = (Object[]) arg;
               if (postBinders.length == 0) {
               } else if (postBinders.length == 1) {
                  if (postBinders[0] instanceof MapEntityBinder) {
                     MapEntityBinder binder = (MapEntityBinder) postBinders[0];
                     injector.injectMembers(binder);
                     return binder;
                  }
               } else {
                  if (postBinders[0] instanceof MapEntityBinder) {
                     throw new IllegalArgumentException(
                              "we currently do not support multiple varargs postBinders in: "
                                       + method.getName());
                  }
               }
            } else if (arg instanceof MapEntityBinder) {
               MapEntityBinder binder = (MapEntityBinder) arg;
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

   public HttpRequest buildEntityIfPostOrPutRequest(Method method, Object[] args,
            HttpRequest request) {
      OUTER: if (request.getMethod().toUpperCase().equals("POST")
               || request.getMethod().toUpperCase().equals("PUT")) {
         MapEntityBinder mapBinder = getMapEntityBinderOrNull(method, args);
         Map<String, String> mapParams = buildPostParams(method, args);
         // MapEntityBinder is only useful if there are parameters. We guard here in case the
         // MapEntityBinder is also an EntityBinder. If so, it can be used with or without
         // parameters.
         if (mapBinder != null) {
            mapBinder.addEntityToRequest(mapParams, request);
            break OUTER;
         }
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
      if (declaring.isAnnotationPresent(Headers.class)) {
         Headers header = declaring.getAnnotation(Headers.class);
         addHeader(headers, method, args, header);
      }
      if (method.isAnnotationPresent(Headers.class)) {
         Headers header = method.getAnnotation(Headers.class);
         addHeader(headers, method, args, header);
      }
   }

   private void addHeader(Multimap<String, String> headers, Method method, Object[] args,
            Headers header) throws UnsupportedEncodingException {
      for (int i = 0; i < header.keys().length; i++) {
         String value = header.values()[i];
         for (Entry<String, Object> tokenValue : getEncodedPathParamKeyValues(method, args)
                  .entrySet()) {
            value = value.replaceAll("\\{" + tokenValue.getKey() + "\\}", tokenValue.getValue()
                     .toString());
         }
         headers.put(header.keys()[i], value);
      }

   }

   private Map<String, Object> getEncodedPathParamKeyValues(Method method, Object[] args,
            char... skipEncode) throws UnsupportedEncodingException {
      Map<String, Object> pathParamValues = Maps.newHashMap();
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
            paramValue = URLEncoder.encode(paramValue, "UTF-8");
            // Web browsers do not always handle '+' characters well, use the well-supported
            // '%20' instead.
            paramValue = paramValue.replaceAll("\\+", "%20");
            for (char c : skipEncode) {
               String value = Character.toString(c);
               String encoded = URLEncoder.encode(value, "UTF-8");
               paramValue = paramValue.replaceAll(encoded, value);
            }
            pathParamValues.put(paramKey, paramValue);
         }
      }
      return pathParamValues;
   }

   private Map<String, String> buildPostParams(Method method, Object[] args) {
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

   public URI getEndpointFor(Method method) {
      Endpoint endpoint;
      if (method.isAnnotationPresent(Endpoint.class)) {
         endpoint = method.getAnnotation(Endpoint.class);
      } else if (declaring.isAnnotationPresent(Endpoint.class)) {
         endpoint = declaring.getAnnotation(Endpoint.class);
      } else {
         throw new IllegalStateException(
                  "There must be an @Endpoint annotation on type or method: " + method);
      }
      return injector.getInstance(Key.get(URI.class, endpoint.value()));
   }
}
