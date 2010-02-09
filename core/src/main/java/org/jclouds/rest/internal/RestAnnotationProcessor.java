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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.util.Utils.replaceTokens;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.Map.Entry;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriBuilderException;

import org.jboss.resteasy.util.IsHttpMethod;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpUtils;
import org.jclouds.http.functions.CloseContentAndReturn;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ParseURIFromListOrLocationHeaderIf20x;
import org.jclouds.http.functions.ReturnInputStream;
import org.jclouds.http.functions.ReturnStringIf200;
import org.jclouds.http.functions.ReturnTrueIf2xx;
import org.jclouds.http.functions.ParseSax.HandlerWithResult;
import org.jclouds.http.options.HttpRequestOptions;
import org.jclouds.logging.Logger;
import org.jclouds.rest.Binder;
import org.jclouds.rest.InvocationContext;
import org.jclouds.rest.InputParamValidator;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Endpoint;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.FormParams;
import org.jclouds.rest.annotations.Headers;
import org.jclouds.rest.annotations.HostPrefixParam;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.MapPayloadParam;
import org.jclouds.rest.annotations.MatrixParams;
import org.jclouds.rest.annotations.OverrideRequestFilters;
import org.jclouds.rest.annotations.ParamParser;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.rest.annotations.VirtualHost;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.rest.functions.MapHttp4xxCodesToExceptions;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.internal.Nullable;

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
   private final Map<Method, Map<Integer, Set<Annotation>>> methodToIndexOfParamToEndpointAnnotations = createMethodToIndexOfParamToAnnotation(Endpoint.class);
   private final Map<Method, Map<Integer, Set<Annotation>>> methodToIndexOfParamToEndpointParamAnnotations = createMethodToIndexOfParamToAnnotation(EndpointParam.class);
   private final Map<Method, Map<Integer, Set<Annotation>>> methodToIndexOfParamToMatrixParamAnnotations = createMethodToIndexOfParamToAnnotation(MatrixParam.class);
   private final Map<Method, Map<Integer, Set<Annotation>>> methodToIndexOfParamToFormParamAnnotations = createMethodToIndexOfParamToAnnotation(FormParam.class);
   private final Map<Method, Map<Integer, Set<Annotation>>> methodToIndexOfParamToQueryParamAnnotations = createMethodToIndexOfParamToAnnotation(QueryParam.class);
   private final Map<Method, Map<Integer, Set<Annotation>>> methodToIndexOfParamToPathParamAnnotations = createMethodToIndexOfParamToAnnotation(PathParam.class);
   private final Map<Method, Map<Integer, Set<Annotation>>> methodToIndexOfParamToPostParamAnnotations = createMethodToIndexOfParamToAnnotation(MapPayloadParam.class);
   private final Map<Method, Map<Integer, Set<Annotation>>> methodToIndexOfParamToParamParserAnnotations = createMethodToIndexOfParamToAnnotation(ParamParser.class);
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

   private char[] skips;

   @Inject
   private InputParamValidator inputParamValidator;

   @VisibleForTesting
   public Function<HttpResponse, ?> createResponseParser(Method method,
            GeneratedHttpRequest<T> request) {
      Function<HttpResponse, ?> transformer;
      Class<? extends HandlerWithResult<?>> handler = getSaxResponseParserClassOrNull(method);
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
   public Function<Exception, ?> createExceptionParserOrThrowResourceNotFoundOn404IfNoAnnotation(
            Method method) {
      ExceptionParser annotation = method.getAnnotation(ExceptionParser.class);
      if (annotation != null) {
         return injector.getInstance(annotation.value());
      }
      return injector.getInstance(MapHttp4xxCodesToExceptions.class);
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
         skips = declaring.getAnnotation(SkipEncoding.class).value();
      } else {
         skips = new char[] {};
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
               methodToIndexOfParamToMatrixParamAnnotations.get(method).get(index);
               methodToIndexOfParamToFormParamAnnotations.get(method).get(index);
               methodToIndexOfParamToQueryParamAnnotations.get(method).get(index);
               methodToIndexOfParamToEndpointAnnotations.get(method).get(index);
               methodToIndexOfParamToEndpointParamAnnotations.get(method).get(index);
               methodToIndexOfParamToPathParamAnnotations.get(method).get(index);
               methodToIndexOfParamToPostParamAnnotations.get(method).get(index);
               methodToIndexOfParamToParamParserAnnotations.get(method).get(index);
               methodToIndexesOfOptions.get(method);
            }
            delegationMap.put(new MethodKey(method), method);
         } else if (isConstantDeclaration(method)) {
            bindConstant(method);
         } else if (!method.getDeclaringClass().equals(declaring)) {
            logger.debug("skipping potentially overridden method %s", method);
         } else if (!method.getName().startsWith("new")) {
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

   public GeneratedHttpRequest<T> createRequest(Method method, Object... args) {
      inputParamValidator.validateMethodParametersOrThrow(method, args);

      URI endpoint = getEndpointFor(method, args);

      String httpMethod = getHttpMethodOrConstantOrThrowException(method);

      UriBuilder builder = addHostPrefixIfPresent(endpoint, method, args);
      if (declaring.isAnnotationPresent(Path.class))
         builder.path(declaring);
      builder.path(method);

      Multimap<String, String> tokenValues = encodeValues(getPathParamKeyValues(method, args),
               skips);

      Multimap<String, String> formParams = addFormParams(tokenValues.entries(), method, args);
      Multimap<String, String> queryParams = addQueryParams(tokenValues.entries(), method, args);

      addMatrixParams(builder, tokenValues.entries(), method, args);

      Multimap<String, String> headers = buildHeaders(tokenValues.entries(), method, args);

      String stringPayload = null;
      HttpRequestOptions options = findOptionsIn(method, args);
      if (options != null) {
         injector.injectMembers(options);// TODO test case
         for (Entry<String, String> header : options.buildRequestHeaders().entries()) {
            headers.put(header.getKey(), replaceTokens(header.getValue(), tokenValues.entries()));
         }
         for (Entry<String, String> matrix : options.buildMatrixParameters().entries()) {
            builder.matrixParam(matrix.getKey(), replaceTokens(matrix.getValue(), tokenValues
                     .entries()));
         }
         for (Entry<String, String> query : options.buildQueryParameters().entries()) {
            queryParams.put(query.getKey(), replaceTokens(query.getValue(), tokenValues.entries()));
         }
         for (Entry<String, String> form : options.buildFormParameters().entries()) {
            formParams.put(form.getKey(), replaceTokens(form.getValue(), tokenValues.entries()));
         }

         String pathSuffix = options.buildPathSuffix();
         if (pathSuffix != null) {
            builder.path(pathSuffix);
         }
         stringPayload = options.buildStringPayload();
      }

      if (queryParams.size() > 0) {
         builder.replaceQuery(makeQueryLine(queryParams, null, skips));
      }

      try {
         endpoint = builder.buildFromEncodedMap(convertUnsafe(tokenValues));
      } catch (IllegalArgumentException e) {
         throw new IllegalStateException(e);
      } catch (UriBuilderException e) {
         throw new IllegalStateException(e);
      }

      GeneratedHttpRequest<T> request = new GeneratedHttpRequest<T>(httpMethod, endpoint, this,
               declaring, method, args);
      addHostHeaderIfAnnotatedWithVirtualHost(headers, request.getEndpoint().getHost(), method);
      addFiltersIfAnnotated(method, request);

      if (formParams.size() > 0) {
         if (headers.get(HttpHeaders.CONTENT_TYPE) != null)
            headers.put(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED);
         request.setPayload(makeQueryLine(formParams, null, skips));
      }

      if (stringPayload != null) {
         request.setPayload(stringPayload);
         if (headers.get(HttpHeaders.CONTENT_LENGTH) != null)
            headers.put(HttpHeaders.CONTENT_LENGTH, stringPayload.getBytes().length + "");
         if (headers.get(HttpHeaders.CONTENT_TYPE) != null)
            headers.put(HttpHeaders.CONTENT_TYPE, "application/unknown");
      }
      request.getHeaders().putAll(headers);
      decorateRequest(request);
      return request;
   }

   public URI replaceQuery(URI in, String newQuery,
            @Nullable Comparator<Entry<String, String>> sorter) {
      return replaceQuery(in, newQuery, sorter, skips);
   }

   public static URI replaceQuery(URI in, String newQuery,
            @Nullable Comparator<Entry<String, String>> sorter, char... skips) {
      UriBuilder builder = UriBuilder.fromUri(in);
      builder.replaceQuery(makeQueryLine(parseQueryToMap(newQuery), sorter, skips));
      return builder.build();
   }

   public URI addQueryParam(URI in, String key, String[] values) {
      return addQueryParam(in, key, values, skips);
   }

   public static URI addQueryParam(URI in, String key, String[] values, char... skips) {
      UriBuilder builder = UriBuilder.fromUri(in);
      Multimap<String, String> map = parseQueryToMap(in.getQuery());
      map.putAll(key, Arrays.asList(values));
      builder.replaceQuery(makeQueryLine(map, null, skips));
      return builder.build();
   }

   public String addFormParam(String in, String key, String[] values) {
      return addFormParam(in, key, values, skips);
   }

   public static String addFormParam(String in, String key, String[] values, char... skips) {
      Multimap<String, String> map = parseQueryToMap(in);
      map.putAll(key, Arrays.asList(values));
      return makeQueryLine(map, null, skips);
   }

   public static Multimap<String, String> parseQueryToMap(String in) {
      Multimap<String, String> map = LinkedListMultimap.create();
      if (in == null) {
      } else if (in.indexOf('&') == -1) {
         map.put(in, null);
      } else {
         String[] parts = HttpUtils.urlDecode(in).split("&");
         for (int partIndex = 0; partIndex < parts.length; partIndex++) {
            // note that '=' can be a valid part of the value
            int indexOfFirstEquals = parts[partIndex].indexOf('=');
            String key = indexOfFirstEquals == -1 ? parts[partIndex] : parts[partIndex].substring(
                     0, indexOfFirstEquals);
            String value = indexOfFirstEquals == -1 ? null : parts[partIndex]
                     .substring(indexOfFirstEquals + 1);
            map.put(key, value);
         }
      }
      return map;
   }

   public static SortedSet<Entry<String, String>> sortEntries(
            Collection<Map.Entry<String, String>> in, Comparator<Map.Entry<String, String>> sorter) {
      SortedSet<Entry<String, String>> entries = Sets.newTreeSet(sorter);
      entries.addAll(in);
      return entries;
   }

   public static String makeQueryLine(Multimap<String, String> params,
            @Nullable Comparator<Map.Entry<String, String>> sorter, char... skips) {

      Iterator<Map.Entry<String, String>> pairs = ((sorter == null) ? params.entries()
               : sortEntries(params.entries(), sorter)).iterator();
      StringBuilder formBuilder = new StringBuilder();
      while (pairs.hasNext()) {
         Map.Entry<String, String> pair = pairs.next();
         formBuilder.append(HttpUtils.urlEncode(pair.getKey(), skips));
         if (pair.getValue() != null && !pair.getValue().equals("")) {
            formBuilder.append("=");
            formBuilder.append(HttpUtils.urlEncode(pair.getValue(), skips));
         }
         if (pairs.hasNext())
            formBuilder.append("&");
      }
      return formBuilder.toString();
   }

   private void addMatrixParams(UriBuilder builder, Collection<Entry<String, String>> tokenValues,
            Method method, Object... args) {
      if (declaring.isAnnotationPresent(MatrixParams.class)) {
         MatrixParams matrix = declaring.getAnnotation(MatrixParams.class);
         addMatrix(builder, matrix, tokenValues);
      }

      if (method.isAnnotationPresent(MatrixParams.class)) {
         MatrixParams matrix = method.getAnnotation(MatrixParams.class);
         addMatrix(builder, matrix, tokenValues);
      }

      for (Entry<String, String> matrix : getMatrixParamKeyValues(method, args).entries()) {
         builder.matrixParam(matrix.getKey(), replaceTokens(matrix.getValue(), tokenValues));
      }
   }

   private Multimap<String, String> addFormParams(Collection<Entry<String, String>> tokenValues,
            Method method, Object... args) {
      Multimap<String, String> formMap = LinkedListMultimap.create();
      if (declaring.isAnnotationPresent(FormParams.class)) {
         FormParams form = declaring.getAnnotation(FormParams.class);
         addForm(formMap, form, tokenValues);
      }

      if (method.isAnnotationPresent(FormParams.class)) {
         FormParams form = method.getAnnotation(FormParams.class);
         addForm(formMap, form, tokenValues);
      }

      for (Entry<String, String> form : getFormParamKeyValues(method, args).entries()) {
         formMap.put(form.getKey(), replaceTokens(form.getValue(), tokenValues));
      }
      return formMap;
   }

   private Multimap<String, String> addQueryParams(Collection<Entry<String, String>> tokenValues,
            Method method, Object... args) {
      Multimap<String, String> queryMap = LinkedListMultimap.create();
      if (declaring.isAnnotationPresent(QueryParams.class)) {
         QueryParams query = declaring.getAnnotation(QueryParams.class);
         addQuery(queryMap, query, tokenValues);
      }

      if (method.isAnnotationPresent(QueryParams.class)) {
         QueryParams query = method.getAnnotation(QueryParams.class);
         addQuery(queryMap, query, tokenValues);
      }

      for (Entry<String, String> query : getQueryParamKeyValues(method, args).entries()) {
         queryMap.put(query.getKey(), replaceTokens(query.getValue(), tokenValues));
      }
      return queryMap;
   }

   private void addForm(Multimap<String, String> formParams, FormParams form,
            Collection<Entry<String, String>> tokenValues) {
      for (int i = 0; i < form.keys().length; i++) {
         if (form.values()[i].equals(FormParams.NULL)) {
            formParams.removeAll(form.keys()[i]);
            formParams.put(form.keys()[i], null);
         } else {
            formParams.put(form.keys()[i], replaceTokens(form.values()[i], tokenValues));
         }
      }
   }

   private void addQuery(Multimap<String, String> queryParams, QueryParams query,
            Collection<Entry<String, String>> tokenValues) {
      for (int i = 0; i < query.keys().length; i++) {
         if (query.values()[i].equals(QueryParams.NULL)) {
            queryParams.removeAll(query.keys()[i]);
            queryParams.put(query.keys()[i], null);
         } else {
            queryParams.put(query.keys()[i], replaceTokens(query.values()[i], tokenValues));
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
      Map<Integer, Set<Annotation>> map = indexWithOnlyOneAnnotation(method, "@EndpointParam",
               methodToIndexOfParamToEndpointParamAnnotations);
      if (map.size() == 1 && args.length > 0) {
         EndpointParam annotation = (EndpointParam) map.values().iterator().next().iterator()
                  .next();
         int index = map.keySet().iterator().next();
         Function<Object, URI> parser = injector.getInstance(annotation.parser());
         Object arg = checkNotNull(args[index], String.format("argument at index %d on method %s",
                  index, method));
         return parser.apply(arg);
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

   public static final TypeLiteral<ListenableFuture<Boolean>> futureBooleanLiteral = new TypeLiteral<ListenableFuture<Boolean>>() {
   };

   public static final TypeLiteral<ListenableFuture<String>> futureStringLiteral = new TypeLiteral<ListenableFuture<String>>() {
   };

   public static final TypeLiteral<ListenableFuture<Void>> futureVoidLiteral = new TypeLiteral<ListenableFuture<Void>>() {
   };
   public static final TypeLiteral<ListenableFuture<URI>> futureURILiteral = new TypeLiteral<ListenableFuture<URI>>() {
   };
   public static final TypeLiteral<ListenableFuture<InputStream>> futureInputStreamLiteral = new TypeLiteral<ListenableFuture<InputStream>>() {
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
            return CloseContentAndReturn.class;
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

   public static Class<? extends HandlerWithResult<?>> getSaxResponseParserClassOrNull(Method method) {
      XMLResponseParser annotation = method.getAnnotation(XMLResponseParser.class);
      if (annotation != null) {
         return annotation.value();
      }
      return null;
   }

   public org.jclouds.rest.MapBinder getMapPayloadBinderOrNull(Method method, Object... args) {
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

   private Multimap<String, String> constants = LinkedHashMultimap.create();

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
      org.jclouds.rest.MapBinder mapBinder = getMapPayloadBinderOrNull(request.getJavaMethod(),
               request.getArgs());
      Map<String, String> mapParams = buildPostParams(request.getJavaMethod(), request.getArgs());
      // MapPayloadBinder is only useful if there are parameters. We guard here in case the
      // MapPayloadBinder is also an PayloadBinder. If so, it can be used with or without
      // parameters.
      if (mapBinder != null) {
         mapBinder.bindToRequest(request, mapParams);
         return;
      }

      OUTER: for (Entry<Integer, Set<Annotation>> entry : Maps.filterValues(
               methodToIndexOfParamToDecoratorParamAnnotation.get(request.getJavaMethod()),
               new Predicate<Set<Annotation>>() {
                  public boolean apply(Set<Annotation> input) {
                     return input.size() >= 1;
                  }
               }).entrySet()) {
         boolean shouldBreak = false;
         BinderParam payloadAnnotation = (BinderParam) entry.getValue().iterator().next();
         Binder binder = injector.getInstance(payloadAnnotation.value());
         if (request.getArgs().length >= entry.getKey() + 1
                  && request.getArgs()[entry.getKey()] != null) {
            Object input;
            Class<?> parameterType = request.getJavaMethod().getParameterTypes()[entry.getKey()];
            Class<? extends Object> argType = request.getArgs()[entry.getKey()].getClass();
            if (!argType.isArray() && request.getJavaMethod().isVarArgs()
                     && parameterType.isArray()) {
               int arrayLength = request.getArgs().length
                        - request.getJavaMethod().getParameterTypes().length + 1;
               if (arrayLength == 0)
                  break OUTER;
               input = (Object[]) Array.newInstance(request.getArgs()[entry.getKey()].getClass(),
                        arrayLength);
               System.arraycopy(request.getArgs(), entry.getKey(), input, 0, arrayLength);
               shouldBreak = true;
            } else if (argType.isArray() && request.getJavaMethod().isVarArgs()
                     && parameterType.isArray()) {
               input = request.getArgs()[entry.getKey()];
            } else {
               input = request.getArgs()[entry.getKey()];
               if (input.getClass().isArray()) {
                  Object[] payloadArray = (Object[]) input;
                  input = payloadArray.length > 0 ? payloadArray[0] : null;
               }
            }
            if (input != null) {
               binder.bindToRequest(request, input);
            }
            if (shouldBreak)
               break OUTER;
         }
      }
      if (request.getMethod().equals("PUT") && request.getPayload() == null) {
         request.getHeaders().replaceValues(HttpHeaders.CONTENT_LENGTH,
                  Collections.singletonList(0 + ""));
      }
      if (request.getPayload() != null)
         assert request.getFirstHeaderOrNull(HttpHeaders.CONTENT_LENGTH) != null : "no content length";
   }

   protected Map<Integer, Set<Annotation>> indexWithOnlyOneAnnotation(Method method,
            String description, Map<Method, Map<Integer, Set<Annotation>>> toRefine) {
      Map<Integer, Set<Annotation>> indexToPayloadAnnotation = Maps.filterValues(toRefine
               .get(method), new Predicate<Set<Annotation>>() {
         public boolean apply(Set<Annotation> input) {
            return input.size() == 1;
         }
      });

      if (indexToPayloadAnnotation.size() > 1) {
         throw new IllegalStateException(String.format(
                  "You must not specify more than one %s annotation on: %s; found %s", description,
                  method.toString(), indexToPayloadAnnotation));
      }
      return indexToPayloadAnnotation;
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
      Multimap<String, String> headers = LinkedHashMultimap.create();
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

   private Map<String, String> convertUnsafe(Multimap<String, String> in) {
      Map<String, String> out = Maps.newLinkedHashMap();
      for (Entry<String, String> entry : in.entries()) {
         out.put(entry.getKey(), entry.getValue());
      }
      return out;
   }

   private Multimap<String, String> getPathParamKeyValues(Method method, Object... args) {
      Multimap<String, String> pathParamValues = LinkedHashMultimap.create();
      pathParamValues.putAll(constants);
      Map<Integer, Set<Annotation>> indexToPathParam = methodToIndexOfParamToPathParamAnnotations
               .get(method);

      Map<Integer, Set<Annotation>> indexToParamExtractor = methodToIndexOfParamToParamParserAnnotations
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

   private Multimap<String, String> encodeValues(Multimap<String, String> unencoded, char... skips) {
      Multimap<String, String> encoded = LinkedHashMultimap.create();
      for (Entry<String, String> entry : unencoded.entries()) {
         encoded.put(entry.getKey(), HttpUtils.urlEncode(entry.getValue(), skips));
      }
      return encoded;
   }

   private Multimap<String, String> getMatrixParamKeyValues(Method method, Object... args) {
      Multimap<String, String> matrixParamValues = LinkedHashMultimap.create();
      matrixParamValues.putAll(constants);
      Map<Integer, Set<Annotation>> indexToMatrixParam = methodToIndexOfParamToMatrixParamAnnotations
               .get(method);

      Map<Integer, Set<Annotation>> indexToParamExtractor = methodToIndexOfParamToParamParserAnnotations
               .get(method);
      for (Entry<Integer, Set<Annotation>> entry : indexToMatrixParam.entrySet()) {
         for (Annotation key : entry.getValue()) {
            Set<Annotation> extractors = indexToParamExtractor.get(entry.getKey());
            String paramKey = ((MatrixParam) key).value();
            String paramValue;
            if (extractors != null && extractors.size() > 0) {
               ParamParser extractor = (ParamParser) extractors.iterator().next();
               paramValue = injector.getInstance(extractor.value()).apply(args[entry.getKey()]);
            } else {
               paramValue = args[entry.getKey()].toString();
            }
            matrixParamValues.put(paramKey, paramValue);
         }
      }

      if (method.isAnnotationPresent(MatrixParam.class)
               && method.isAnnotationPresent(ParamParser.class)) {
         String paramKey = method.getAnnotation(MatrixParam.class).value();
         String paramValue = injector.getInstance(method.getAnnotation(ParamParser.class).value())
                  .apply(args);
         matrixParamValues.put(paramKey, paramValue);

      }
      return matrixParamValues;
   }

   private Multimap<String, String> getFormParamKeyValues(Method method, Object... args) {
      Multimap<String, String> formParamValues = LinkedHashMultimap.create();
      formParamValues.putAll(constants);
      Map<Integer, Set<Annotation>> indexToFormParam = methodToIndexOfParamToFormParamAnnotations
               .get(method);

      Map<Integer, Set<Annotation>> indexToParamExtractor = methodToIndexOfParamToParamParserAnnotations
               .get(method);
      for (Entry<Integer, Set<Annotation>> entry : indexToFormParam.entrySet()) {
         for (Annotation key : entry.getValue()) {
            Set<Annotation> extractors = indexToParamExtractor.get(entry.getKey());
            String paramKey = ((FormParam) key).value();
            String paramValue;
            if (extractors != null && extractors.size() > 0) {
               ParamParser extractor = (ParamParser) extractors.iterator().next();
               paramValue = injector.getInstance(extractor.value()).apply(args[entry.getKey()]);
            } else {
               paramValue = args[entry.getKey()].toString();
            }
            formParamValues.put(paramKey, paramValue);
         }
      }

      if (method.isAnnotationPresent(FormParam.class)
               && method.isAnnotationPresent(ParamParser.class)) {
         String paramKey = method.getAnnotation(FormParam.class).value();
         String paramValue = injector.getInstance(method.getAnnotation(ParamParser.class).value())
                  .apply(args);
         formParamValues.put(paramKey, paramValue);

      }
      return formParamValues;
   }

   private Multimap<String, String> getQueryParamKeyValues(Method method, Object... args) {
      Multimap<String, String> queryParamValues = LinkedHashMultimap.create();
      queryParamValues.putAll(constants);
      Map<Integer, Set<Annotation>> indexToQueryParam = methodToIndexOfParamToQueryParamAnnotations
               .get(method);

      Map<Integer, Set<Annotation>> indexToParamExtractor = methodToIndexOfParamToParamParserAnnotations
               .get(method);
      for (Entry<Integer, Set<Annotation>> entry : indexToQueryParam.entrySet()) {
         for (Annotation key : entry.getValue()) {
            Set<Annotation> extractors = indexToParamExtractor.get(entry.getKey());
            String paramKey = ((QueryParam) key).value();
            String paramValue;
            if (extractors != null && extractors.size() > 0) {
               ParamParser extractor = (ParamParser) extractors.iterator().next();
               paramValue = injector.getInstance(extractor.value()).apply(args[entry.getKey()]);
            } else {
               paramValue = args[entry.getKey()].toString();
            }
            queryParamValues.put(paramKey, paramValue);
         }
      }

      if (method.isAnnotationPresent(QueryParam.class)
               && method.isAnnotationPresent(ParamParser.class)) {
         String paramKey = method.getAnnotation(QueryParam.class).value();
         String paramValue = injector.getInstance(method.getAnnotation(ParamParser.class).value())
                  .apply(args);
         queryParamValues.put(paramKey, paramValue);

      }
      return queryParamValues;
   }

   private Map<String, String> buildPostParams(Method method, Object... args) {
      Map<String, String> postParams = Maps.newHashMap();
      Map<Integer, Set<Annotation>> indexToPathParam = methodToIndexOfParamToPostParamAnnotations
               .get(method);
      Map<Integer, Set<Annotation>> indexToParamExtractor = methodToIndexOfParamToParamParserAnnotations
               .get(method);
      for (Entry<Integer, Set<Annotation>> entry : indexToPathParam.entrySet()) {
         for (Annotation key : entry.getValue()) {
            Set<Annotation> extractors = indexToParamExtractor.get(entry.getKey());
            String paramKey = ((MapPayloadParam) key).value();
            String paramValue;
            if (extractors != null && extractors.size() > 0) {
               ParamParser extractor = (ParamParser) extractors.iterator().next();
               paramValue = injector.getInstance(extractor.value()).apply(args[entry.getKey()]);

            } else {
               paramValue = args[entry.getKey()].toString();
            }
            postParams.put(paramKey, paramValue);

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
         } else if (declaring.isAnnotationPresent(Endpoint.class)) {
            annotation = declaring.getAnnotation(Endpoint.class);
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
