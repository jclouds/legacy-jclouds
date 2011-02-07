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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Collections2.filter;
import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.get;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.newLinkedList;
import static com.google.common.collect.Maps.filterValues;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Maps.newLinkedHashMap;
import static com.google.common.collect.Sets.difference;
import static com.google.common.collect.Sets.newHashSet;
import static com.google.common.collect.Sets.newTreeSet;
import static java.util.Arrays.asList;
import static javax.ws.rs.core.HttpHeaders.ACCEPT;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.HttpHeaders.HOST;
import static org.jclouds.io.Payloads.newPayload;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.Map.Entry;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Provider;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriBuilderException;

import org.jclouds.Constants;
import org.jclouds.functions.IdentityFunction;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpUtils;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ParseURIFromListOrLocationHeaderIf20x;
import org.jclouds.http.functions.ReleasePayloadAndReturn;
import org.jclouds.http.functions.ReturnInputStream;
import org.jclouds.http.functions.ReturnStringIf2xx;
import org.jclouds.http.functions.ReturnTrueIf2xx;
import org.jclouds.http.functions.UnwrapOnlyJsonValue;
import org.jclouds.http.functions.UnwrapOnlyNestedJsonValue;
import org.jclouds.http.functions.UnwrapOnlyNestedJsonValueInSet;
import org.jclouds.http.functions.ParseSax.HandlerWithResult;
import org.jclouds.http.options.HttpRequestOptions;
import org.jclouds.http.utils.ModifyRequest;
import org.jclouds.internal.ClassMethodArgs;
import org.jclouds.io.ContentMetadata;
import org.jclouds.io.Payload;
import org.jclouds.io.PayloadEnclosing;
import org.jclouds.io.Payloads;
import org.jclouds.io.payloads.MultipartForm;
import org.jclouds.io.payloads.Part;
import org.jclouds.io.payloads.Part.PartOptions;
import org.jclouds.logging.Logger;
import org.jclouds.rest.Binder;
import org.jclouds.rest.InputParamValidator;
import org.jclouds.rest.InvocationContext;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Delegate;
import org.jclouds.rest.annotations.Endpoint;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.FormParams;
import org.jclouds.rest.annotations.Headers;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.MapPayloadParam;
import org.jclouds.rest.annotations.MapPayloadParams;
import org.jclouds.rest.annotations.MatrixParams;
import org.jclouds.rest.annotations.OverrideRequestFilters;
import org.jclouds.rest.annotations.ParamParser;
import org.jclouds.rest.annotations.PartParam;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.rest.annotations.Unwrap;
import org.jclouds.rest.annotations.VirtualHost;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.rest.functions.MapHttp4xxCodesToExceptions;
import org.jclouds.util.Strings2;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import com.google.inject.util.Types;

/**
 * Creates http methods based on annotations on a class or interface.
 * 
 * @author Adrian Cole
 */
public class RestAnnotationProcessor<T> {

   @Resource
   protected Logger logger = Logger.NULL;

   private final Class<T> declaring;

   static final Map<Method, Map<Integer, Set<Annotation>>> methodToIndexOfParamToDecoratorParamAnnotation = createMethodToIndexOfParamToAnnotation(BinderParam.class);
   static final Map<Method, Map<Integer, Set<Annotation>>> methodToIndexOfParamToHeaderParamAnnotations = createMethodToIndexOfParamToAnnotation(HeaderParam.class);
   static final Map<Method, Map<Integer, Set<Annotation>>> methodToIndexOfParamToEndpointAnnotations = createMethodToIndexOfParamToAnnotation(Endpoint.class);
   static final Map<Method, Map<Integer, Set<Annotation>>> methodToIndexOfParamToEndpointParamAnnotations = createMethodToIndexOfParamToAnnotation(EndpointParam.class);
   static final Map<Method, Map<Integer, Set<Annotation>>> methodToIndexOfParamToMatrixParamAnnotations = createMethodToIndexOfParamToAnnotation(MatrixParam.class);
   static final Map<Method, Map<Integer, Set<Annotation>>> methodToIndexOfParamToFormParamAnnotations = createMethodToIndexOfParamToAnnotation(FormParam.class);
   static final Map<Method, Map<Integer, Set<Annotation>>> methodToIndexOfParamToQueryParamAnnotations = createMethodToIndexOfParamToAnnotation(QueryParam.class);
   static final Map<Method, Map<Integer, Set<Annotation>>> methodToIndexOfParamToPathParamAnnotations = createMethodToIndexOfParamToAnnotation(PathParam.class);
   static final Map<Method, Map<Integer, Set<Annotation>>> methodToIndexOfParamToPostParamAnnotations = createMethodToIndexOfParamToAnnotation(MapPayloadParam.class);
   static final Map<Method, Map<Integer, Set<Annotation>>> methodToIndexOfParamToPartParamAnnotations = createMethodToIndexOfParamToAnnotation(PartParam.class);
   static final Map<Method, Map<Integer, Set<Annotation>>> methodToIndexOfParamToParamParserAnnotations = createMethodToIndexOfParamToAnnotation(ParamParser.class);
   static final Map<MethodKey, Method> delegationMap = newHashMap();

   static Map<Method, Map<Integer, Set<Annotation>>> createMethodToIndexOfParamToAnnotation(
            final Class<? extends Annotation> annotation) {
      return new MapMaker().makeComputingMap(new Function<Method, Map<Integer, Set<Annotation>>>() {
         public Map<Integer, Set<Annotation>> apply(final Method method) {
            return new MapMaker().makeComputingMap(new GetAnnotationsForMethodParameterIndex(method, annotation));
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
         List<Annotation> parameterAnnotations = newArrayList(method.getParameterAnnotations()[index]);
         Collection<Annotation> filtered = filter(parameterAnnotations, new Predicate<Annotation>() {
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

   private static final Function<? super Entry<String, String>, ? extends Part> ENTRY_TO_PART = new Function<Entry<String, String>, Part>() {

      @Override
      public Part apply(Entry<String, String> from) {
         return Part.create(from.getKey(), from.getValue());
      }

   };

   private final Map<Method, Set<Integer>> methodToIndexesOfOptions = new MapMaker()
            .makeComputingMap(new Function<Method, Set<Integer>>() {
               public Set<Integer> apply(final Method method) {
                  Set<Integer> toReturn = newHashSet();
                  for (int index = 0; index < method.getParameterTypes().length; index++) {
                     Class<?> type = method.getParameterTypes()[index];
                     if (HttpRequestOptions.class.isAssignableFrom(type) || optionsVarArgsClass.isAssignableFrom(type))
                        toReturn.add(index);
                  }
                  return toReturn;
               }
            });

   private final ParseSax.Factory parserFactory;
   private final HttpUtils utils;
   private final Provider<UriBuilder> uriBuilderProvider;
   private final String apiVersion;

   private char[] skips;

   @Inject
   private InputParamValidator inputParamValidator;

   @VisibleForTesting
   Function<HttpResponse, ?> createResponseParser(Method method, HttpRequest request) {
      return createResponseParser(parserFactory, injector, method, request);
   }

   @VisibleForTesting
   public static Function<HttpResponse, ?> createResponseParser(ParseSax.Factory parserFactory, Injector injector,
            Method method, HttpRequest request) {
      Function<HttpResponse, ?> transformer;
      Class<? extends HandlerWithResult<?>> handler = getSaxResponseParserClassOrNull(method);
      if (handler != null) {
         transformer = parserFactory.create(injector.getInstance(handler));
      } else {
         transformer = injector.getInstance(getParserOrThrowException(method));
      }
      if (transformer instanceof InvocationContext<?>) {
         ((InvocationContext<?>) transformer).setContext(request);
      }
      return transformer;
   }

   @VisibleForTesting
   Function<Exception, ?> createExceptionParserOrThrowResourceNotFoundOn404IfNoAnnotation(Method method) {
      return createExceptionParserOrThrowResourceNotFoundOn404IfNoAnnotation(injector, method);
   }

   @VisibleForTesting
   public static Function<Exception, ?> createExceptionParserOrThrowResourceNotFoundOn404IfNoAnnotation(
            Injector injector, Method method) {
      ExceptionParser annotation = method.getAnnotation(ExceptionParser.class);
      if (annotation != null) {
         return injector.getInstance(annotation.value());
      }
      return injector.getInstance(MapHttp4xxCodesToExceptions.class);
   }

   @SuppressWarnings("unchecked")
   @Inject
   public RestAnnotationProcessor(Injector injector, ParseSax.Factory parserFactory, HttpUtils utils,
            TypeLiteral<T> typeLiteral) {
      this.declaring = (Class<T>) typeLiteral.getRawType();
      this.injector = injector;
      this.parserFactory = parserFactory;
      this.utils = utils;
      this.uriBuilderProvider = injector.getProvider(UriBuilder.class);
      seedCache(declaring);
      if (declaring.isAnnotationPresent(SkipEncoding.class)) {
         skips = declaring.getAnnotation(SkipEncoding.class).value();
      } else {
         skips = new char[] {};
      }
      this.apiVersion = injector.getInstance(Key.get(String.class, Names.named(Constants.PROPERTY_API_VERSION)));
   }

   public Method getDelegateOrNull(Method in) {
      return delegationMap.get(new MethodKey(in));
   }

   private void seedCache(Class<?> declaring) {
      Set<Method> methods = newHashSet(declaring.getMethods());
      methods = difference(methods, newHashSet(Object.class.getMethods()));
      for (Method method : methods) {
         if (isHttpMethod(method)) {
            for (int index = 0; index < method.getParameterTypes().length; index++) {
               methodToIndexOfParamToDecoratorParamAnnotation.get(method).get(index);
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
         } else if (isConstantDeclaration(method)) {
            bindConstant(method);
         } else if (!method.getDeclaringClass().equals(declaring)) {
            logger.debug("skipping potentially overridden method %s", method);
         } else if (method.isAnnotationPresent(Delegate.class)) {
            logger.debug("skipping delegate method %s", method);
         } else if (!method.getName().startsWith("new")) {
            logger.trace("Method is not annotated as either http or constant: %s", method);
         }
      }
   }

   public static class MethodKey {

      @Override
      public int hashCode() {
         final int prime = 31;
         int result = 1;
         result = prime * result + ((declaringPackage == null) ? 0 : declaringPackage.hashCode());
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
         if (declaringPackage == null) {
            if (other.declaringPackage != null)
               return false;
         } else if (!declaringPackage.equals(other.declaringPackage))
            return false;
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
      private final Package declaringPackage;

      public MethodKey(Method method) {
         this.name = method.getName();
         this.declaringPackage = method.getDeclaringClass().getPackage();
         this.parameterCount = method.getParameterTypes().length;
      }

   }

   final Injector injector;

   private ClassMethodArgs caller;
   private URI callerEndpoint;

   public void setCaller(ClassMethodArgs caller) {
      seedCache(caller.getMethod().getDeclaringClass());
      this.caller = caller;
      try {
         callerEndpoint = getEndpointFor(caller.getMethod(), caller.getArgs(), injector);
      } catch (IllegalStateException e) {
      }
   }

   public GeneratedHttpRequest<T> createRequest(Method method, Object... args) {
      inputParamValidator.validateMethodParametersOrThrow(method, args);
      ClassMethodArgs cma = logger.isTraceEnabled() ? new ClassMethodArgs(method.getDeclaringClass(), method, args)
               : null;

      URI endpoint = callerEndpoint;
      try {
         if (endpoint == null) {
            endpoint = getEndpointFor(method, args, injector);
            logger.trace("using endpoint %s for %s", endpoint, cma);
         } else {
            logger.trace("using endpoint %s from caller %s for %s", caller, endpoint, cma);
         }
      } catch (IllegalStateException e) {
         logger.trace("looking up default endpoint for %s", cma);
         endpoint = injector.getInstance(Key.get(URI.class, org.jclouds.location.Provider.class));
         logger.trace("using default endpoint %s for %s", endpoint, cma);
      }
      GeneratedHttpRequest.Builder<T> requestBuilder;
      HttpRequest r = RestAnnotationProcessor.findHttpRequestInArgs(args);
      if (r != null) {
         requestBuilder = GeneratedHttpRequest.Builder.<T> from(r);
         endpoint = r.getEndpoint();
      } else {
         requestBuilder = GeneratedHttpRequest.<T> builder();
         requestBuilder.method(getHttpMethodOrConstantOrThrowException(method));
      }

      requestBuilder.declaring(declaring).javaMethod(method).args(args).skips(skips);
      requestBuilder.filters(getFiltersIfAnnotated(method));

      UriBuilder builder = uriBuilderProvider.get().uri(endpoint);

      Multimap<String, String> tokenValues = LinkedHashMultimap.create();

      tokenValues.put(Constants.PROPERTY_API_VERSION, apiVersion);

      tokenValues.putAll(addPathAndGetTokens(declaring, method, args, builder));

      Multimap<String, String> formParams = addFormParams(tokenValues.entries(), method, args);
      Multimap<String, String> queryParams = addQueryParams(tokenValues.entries(), method, args);
      Multimap<String, String> matrixParams = addMatrixParams(tokenValues.entries(), method, args);
      Multimap<String, String> headers = buildHeaders(tokenValues.entries(), method, args);
      if (r != null)
         headers.putAll(r.getHeaders());

      if (shouldAddHostHeader(method))
         headers.put(HOST, endpoint.getHost());

      Payload payload = null;
      HttpRequestOptions options = findOptionsIn(method, args);
      if (options != null) {
         injector.injectMembers(options);// TODO test case
         for (Entry<String, String> header : options.buildRequestHeaders().entries()) {
            headers.put(header.getKey(), Strings2.replaceTokens(header.getValue(), tokenValues.entries()));
         }
         for (Entry<String, String> matrix : options.buildMatrixParameters().entries()) {
            matrixParams.put(matrix.getKey(), Strings2.replaceTokens(matrix.getValue(), tokenValues.entries()));
         }
         for (Entry<String, String> query : options.buildQueryParameters().entries()) {
            queryParams.put(query.getKey(), Strings2.replaceTokens(query.getValue(), tokenValues.entries()));
         }
         for (Entry<String, String> form : options.buildFormParameters().entries()) {
            formParams.put(form.getKey(), Strings2.replaceTokens(form.getValue(), tokenValues.entries()));
         }

         String pathSuffix = options.buildPathSuffix();
         if (pathSuffix != null) {
            builder.path(pathSuffix);
         }
         String stringPayload = options.buildStringPayload();
         if (stringPayload != null)
            payload = Payloads.newStringPayload(stringPayload);
      }

      if (matrixParams.size() > 0) {
         for (String key : matrixParams.keySet())
            builder.matrixParam(key, Lists.newArrayList(matrixParams.get(key)).toArray());
      }

      if (queryParams.size() > 0) {
         builder.replaceQuery(ModifyRequest.makeQueryLine(queryParams, null, skips));
      }

      requestBuilder.headers(filterOutContentHeaders(headers));

      try {
         requestBuilder.endpoint(builder.buildFromEncodedMap(convertUnsafe(tokenValues)));
      } catch (IllegalArgumentException e) {
         throw new IllegalStateException(e);
      } catch (UriBuilderException e) {
         throw new IllegalStateException(e);
      }

      if (payload == null)
         payload = findPayloadInArgs(args);
      List<? extends Part> parts = getParts(method, args, concat(tokenValues.entries(), formParams.entries()));
      if (parts.size() > 0) {
         if (formParams.size() > 0) {
            parts = newLinkedList(concat(transform(formParams.entries(), ENTRY_TO_PART), parts));
         }
         payload = new MultipartForm(BOUNDARY, parts);
      } else if (formParams.size() > 0) {
         payload = Payloads.newUrlEncodedFormPayload(formParams, skips);
      } else if (headers.containsKey(CONTENT_TYPE)) {
         if (payload == null)
            payload = Payloads.newByteArrayPayload(new byte[] {});
         payload.getContentMetadata().setContentType(Iterables.get(headers.get(CONTENT_TYPE), 0));
      }
      if (payload != null) {
         requestBuilder.payload(payload);
      }
      GeneratedHttpRequest<T> request = requestBuilder.build();

      org.jclouds.rest.MapBinder mapBinder = getMapPayloadBinderOrNull(method, args);
      if (mapBinder != null) {
         Map<String, String> mapParams = buildPostParams(method, args);
         if (method.isAnnotationPresent(MapPayloadParams.class)) {
            MapPayloadParams params = method.getAnnotation(MapPayloadParams.class);
            addMapPayload(mapParams, params, headers.entries());
         }
         request = mapBinder.bindToRequest(request, mapParams);
      } else {
         request = decorateRequest(request);
      }

      if (request.getPayload() != null)
         request.getPayload().getContentMetadata().setPropertiesFromHttpHeaders(headers);
      utils.checkRequestHasRequiredProperties(request);
      return request;
   }

   public static Multimap<String, String> filterOutContentHeaders(Multimap<String, String> headers) {
      // TODO make a filter like {@link Maps.filterKeys} instead of this
      ImmutableMultimap.Builder<String, String> headersBuilder = ImmutableMultimap.builder();
      // http message usually comes in as a null key header, let's filter it out.
      for (String header : Iterables.filter(headers.keySet(), Predicates.notNull())) {
         if (!ContentMetadata.HTTP_HEADERS.contains(header)) {
            headersBuilder.putAll(header, headers.get(header));
         }
      }
      return headersBuilder.build();
   }

   public static final String BOUNDARY = "--JCLOUDS--";

   private Multimap<String, String> addPathAndGetTokens(Class<?> clazz, Method method, Object[] args, UriBuilder builder) {
      if (clazz.isAnnotationPresent(Path.class))
         builder.path(clazz);
      if (method.isAnnotationPresent(Path.class))
         builder.path(method);
      return encodeValues(getPathParamKeyValues(method, args), skips);
   }

   public URI replaceQuery(URI in, String newQuery, @Nullable Comparator<Entry<String, String>> sorter) {
      return replaceQuery(uriBuilderProvider, in, newQuery, sorter, skips);
   }

   public static URI replaceQuery(Provider<UriBuilder> uriBuilderProvider, URI in, String newQuery,
            @Nullable Comparator<Entry<String, String>> sorter, char... skips) {
      UriBuilder builder = uriBuilderProvider.get().uri(in);
      builder.replaceQuery(ModifyRequest.makeQueryLine(ModifyRequest.parseQueryToMap(newQuery), sorter, skips));
      return builder.build();
   }

   private Multimap<String, String> addMatrixParams(Collection<Entry<String, String>> tokenValues, Method method,
            Object... args) {
      Multimap<String, String> matrixMap = LinkedListMultimap.create();
      if (declaring.isAnnotationPresent(MatrixParams.class)) {
         MatrixParams matrix = declaring.getAnnotation(MatrixParams.class);
         addMatrix(matrixMap, matrix, tokenValues);
      }

      if (method.isAnnotationPresent(MatrixParams.class)) {
         MatrixParams matrix = method.getAnnotation(MatrixParams.class);
         addMatrix(matrixMap, matrix, tokenValues);
      }

      for (Entry<String, String> matrix : getMatrixParamKeyValues(method, args).entries()) {
         matrixMap.put(matrix.getKey(), Strings2.replaceTokens(matrix.getValue(), tokenValues));
      }
      return matrixMap;
   }

   private Multimap<String, String> addFormParams(Collection<Entry<String, String>> tokenValues, Method method,
            Object... args) {
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
         formMap.put(form.getKey(), Strings2.replaceTokens(form.getValue(), tokenValues));
      }
      return formMap;
   }

   private Multimap<String, String> addQueryParams(Collection<Entry<String, String>> tokenValues, Method method,
            Object... args) {
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
         queryMap.put(query.getKey(), Strings2.replaceTokens(query.getValue(), tokenValues));
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
            formParams.put(form.keys()[i], Strings2.replaceTokens(form.values()[i], tokenValues));
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
            queryParams.put(query.keys()[i], Strings2.replaceTokens(query.values()[i], tokenValues));
         }
      }
   }

   private void addMatrix(Multimap<String, String> matrixParams, MatrixParams matrix,
            Collection<Entry<String, String>> tokenValues) {
      for (int i = 0; i < matrix.keys().length; i++) {
         if (matrix.values()[i].equals(MatrixParams.NULL)) {
            matrixParams.removeAll(matrix.keys()[i]);
            matrixParams.put(matrix.keys()[i], null);
         } else {
            matrixParams.put(matrix.keys()[i], Strings2.replaceTokens(matrix.values()[i], tokenValues));
         }
      }
   }

   private void addMapPayload(Map<String, String> postParams, MapPayloadParams mapDefaults,
            Collection<Entry<String, String>> tokenValues) {
      for (int i = 0; i < mapDefaults.keys().length; i++) {
         if (mapDefaults.values()[i].equals(MapPayloadParams.NULL)) {
            postParams.put(mapDefaults.keys()[i], null);
         } else {
            postParams.put(mapDefaults.keys()[i], Strings2.replaceTokens(mapDefaults.values()[i], tokenValues));
         }
      }
   }

   @VisibleForTesting
   List<HttpRequestFilter> getFiltersIfAnnotated(Method method) {
      List<HttpRequestFilter> filters = Lists.newArrayList();
      if (declaring.isAnnotationPresent(RequestFilters.class)) {
         for (Class<? extends HttpRequestFilter> clazz : declaring.getAnnotation(RequestFilters.class).value()) {
            HttpRequestFilter instance = injector.getInstance(clazz);
            filters.add(instance);
            logger.trace("adding filter %s from annotation on %s", instance, declaring.getName());
         }
      }
      if (method.isAnnotationPresent(RequestFilters.class)) {
         if (method.isAnnotationPresent(OverrideRequestFilters.class))
            filters.clear();
         for (Class<? extends HttpRequestFilter> clazz : method.getAnnotation(RequestFilters.class).value()) {
            HttpRequestFilter instance = injector.getInstance(clazz);
            filters.add(instance);
            logger.trace("adding filter %s from annotation on %s", instance, method.getName());
         }
      }
      return filters;
   }

   @VisibleForTesting
   public static URI getEndpointInParametersOrNull(Method method, final Object[] args, Injector injector) {
      Map<Integer, Set<Annotation>> map = indexWithAtLeastOneAnnotation(method,
               methodToIndexOfParamToEndpointParamAnnotations);
      if (map.size() >= 1 && args.length > 0) {
         EndpointParam firstAnnotation = (EndpointParam) get(get(map.values(), 0), 0);
         Function<Object, URI> parser = injector.getInstance(firstAnnotation.parser());

         if (map.size() == 1) {
            int index = map.keySet().iterator().next();
            try {
               URI returnVal = parser.apply(args[index]);
               checkArgument(returnVal != null, String.format("endpoint for [%s] not configured for %s", args[index],
                        method));
               return returnVal;
            } catch (NullPointerException e) {
               throw new IllegalArgumentException(String.format("argument at index %d on method %s", index, method), e);
            }
         } else {
            SortedSet<Integer> keys = newTreeSet(map.keySet());
            Iterable<Object> argsToParse = transform(keys, new Function<Integer, Object>() {

               @Override
               public Object apply(Integer from) {
                  return args[from];
               }

            });
            try {
               URI returnVal = parser.apply(argsToParse);
               checkArgument(returnVal != null, String.format("endpoint for [%s] not configured for %s", argsToParse,
                        method));
               return returnVal;
            } catch (NullPointerException e) {
               throw new IllegalArgumentException(String.format("argument at indexes %s on method %s", map.keySet(),
                        method), e);
            }
         }
      }
      return null;
   }

   public static URI getEndpointFor(Method method, Object[] args, Injector injector) {
      URI endpoint = getEndpointInParametersOrNull(method, args, injector);
      if (endpoint == null) {
         Endpoint annotation;
         if (method.isAnnotationPresent(Endpoint.class)) {
            annotation = method.getAnnotation(Endpoint.class);
         } else if (method.getDeclaringClass().isAnnotationPresent(Endpoint.class)) {
            annotation = method.getDeclaringClass().getAnnotation(Endpoint.class);
         } else {
            throw new IllegalStateException("no annotations on class or method: " + method);
         }
         return injector.getInstance(Key.get(URI.class, annotation.value()));
      }
      return endpoint;
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
   public static final TypeLiteral<ListenableFuture<HttpResponse>> futureHttpResponseLiteral = new TypeLiteral<ListenableFuture<HttpResponse>>() {
   };

   @SuppressWarnings( { "unchecked", "rawtypes" })
   public static Key<? extends Function<HttpResponse, ?>> getParserOrThrowException(Method method) {
      ResponseParser annotation = method.getAnnotation(ResponseParser.class);
      if (annotation == null) {
         if (method.getReturnType().equals(void.class)
                  || TypeLiteral.get(method.getGenericReturnType()).equals(futureVoidLiteral)) {
            return Key.get(ReleasePayloadAndReturn.class);
         } else if (method.getReturnType().equals(boolean.class) || method.getReturnType().equals(Boolean.class)
                  || TypeLiteral.get(method.getGenericReturnType()).equals(futureBooleanLiteral)) {
            return Key.get(ReturnTrueIf2xx.class);
         } else if (method.getReturnType().equals(InputStream.class)
                  || TypeLiteral.get(method.getGenericReturnType()).equals(futureInputStreamLiteral)) {
            return Key.get(ReturnInputStream.class);
         } else if (method.getReturnType().equals(HttpResponse.class)
                  || TypeLiteral.get(method.getGenericReturnType()).equals(futureHttpResponseLiteral)) {
            return Key.get((Class) IdentityFunction.class);
         } else if (getAcceptHeadersOrNull(method).contains(MediaType.APPLICATION_JSON)) {
            Type returnVal;
            if (method.getReturnType().getTypeParameters().length == 0) {
               returnVal = method.getReturnType();
            } else if (method.getReturnType().equals(ListenableFuture.class)) {
               ParameterizedType futureType = ((ParameterizedType) method.getGenericReturnType());
               returnVal = futureType.getActualTypeArguments()[0];
               if (returnVal instanceof WildcardType)
                  returnVal = WildcardType.class.cast(returnVal).getUpperBounds()[0];
            } else {
               returnVal = method.getGenericReturnType();
            }
            ParameterizedType parserType;
            if (method.isAnnotationPresent(Unwrap.class)) {
               int depth = method.getAnnotation(Unwrap.class).depth();
               Class edgeCollection = method.getAnnotation(Unwrap.class).edgeCollection();
               if (depth == 1 && edgeCollection == Map.class)
                  parserType = Types.newParameterizedType(UnwrapOnlyJsonValue.class, returnVal);
               else if (depth == 2 && edgeCollection == Map.class)
                  parserType = Types.newParameterizedType(UnwrapOnlyNestedJsonValue.class, returnVal);
               else if (depth == 3 && edgeCollection == Set.class)
                  parserType = Types.newParameterizedType(UnwrapOnlyNestedJsonValueInSet.class, returnVal);
               else
                  throw new IllegalStateException(String.format(
                           "depth(%d) edgeCollection(%s) not yet supported for @Unwrap", depth, edgeCollection));
            } else {
               parserType = Types.newParameterizedType(ParseJson.class, returnVal);
            }
            return (Key<? extends Function<HttpResponse, ?>>) Key.get(parserType);
         } else if (method.getReturnType().equals(String.class)
                  || TypeLiteral.get(method.getGenericReturnType()).equals(futureStringLiteral)) {
            return Key.get(ReturnStringIf2xx.class);
         } else if (method.getReturnType().equals(URI.class)
                  || TypeLiteral.get(method.getGenericReturnType()).equals(futureURILiteral)) {
            return Key.get(ParseURIFromListOrLocationHeaderIf20x.class);
         } else {
            throw new IllegalStateException("You must specify a ResponseParser annotation on: " + method.toString());
         }
      }
      return Key.get(annotation.value());
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
                     throw new IllegalArgumentException("we currently do not support multiple varargs postBinders in: "
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
      return method.isAnnotationPresent(Path.class) || getHttpMethods(method) != null
               || Sets.newHashSet(method.getParameterTypes()).contains(HttpRequest.class);
   }

   public boolean isConstantDeclaration(Method method) {
      return method.isAnnotationPresent(PathParam.class) && method.isAnnotationPresent(Named.class);
   }

   public void bindConstant(Method method) {
      String key = method.getAnnotation(PathParam.class).value();
      String value = injector.getInstance(Key.get(String.class, method.getAnnotation(Named.class)));
      constants.put(key, value);
   }

   public static Set<String> getHttpMethods(Method method) {
      HashSet<String> methods = new HashSet<String>();
      for (Annotation annotation : method.getAnnotations()) {
         HttpMethod http = annotation.annotationType().getAnnotation(HttpMethod.class);
         if (http != null)
            methods.add(http.value());
      }
      if (methods.size() == 0)
         return null;
      return methods;
   }

   public String getHttpMethodOrConstantOrThrowException(Method method) {
      Set<String> requests = getHttpMethods(method);
      if (requests == null || requests.size() != 1) {
         throw new IllegalStateException(
                  "You must use at least one, but no more than one http method or pathparam annotation on: "
                           + method.toString());
      }
      return requests.iterator().next();
   }

   public boolean shouldAddHostHeader(Method method) {
      if (declaring.isAnnotationPresent(VirtualHost.class) || method.isAnnotationPresent(VirtualHost.class)) {
         return true;
      }
      return false;
   }

   public GeneratedHttpRequest<T> decorateRequest(GeneratedHttpRequest<T> request) {
      OUTER: for (Entry<Integer, Set<Annotation>> entry : filterValues(
               methodToIndexOfParamToDecoratorParamAnnotation.get(request.getJavaMethod()),
               new Predicate<Set<Annotation>>() {
                  public boolean apply(Set<Annotation> input) {
                     return input.size() >= 1;
                  }
               }).entrySet()) {
         boolean shouldBreak = false;
         BinderParam payloadAnnotation = (BinderParam) entry.getValue().iterator().next();
         Binder binder = injector.getInstance(payloadAnnotation.value());
         if (request.getArgs().size() >= entry.getKey() + 1 && request.getArgs().get(entry.getKey()) != null) {
            Object input;
            Class<?> parameterType = request.getJavaMethod().getParameterTypes()[entry.getKey()];
            Class<? extends Object> argType = request.getArgs().get(entry.getKey()).getClass();
            if (!argType.isArray() && request.getJavaMethod().isVarArgs() && parameterType.isArray()) {
               int arrayLength = request.getArgs().size() - request.getJavaMethod().getParameterTypes().length + 1;
               if (arrayLength == 0)
                  break OUTER;
               input = (Object[]) Array.newInstance(request.getArgs().get(entry.getKey()).getClass(), arrayLength);
               System.arraycopy(request.getArgs().toArray(), entry.getKey(), input, 0, arrayLength);
               shouldBreak = true;
            } else if (argType.isArray() && request.getJavaMethod().isVarArgs() && parameterType.isArray()) {
               input = request.getArgs().get(entry.getKey());
            } else {
               input = request.getArgs().get(entry.getKey());
               if (input.getClass().isArray()) {
                  Object[] payloadArray = (Object[]) input;
                  input = payloadArray.length > 0 ? payloadArray[0] : null;
               }
            }
            if (input != null) {
               request = binder.bindToRequest(request, input);
            }
            if (shouldBreak)
               break OUTER;
         }
      }

      return request;
   }

   public static Map<Integer, Set<Annotation>> indexWithOnlyOneAnnotation(Method method, String description,
            Map<Method, Map<Integer, Set<Annotation>>> toRefine) {
      Map<Integer, Set<Annotation>> indexToPayloadAnnotation = indexWithAtLeastOneAnnotation(method, toRefine);
      if (indexToPayloadAnnotation.size() > 1) {
         throw new IllegalStateException(String.format(
                  "You must not specify more than one %s annotation on: %s; found %s", description, method.toString(),
                  indexToPayloadAnnotation));
      }
      return indexToPayloadAnnotation;
   }

   private static Map<Integer, Set<Annotation>> indexWithAtLeastOneAnnotation(Method method,
            Map<Method, Map<Integer, Set<Annotation>>> toRefine) {
      Map<Integer, Set<Annotation>> indexToPayloadAnnotation = filterValues(toRefine.get(method),
               new Predicate<Set<Annotation>>() {
                  public boolean apply(Set<Annotation> input) {
                     return input.size() == 1;
                  }
               });
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
                     throw new IllegalArgumentException("we currently do not support multiple varargs options in: "
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

   public Multimap<String, String> buildHeaders(Collection<Entry<String, String>> tokenValues, Method method,
            final Object... args) {
      Multimap<String, String> headers = LinkedHashMultimap.create();
      addHeaderIfAnnotationPresentOnMethod(headers, method, tokenValues);
      Map<Integer, Set<Annotation>> indexToHeaderParam = methodToIndexOfParamToHeaderParamAnnotations.get(method);
      for (Entry<Integer, Set<Annotation>> entry : indexToHeaderParam.entrySet()) {
         for (Annotation key : entry.getValue()) {
            String value = args[entry.getKey()].toString();
            value = Strings2.replaceTokens(value, tokenValues);
            headers.put(((HeaderParam) key).value(), value);
         }
      }
      addProducesIfPresentOnTypeOrMethod(headers, method);
      addConsumesIfPresentOnTypeOrMethod(headers, method);
      return headers;
   }

   void addConsumesIfPresentOnTypeOrMethod(Multimap<String, String> headers, Method method) {
      List<String> accept = getAcceptHeadersOrNull(method);
      if (accept.size() > 0)
         headers.replaceValues(ACCEPT, accept);
   }

   private static List<String> getAcceptHeadersOrNull(Method method) {
      List<String> accept = Collections.emptyList();
      if (method.getDeclaringClass().isAnnotationPresent(Consumes.class)) {
         Consumes header = method.getDeclaringClass().getAnnotation(Consumes.class);
         accept = asList(header.value());
      }
      if (method.isAnnotationPresent(Consumes.class)) {
         Consumes header = method.getAnnotation(Consumes.class);
         accept = asList(header.value());
      }
      return accept;
   }

   void addProducesIfPresentOnTypeOrMethod(Multimap<String, String> headers, Method method) {
      if (declaring.isAnnotationPresent(Produces.class)) {
         Produces header = declaring.getAnnotation(Produces.class);
         headers.replaceValues(CONTENT_TYPE, asList(header.value()));
      }
      if (method.isAnnotationPresent(Produces.class)) {
         Produces header = method.getAnnotation(Produces.class);
         headers.replaceValues(CONTENT_TYPE, asList(header.value()));
      }
   }

   public void addHeaderIfAnnotationPresentOnMethod(Multimap<String, String> headers, Method method,
            Collection<Entry<String, String>> tokenValues) {
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
         value = Strings2.replaceTokens(value, tokenValues);
         headers.put(header.keys()[i], value);
      }

   }

   private Map<String, String> convertUnsafe(Multimap<String, String> in) {
      Map<String, String> out = newLinkedHashMap();
      for (Entry<String, String> entry : in.entries()) {
         out.put(entry.getKey(), entry.getValue());
      }
      return out;
   }

   List<? extends Part> getParts(Method method, Object[] args, Iterable<Entry<String, String>> iterable) {
      List<Part> parts = newLinkedList();
      Map<Integer, Set<Annotation>> indexToPartParam = methodToIndexOfParamToPartParamAnnotations.get(method);
      for (Entry<Integer, Set<Annotation>> entry : indexToPartParam.entrySet()) {
         for (Annotation key : entry.getValue()) {
            PartParam param = (PartParam) key;
            PartOptions options = new PartOptions();
            if (!PartParam.NO_CONTENT_TYPE.equals(param.contentType()))
               options.contentType(param.contentType());
            if (!PartParam.NO_FILENAME.equals(param.filename()))
               options.filename(Strings2.replaceTokens(param.filename(), iterable));
            Part part = Part.create(param.name(), newPayload(args[entry.getKey()]), options);
            parts.add(part);
         }
      }
      return parts;
   }

   public static HttpRequest findHttpRequestInArgs(Object[] args) {
      if (args == null)
         return null;
      for (int i = 0; i < args.length; i++)
         if (args[i] instanceof HttpRequest)
            return HttpRequest.class.cast(args[i]);
      return null;
   }

   public static Payload findPayloadInArgs(Object[] args) {
      if (args == null)
         return null;
      for (int i = 0; i < args.length; i++)
         if (args[i] instanceof Payload)
            return Payload.class.cast(args[i]);
         else if (args[i] instanceof PayloadEnclosing)
            return PayloadEnclosing.class.cast(args[i]).getPayload();
      return null;
   }

   private Multimap<String, String> getPathParamKeyValues(Method method, Object... args) {
      Multimap<String, String> pathParamValues = LinkedHashMultimap.create();
      pathParamValues.putAll(constants);
      Map<Integer, Set<Annotation>> indexToPathParam = methodToIndexOfParamToPathParamAnnotations.get(method);

      Map<Integer, Set<Annotation>> indexToParamExtractor = methodToIndexOfParamToParamParserAnnotations.get(method);
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

      if (method.isAnnotationPresent(PathParam.class) && method.isAnnotationPresent(ParamParser.class)) {
         String paramKey = method.getAnnotation(PathParam.class).value();
         String paramValue = injector.getInstance(method.getAnnotation(ParamParser.class).value()).apply(args);
         pathParamValues.put(paramKey, paramValue);

      }
      return pathParamValues;
   }

   private Multimap<String, String> encodeValues(Multimap<String, String> unencoded, char... skips) {
      Multimap<String, String> encoded = LinkedHashMultimap.create();
      for (Entry<String, String> entry : unencoded.entries()) {
         encoded.put(entry.getKey(), Strings2.urlEncode(entry.getValue(), skips));
      }
      return encoded;
   }

   private Multimap<String, String> getMatrixParamKeyValues(Method method, Object... args) {
      Multimap<String, String> matrixParamValues = LinkedHashMultimap.create();
      matrixParamValues.putAll(constants);
      Map<Integer, Set<Annotation>> indexToMatrixParam = methodToIndexOfParamToMatrixParamAnnotations.get(method);

      Map<Integer, Set<Annotation>> indexToParamExtractor = methodToIndexOfParamToParamParserAnnotations.get(method);
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

      if (method.isAnnotationPresent(MatrixParam.class) && method.isAnnotationPresent(ParamParser.class)) {
         String paramKey = method.getAnnotation(MatrixParam.class).value();
         String paramValue = injector.getInstance(method.getAnnotation(ParamParser.class).value()).apply(args);
         matrixParamValues.put(paramKey, paramValue);

      }
      return matrixParamValues;
   }

   private Multimap<String, String> getFormParamKeyValues(Method method, Object... args) {
      Multimap<String, String> formParamValues = LinkedHashMultimap.create();
      formParamValues.putAll(constants);
      Map<Integer, Set<Annotation>> indexToFormParam = methodToIndexOfParamToFormParamAnnotations.get(method);

      Map<Integer, Set<Annotation>> indexToParamExtractor = methodToIndexOfParamToParamParserAnnotations.get(method);
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

      if (method.isAnnotationPresent(FormParam.class) && method.isAnnotationPresent(ParamParser.class)) {
         String paramKey = method.getAnnotation(FormParam.class).value();
         String paramValue = injector.getInstance(method.getAnnotation(ParamParser.class).value()).apply(args);
         formParamValues.put(paramKey, paramValue);

      }
      return formParamValues;
   }

   private Multimap<String, String> getQueryParamKeyValues(Method method, Object... args) {
      Multimap<String, String> queryParamValues = LinkedHashMultimap.create();
      queryParamValues.putAll(constants);
      Map<Integer, Set<Annotation>> indexToQueryParam = methodToIndexOfParamToQueryParamAnnotations.get(method);

      Map<Integer, Set<Annotation>> indexToParamExtractor = methodToIndexOfParamToParamParserAnnotations.get(method);
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

      if (method.isAnnotationPresent(QueryParam.class) && method.isAnnotationPresent(ParamParser.class)) {
         String paramKey = method.getAnnotation(QueryParam.class).value();
         String paramValue = injector.getInstance(method.getAnnotation(ParamParser.class).value()).apply(args);
         queryParamValues.put(paramKey, paramValue);

      }
      return queryParamValues;
   }

   private Map<String, String> buildPostParams(Method method, Object... args) {
      Map<String, String> postParams = newHashMap();
      Map<Integer, Set<Annotation>> indexToPathParam = methodToIndexOfParamToPostParamAnnotations.get(method);
      Map<Integer, Set<Annotation>> indexToParamExtractor = methodToIndexOfParamToParamParserAnnotations.get(method);
      for (Entry<Integer, Set<Annotation>> entry : indexToPathParam.entrySet()) {
         for (Annotation key : entry.getValue()) {
            Set<Annotation> extractors = indexToParamExtractor.get(entry.getKey());
            String paramKey = ((MapPayloadParam) key).value();
            String paramValue;
            if (extractors != null && extractors.size() > 0) {
               ParamParser extractor = (ParamParser) extractors.iterator().next();
               paramValue = injector.getInstance(extractor.value()).apply(args[entry.getKey()]);
            } else {
               paramValue = args[entry.getKey()] != null ? args[entry.getKey()].toString() : null;
            }
            postParams.put(paramKey, paramValue);

         }
      }
      return postParams;
   }

}
