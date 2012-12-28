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
import static com.google.common.base.Functions.toStringFunction;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.and;
import static com.google.common.base.Predicates.in;
import static com.google.common.base.Predicates.not;
import static com.google.common.base.Predicates.notNull;
import static com.google.common.collect.Collections2.filter;
import static com.google.common.collect.Iterables.any;
import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.get;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.newLinkedList;
import static com.google.common.collect.Maps.filterValues;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Multimaps.filterKeys;
import static com.google.common.collect.Multimaps.transformValues;
import static com.google.common.collect.Sets.newTreeSet;
import static com.google.common.net.HttpHeaders.ACCEPT;
import static com.google.common.net.HttpHeaders.CONTENT_TYPE;
import static com.google.common.net.HttpHeaders.HOST;
import static java.util.Arrays.asList;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import static org.jclouds.http.Uris.uriBuilder;
import static org.jclouds.io.Payloads.newPayload;
import static org.jclouds.util.Maps2.convertUnsafe;
import static org.jclouds.util.Strings2.replaceTokens;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedSet;

import javax.annotation.Resource;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.jclouds.Constants;
import org.jclouds.functions.IdentityFunction;
import org.jclouds.functions.OnlyElementOrNull;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpUtils;
import org.jclouds.http.Uris.UriBuilder;
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
import org.jclouds.http.options.HttpRequestOptions;
import org.jclouds.internal.ClassMethodArgs;
import org.jclouds.io.ContentMetadata;
import org.jclouds.io.ContentMetadataCodec;
import org.jclouds.io.Payload;
import org.jclouds.io.PayloadEnclosing;
import org.jclouds.io.Payloads;
import org.jclouds.io.payloads.MultipartForm;
import org.jclouds.io.payloads.Part;
import org.jclouds.io.payloads.Part.PartOptions;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.internal.GsonWrapper;
import org.jclouds.logging.Logger;
import org.jclouds.rest.Binder;
import org.jclouds.rest.InputParamValidator;
import org.jclouds.rest.InvocationContext;
import org.jclouds.rest.annotations.ApiVersion;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.BuildVersion;
import org.jclouds.rest.annotations.Endpoint;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.FormParams;
import org.jclouds.rest.annotations.Headers;
import org.jclouds.rest.annotations.JAXBResponseParser;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.OnlyElement;
import org.jclouds.rest.annotations.OverrideRequestFilters;
import org.jclouds.rest.annotations.ParamParser;
import org.jclouds.rest.annotations.PartParam;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.PayloadParams;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.rest.annotations.Transform;
import org.jclouds.rest.annotations.Unwrap;
import org.jclouds.rest.annotations.VirtualHost;
import org.jclouds.rest.annotations.WrapWith;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.rest.binders.BindMapToStringPayload;
import org.jclouds.rest.binders.BindToJsonPayloadWrappedWith;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.primitives.Chars;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
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

   // TODO replace with Table object
   static final LoadingCache<Method, LoadingCache<Integer, Set<Annotation>>> methodToIndexOfParamToBinderParamAnnotation = createMethodToIndexOfParamToAnnotation(BinderParam.class);
   static final LoadingCache<Method, LoadingCache<Integer, Set<Annotation>>> methodToIndexOfParamToWrapWithAnnotation = createMethodToIndexOfParamToAnnotation(WrapWith.class);
   static final LoadingCache<Method, LoadingCache<Integer, Set<Annotation>>> methodToIndexOfParamToHeaderParamAnnotations = createMethodToIndexOfParamToAnnotation(HeaderParam.class);
   static final LoadingCache<Method, LoadingCache<Integer, Set<Annotation>>> methodToIndexOfParamToEndpointAnnotations = createMethodToIndexOfParamToAnnotation(Endpoint.class);
   static final LoadingCache<Method, LoadingCache<Integer, Set<Annotation>>> methodToIndexOfParamToEndpointParamAnnotations = createMethodToIndexOfParamToAnnotation(EndpointParam.class);
   static final LoadingCache<Method, LoadingCache<Integer, Set<Annotation>>> methodToIndexOfParamToFormParamAnnotations = createMethodToIndexOfParamToAnnotation(FormParam.class);
   static final LoadingCache<Method, LoadingCache<Integer, Set<Annotation>>> methodToIndexOfParamToQueryParamAnnotations = createMethodToIndexOfParamToAnnotation(QueryParam.class);
   static final LoadingCache<Method, LoadingCache<Integer, Set<Annotation>>> methodToIndexOfParamToPathParamAnnotations = createMethodToIndexOfParamToAnnotation(PathParam.class);
   static final LoadingCache<Method, LoadingCache<Integer, Set<Annotation>>> methodToIndexOfParamToPostParamAnnotations = createMethodToIndexOfParamToAnnotation(PayloadParam.class);
   static final LoadingCache<Method, LoadingCache<Integer, Set<Annotation>>> methodToIndexOfParamToPartParamAnnotations = createMethodToIndexOfParamToAnnotation(PartParam.class);
   static final LoadingCache<Method, LoadingCache<Integer, Set<Annotation>>> methodToIndexOfParamToParamParserAnnotations = createMethodToIndexOfParamToAnnotation(ParamParser.class);

   final Cache<MethodKey, Method> delegationMap;

   static LoadingCache<Method, LoadingCache<Integer, Set<Annotation>>> createMethodToIndexOfParamToAnnotation(
            final Class<? extends Annotation> annotation) {
      return CacheBuilder.newBuilder().build(new CacheLoader<Method, LoadingCache<Integer, Set<Annotation>>>() {
         public LoadingCache<Integer, Set<Annotation>> load(Method method) {
            return CacheBuilder.newBuilder().build(CacheLoader.from(new GetAnnotationsForMethodParameterIndex(method, annotation)));
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
         return ImmutableSet.<Annotation> copyOf(filter(ImmutableList.copyOf(method.getParameterAnnotations()[index]),
               new Predicate<Annotation>() {
                  public boolean apply(Annotation input) {
                     return input.annotationType().equals(clazz);
                  }
               }));
      }

   }

   private static final Function<? super Entry<String, Object>, ? extends Part> ENTRY_TO_PART = new Function<Entry<String, Object>, Part>() {

      @Override
      public Part apply(Entry<String, Object> from) {
         return Part.create(from.getKey(), from.getValue().toString());
      }

   };

   static final LoadingCache<Method, Set<Integer>> methodToIndexesOfOptions = CacheBuilder.newBuilder().build(
         new CacheLoader<Method, Set<Integer>>() {
            @Override
            public Set<Integer> load(Method method) {
               Builder<Integer> toReturn = ImmutableSet.builder();
               for (int index = 0; index < method.getParameterTypes().length; index++) {
                  Class<?> type = method.getParameterTypes()[index];
                  if (HttpRequestOptions.class.isAssignableFrom(type) || HttpRequestOptions[].class.isAssignableFrom(type))
                     toReturn.add(index);
               }
               return toReturn.build();
            }
         });

   private final ParseSax.Factory parserFactory;
   private final HttpUtils utils;
   private final ContentMetadataCodec contentMetadataCodec;
   private final LoadingCache<Class<?>, Boolean> seedAnnotationCache;
   private final String apiVersion;
   private final String buildVersion;

   @Inject
   private InputParamValidator inputParamValidator;

   @VisibleForTesting
   Function<HttpResponse, ?> createResponseParser(Method method, HttpRequest request) {
      return createResponseParser(parserFactory, injector, method, request);
   }

   @SuppressWarnings("unchecked")
   @VisibleForTesting
   public static Function<HttpResponse, ?> createResponseParser(ParseSax.Factory parserFactory, Injector injector,
         Method method, HttpRequest request) {
      Function<HttpResponse, ?> transformer;
      Class<? extends HandlerWithResult<?>> handler = getSaxResponseParserClassOrNull(method);
      if (handler != null) {
         transformer = parserFactory.create(injector.getInstance(handler));
      } else {
         transformer = getTransformerForMethod(method, injector);
      }
      if (transformer instanceof InvocationContext<?>) {
         ((InvocationContext<?>) transformer).setContext(request);
      }
      if (method.isAnnotationPresent(Transform.class)) {
         Function<?, ?> wrappingTransformer = injector.getInstance(method.getAnnotation(Transform.class).value());
         if (wrappingTransformer instanceof InvocationContext<?>) {
            ((InvocationContext<?>) wrappingTransformer).setContext(request);
         }
         transformer = compose(Function.class.cast(wrappingTransformer), transformer);
      }
      return transformer;
   }

   @SuppressWarnings({ "rawtypes", "unchecked" })
   public static Function<HttpResponse, ?> getTransformerForMethod(Method method, Injector injector) {
      Function<HttpResponse, ?> transformer;
      if (method.isAnnotationPresent(SelectJson.class)) {
         Type returnVal = getReturnTypeForMethod(method);
         if (method.isAnnotationPresent(OnlyElement.class))
            returnVal = Types.newParameterizedType(Set.class, returnVal);
         transformer = new ParseFirstJsonValueNamed(injector.getInstance(GsonWrapper.class),
               TypeLiteral.get(returnVal), method.getAnnotation(SelectJson.class).value());
         if (method.isAnnotationPresent(OnlyElement.class))
            transformer = compose(new OnlyElementOrNull(), transformer);
      } else {
         transformer = injector.getInstance(getParserOrThrowException(method));
      }
      return transformer;
   }

   @SuppressWarnings("unchecked")
   @Inject
   public RestAnnotationProcessor(Injector injector, LoadingCache<Class<?>, Boolean> seedAnnotationCache, Cache<MethodKey, Method> delegationMap,
            @ApiVersion String apiVersion, @BuildVersion String buildVersion, ParseSax.Factory parserFactory,
            HttpUtils utils, ContentMetadataCodec contentMetadataCodec, TypeLiteral<T> typeLiteral) {
      this.declaring = (Class<T>) typeLiteral.getRawType();
      this.injector = injector;
      this.parserFactory = parserFactory;
      this.utils = utils;
      this.contentMetadataCodec = contentMetadataCodec;
      this.seedAnnotationCache = seedAnnotationCache;
      seedAnnotationCache.getUnchecked(declaring);
      this.delegationMap = delegationMap;
      this.apiVersion = apiVersion;
      this.buildVersion = buildVersion;
   }


   public Method getDelegateOrNull(Method in) {
      return delegationMap.getIfPresent(new MethodKey(in));
   }

   public static class MethodKey {

      @Override
      public int hashCode() {
         return Objects.hashCode(declaringClass, name, parametersTypeHashCode);
      }

      @Override
      public boolean equals(Object obj) {
         if (this == obj) return true;
         if (obj == null || getClass() != obj.getClass()) return false;
         MethodKey that = MethodKey.class.cast(obj);
         return equal(this.declaringClass, that.declaringClass)
               && equal(this.name, that.name)
               && equal(this.parametersTypeHashCode, that.parametersTypeHashCode);
      }

      private final String name;
      private final int parametersTypeHashCode;
      private final Class<?> declaringClass;

      public MethodKey(Method method) {
         this.name = method.getName();
         this.declaringClass = method.getDeclaringClass();
         int parametersTypeHashCode = 0;
         for (Class<?> param : method.getParameterTypes())
            parametersTypeHashCode += param.hashCode();
         this.parametersTypeHashCode = parametersTypeHashCode;
      }

   }

   final Injector injector;

   private ClassMethodArgs caller;

   public void setCaller(ClassMethodArgs caller) {
      seedAnnotationCache.getUnchecked(caller.getMethod().getDeclaringClass());
      this.caller = caller;
   }

   public GeneratedHttpRequest createRequest(Method method, Object... args) {
      inputParamValidator.validateMethodParametersOrThrow(method, args);


      Optional<URI> endpoint = findEndpoint(method, args);

      if (!endpoint.isPresent()) {
         throw new NoSuchElementException(String.format("no endpoint found for %s",
               new ClassMethodArgs(method.getDeclaringClass(), method, args)));
      }

      GeneratedHttpRequest.Builder requestBuilder = GeneratedHttpRequest.builder();
      HttpRequest r = RestAnnotationProcessor.findHttpRequestInArgs(args);
      if (r != null) {
         requestBuilder.fromHttpRequest(r);
      } else {
         requestBuilder.method(getHttpMethodOrConstantOrThrowException(method));
      }

      // URI template in rfc6570 form
      UriBuilder uriBuilder = uriBuilder(endpoint.get().toString());
      
      overridePathEncoding(uriBuilder, method);
      
      requestBuilder.declaring(declaring)
                    .javaMethod(method)
                    .args(args)
                    .caller(caller)
                    .filters(getFiltersIfAnnotated(method));
      
      Multimap<String, Object> tokenValues = LinkedHashMultimap.create();

      tokenValues.put(Constants.PROPERTY_API_VERSION, apiVersion);
      tokenValues.put(Constants.PROPERTY_BUILD_VERSION, buildVersion);

      // make sure any path from the caller is a prefix
      if (caller != null) {
         tokenValues.putAll(addPathAndGetTokens(caller.getMethod().getDeclaringClass(), caller.getMethod(),
               caller.getArgs(), uriBuilder));
      }
      
      tokenValues.putAll(addPathAndGetTokens(declaring, method, args, uriBuilder));
      
      Multimap<String, Object> formParams = addFormParams(tokenValues, method, args);
      Multimap<String, Object> queryParams = addQueryParams(tokenValues, method, args);
      Multimap<String, String> headers = buildHeaders(tokenValues, method, args);

      if (r != null)
         headers.putAll(r.getHeaders());

      if (shouldAddHostHeader(method)) {
         StringBuilder hostHeader = new StringBuilder(endpoint.get().getHost());
         if (endpoint.get().getPort() != -1)
            hostHeader.append(":").append(endpoint.get().getPort());
         headers.put(HOST, hostHeader.toString());
      }

      Payload payload = null;
      for(HttpRequestOptions options : findOptionsIn(method, args)) {
         injector.injectMembers(options);// TODO test case
         for (Entry<String, String> header : options.buildRequestHeaders().entries()) {
            headers.put(header.getKey(), replaceTokens(header.getValue(), tokenValues));
         }
         for (Entry<String, String> query : options.buildQueryParameters().entries()) {
            queryParams.put(query.getKey(), replaceTokens(query.getValue(), tokenValues));
         }
         for (Entry<String, String> form : options.buildFormParameters().entries()) {
            formParams.put(form.getKey(), replaceTokens(form.getValue(), tokenValues));
         }

         String pathSuffix = options.buildPathSuffix();
         if (pathSuffix != null) {
            uriBuilder.appendPath(pathSuffix);
         }
         String stringPayload = options.buildStringPayload();
         if (stringPayload != null)
            payload = Payloads.newStringPayload(stringPayload);
      }
      
      if (queryParams.size() > 0) {
         uriBuilder.query(queryParams);
      }

      requestBuilder.headers(filterOutContentHeaders(headers));

      requestBuilder.endpoint(uriBuilder.build(convertUnsafe(tokenValues)));
      
      if (payload == null)
         payload = findPayloadInArgs(args);
      
      List<? extends Part> parts = getParts(method, args, ImmutableMultimap.<String, Object> builder()
                                                                           .putAll(tokenValues)
                                                                           .putAll(formParams).build());
      
      if (parts.size() > 0) {
         if (formParams.size() > 0) {
            parts = newLinkedList(concat(transform(formParams.entries(), ENTRY_TO_PART), parts));
         }
         payload = new MultipartForm(BOUNDARY, parts);
      } else if (formParams.size() > 0) {
         payload = Payloads.newUrlEncodedFormPayload(transformValues(formParams, NullableToStringFunction.INSTANCE));
      } else if (headers.containsKey(CONTENT_TYPE)) {
         if (payload == null)
            payload = Payloads.newByteArrayPayload(new byte[] {});
         payload.getContentMetadata().setContentType(get(headers.get(CONTENT_TYPE), 0));
      }
      if (payload != null) {
         requestBuilder.payload(payload);
      }
      GeneratedHttpRequest request = requestBuilder.build();

      org.jclouds.rest.MapBinder mapBinder = getMapPayloadBinderOrNull(method, args);
      if (mapBinder != null) {
         Map<String, Object> mapParams = buildPostParams(method, args);
         if (method.isAnnotationPresent(PayloadParams.class)) {
            PayloadParams params = method.getAnnotation(PayloadParams.class);
            addMapPayload(mapParams, params, headers);
         }
         request = mapBinder.bindToRequest(request, mapParams);
      } else {
         request = decorateRequest(request);
      }

      if (request.getPayload() != null) {
         contentMetadataCodec.fromHeaders(request.getPayload().getContentMetadata(), headers);
      }
      utils.checkRequestHasRequiredProperties(request);
      return request;
   }

   // TODO cache
   private void overridePathEncoding(UriBuilder uriBuilder, Method method) {
      if (declaring.isAnnotationPresent(SkipEncoding.class)) {
         uriBuilder.skipPathEncoding(Chars.asList(declaring.getAnnotation(SkipEncoding.class).value()));
      }
      if (method.isAnnotationPresent(SkipEncoding.class)) {
         uriBuilder.skipPathEncoding(Chars.asList(method.getAnnotation(SkipEncoding.class).value()));
      }
   }

   // different than guava as accepts null
   private static enum NullableToStringFunction implements Function<Object, String> {
      INSTANCE;

      @Override
      public String apply(Object o) {
         if (o == null)
            return null;
         return o.toString();
      }

      @Override
      public String toString() {
         return "toString";
      }
   }
   
   private Optional<URI> findEndpoint(Method method, Object... args) {
      ClassMethodArgs cma = logger.isTraceEnabled() ? new ClassMethodArgs(method.getDeclaringClass(), method, args)
            : null;
      Optional<URI> endpoint = Optional.absent();

      HttpRequest r = RestAnnotationProcessor.findHttpRequestInArgs(args);

      if (r != null) {
         endpoint = Optional.fromNullable(r.getEndpoint());
         if (endpoint.isPresent())
            logger.trace("using endpoint %s from args for %s", endpoint, cma);
      }

      if (!endpoint.isPresent() && caller != null) {
         endpoint = getEndpointFor(caller.getMethod(), caller.getArgs());
         if (endpoint.isPresent())
            logger.trace("using endpoint %s from caller %s for %s", endpoint, caller, cma);
      }
      if (!endpoint.isPresent()) {
         endpoint = getEndpointFor(method, args);
         if (endpoint.isPresent())
            logger.trace("using endpoint %s for %s", endpoint, cma);
      }
      if (!endpoint.isPresent()) {
         logger.trace("looking up default endpoint for %s", cma);
         endpoint = Optional.fromNullable(injector.getInstance(
               Key.get(uriSupplierLiteral, org.jclouds.location.Provider.class)).get());
         if (endpoint.isPresent())
            logger.trace("using default endpoint %s for %s", endpoint, cma);
      }
      return endpoint;
   }

   public static Multimap<String, String> filterOutContentHeaders(Multimap<String, String> headers) {
      // http message usually comes in as a null key header, let's filter it out.
      return ImmutableMultimap.copyOf(filterKeys(headers, and(notNull(), not(in(ContentMetadata.HTTP_HEADERS)))));
   }

   public static final String BOUNDARY = "--JCLOUDS--";

   private Multimap<String, Object> addPathAndGetTokens(Class<?> clazz, Method method, Object[] args, UriBuilder uriBuilder) {
      if (clazz.isAnnotationPresent(Path.class))
         uriBuilder.appendPath(clazz.getAnnotation(Path.class).value());
      if (method.isAnnotationPresent(Path.class))
         uriBuilder.appendPath(method.getAnnotation(Path.class).value());
      return getPathParamKeyValues(method, args);
   }

   private Multimap<String, Object> addFormParams(Multimap<String, ?> tokenValues, Method method, Object... args) {
      Multimap<String, Object> formMap = LinkedListMultimap.create();
      if (declaring.isAnnotationPresent(FormParams.class)) {
         FormParams form = declaring.getAnnotation(FormParams.class);
         addForm(formMap, form, tokenValues);
      }

      if (method.isAnnotationPresent(FormParams.class)) {
         FormParams form = method.getAnnotation(FormParams.class);
         addForm(formMap, form, tokenValues);
      }

      for (Entry<String, Object> form : getFormParamKeyValues(method, args).entries()) {
         formMap.put(form.getKey(), replaceTokens(form.getValue().toString(), tokenValues));
      }
      return formMap;
   }

   private Multimap<String, Object> addQueryParams(Multimap<String, ?> tokenValues, Method method,
         Object... args) {
      Multimap<String, Object> queryMap = LinkedListMultimap.create();
      if (declaring.isAnnotationPresent(QueryParams.class)) {
         QueryParams query = declaring.getAnnotation(QueryParams.class);
         addQuery(queryMap, query, tokenValues);
      }

      if (method.isAnnotationPresent(QueryParams.class)) {
         QueryParams query = method.getAnnotation(QueryParams.class);
         addQuery(queryMap, query, tokenValues);
      }

      for (Entry<String, Object> query : getQueryParamKeyValues(method, args).entries()) {
         queryMap.put(query.getKey(), replaceTokens(query.getValue().toString(), tokenValues));
      }
      return queryMap;
   }

   private void addForm(Multimap<String, Object> formParams, FormParams form, Multimap<String, ?> tokenValues) {
      for (int i = 0; i < form.keys().length; i++) {
         if (form.values()[i].equals(FormParams.NULL)) {
            formParams.removeAll(form.keys()[i]);
            formParams.put(form.keys()[i], null);
         } else {
            formParams.put(form.keys()[i], replaceTokens(form.values()[i], tokenValues));
         }
      }
   }

   private void addQuery(Multimap<String, Object> queryParams, QueryParams query, Multimap<String, ?> tokenValues) {
      for (int i = 0; i < query.keys().length; i++) {
         if (query.values()[i].equals(QueryParams.NULL)) {
            queryParams.removeAll(query.keys()[i]);
            queryParams.put(query.keys()[i], null);
         } else {
            queryParams.put(query.keys()[i], replaceTokens(query.values()[i], tokenValues));
         }
      }
   }

   private void addMapPayload(Map<String, Object> postParams, PayloadParams mapDefaults,
         Multimap<String, String> headers) {
      for (int i = 0; i < mapDefaults.keys().length; i++) {
         if (mapDefaults.values()[i].equals(PayloadParams.NULL)) {
            postParams.put(mapDefaults.keys()[i], null);
         } else {
            postParams.put(mapDefaults.keys()[i], replaceTokens(mapDefaults.values()[i], headers));
         }
      }
   }

   //TODO: change to LoadingCache<Method, List<HttpRequestFilter>> and move this logic to the CacheLoader.
   @VisibleForTesting
   List<HttpRequestFilter> getFiltersIfAnnotated(Method method) {
      List<HttpRequestFilter> filters = newArrayList();
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

   //TODO: change to LoadingCache<ClassMethodArgs, Optional<URI>> and move this logic to the CacheLoader.
   @VisibleForTesting
   public static URI getEndpointInParametersOrNull(Method method, final Object[] args, Injector injector)
         {
      Map<Integer, Set<Annotation>> map = indexWithAtLeastOneAnnotation(method,
            methodToIndexOfParamToEndpointParamAnnotations);
      if (map.size() >= 1 && args.length > 0) {
         EndpointParam firstAnnotation = (EndpointParam) get(get(map.values(), 0), 0);
         Function<Object, URI> parser = injector.getInstance(firstAnnotation.parser());

         if (map.size() == 1) {
            int index = map.keySet().iterator().next();
            try {
               URI returnVal = parser.apply(args[index]);
               checkArgument(returnVal != null,
                     String.format("endpoint for [%s] not configured for %s", args[index], method));
               return returnVal;
            } catch (NullPointerException e) {
               throw new IllegalArgumentException(String.format("argument at index %d on method %s was null", index, method), e);
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
               checkArgument(returnVal != null,
                     String.format("endpoint for [%s] not configured for %s", argsToParse, method));
               return returnVal;
            } catch (NullPointerException e) {
               throw new IllegalArgumentException(String.format("illegal argument in [%s] for method %s", argsToParse,
                     method), e);
            }
         }
      }
      return null;
   }

   private static final TypeLiteral<Supplier<URI>> uriSupplierLiteral = new TypeLiteral<Supplier<URI>>() {
   };

   // TODO: change to LoadingCache<ClassMethodArgs, URI> and move this logic to the CacheLoader.
   private Optional<URI> getEndpointFor(Method method, Object[] args) {
      URI endpoint = getEndpointInParametersOrNull(method, args, injector);
      if (endpoint == null) {
         Endpoint annotation;
         if (method.isAnnotationPresent(Endpoint.class)) {
            annotation = method.getAnnotation(Endpoint.class);
         } else if (method.getDeclaringClass().isAnnotationPresent(Endpoint.class)) {
            annotation = method.getDeclaringClass().getAnnotation(Endpoint.class);
         } else {
            logger.trace("no annotations on class or method: %s", method);
            return Optional.absent();
         }
         endpoint = injector.getInstance(Key.get(uriSupplierLiteral, annotation.value())).get();
      }
      URI providerEndpoint = injector.getInstance(Key.get(uriSupplierLiteral, org.jclouds.location.Provider.class))
               .get();
      return Optional.fromNullable(addHostIfMissing(endpoint, providerEndpoint));
   }

   @VisibleForTesting
   static URI addHostIfMissing(URI original, URI withHost) {
      checkNotNull(withHost, "URI withHost cannot be null");
      checkArgument(withHost.getHost() != null, "URI withHost must have host:" + withHost);

      if (original == null)
         return null;
      if (original.getHost() != null)
         return original;
      return withHost.resolve(original);
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

   //TODO: change to LoadingCache<Method, Key<? extends Function<HttpResponse, ?>>> and move this logic to the CacheLoader.
   @SuppressWarnings({ "unchecked", "rawtypes" })
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
            return Key.get(Class.class.cast(IdentityFunction.class));
         } else if (getAcceptHeadersOrNull(method).contains(APPLICATION_JSON)) {
            return getJsonParserKeyForMethod(method);
         } else if (getAcceptHeadersOrNull(method).contains(APPLICATION_XML)
                || method.isAnnotationPresent(JAXBResponseParser.class)) {
            return getJAXBParserKeyForMethod(method);
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

   public static Key<? extends Function<HttpResponse, ?>> getJsonParserKeyForMethod(Method method) {
      Type returnVal = getReturnTypeForMethod(method);
      return getJsonParserKeyForMethodAnType(method, returnVal);
   }

   @SuppressWarnings("unchecked")
   public static Key<? extends Function<HttpResponse, ?>> getJAXBParserKeyForMethod(Method method) {
       Type returnVal = getReturnTypeForMethod(method);
       Type parserType = Types.newParameterizedType(ParseXMLWithJAXB.class, returnVal);
       return (Key<? extends Function<HttpResponse, ?>>) Key.get(parserType);
    }

   public static Type getReturnTypeForMethod(Method method) {
      Type returnVal;
      if (method.getReturnType().getTypeParameters().length == 0) {
         returnVal = method.getReturnType();
      } else if (method.getReturnType().equals(ListenableFuture.class)) {
         ParameterizedType futureType = (ParameterizedType) method.getGenericReturnType();
         returnVal = futureType.getActualTypeArguments()[0];
         if (returnVal instanceof WildcardType)
            returnVal = WildcardType.class.cast(returnVal).getUpperBounds()[0];
      } else {
         returnVal = method.getGenericReturnType();
      }
      return returnVal;
   }

   @SuppressWarnings({ "unchecked" })
   public static Key<? extends Function<HttpResponse, ?>> getJsonParserKeyForMethodAnType(Method method, Type returnVal) {
      ParameterizedType parserType;
      if (method.isAnnotationPresent(Unwrap.class)) {
         parserType = Types.newParameterizedType(UnwrapOnlyJsonValue.class, returnVal);
      } else {
         parserType = Types.newParameterizedType(ParseJson.class, returnVal);
      }
      return (Key<? extends Function<HttpResponse, ?>>) Key.get(parserType);
   }

   public static Class<? extends HandlerWithResult<?>> getSaxResponseParserClassOrNull(Method method) {
      XMLResponseParser annotation = method.getAnnotation(XMLResponseParser.class);
      if (annotation != null) {
         return annotation.value();
      }
      return null;
   }

   //TODO: change to LoadingCache<ClassMethodArgs, Optional<MapBinder>> and move this logic to the CacheLoader.
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
      if (method.isAnnotationPresent(MapBinder.class)) {
         return injector.getInstance(method.getAnnotation(MapBinder.class).value());
      } else if (method.isAnnotationPresent(org.jclouds.rest.annotations.Payload.class)) {
         return injector.getInstance(BindMapToStringPayload.class);
      } else if (method.isAnnotationPresent(WrapWith.class)) {
         return injector.getInstance(BindToJsonPayloadWrappedWith.Factory.class).create(
            method.getAnnotation(WrapWith.class).value());
      }
      return null;
   }

   public static Set<String> getHttpMethods(Method method) {
      Builder<String> methodsBuilder = ImmutableSet.builder();
      for (Annotation annotation : method.getAnnotations()) {
         HttpMethod http = annotation.annotationType().getAnnotation(HttpMethod.class);
         if (http != null)
            methodsBuilder.add(http.value());
      }
      Set<String> methods = methodsBuilder.build();
      return (methods.size() == 0) ? null : methods;
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

   private static final Predicate<Set<?>> notEmpty = new Predicate<Set<?>>() {
      public boolean apply(Set<?> input) {
         return input.size() >= 1;
      }
   };

   public GeneratedHttpRequest decorateRequest(GeneratedHttpRequest request) throws NegativeArraySizeException {
      Iterable<Entry<Integer, Set<Annotation>>> binderOrWrapWith = concat(
            filterValues(methodToIndexOfParamToBinderParamAnnotation.getUnchecked(request.getJavaMethod()).asMap(),
                  notEmpty).entrySet(),
            filterValues(methodToIndexOfParamToWrapWithAnnotation.getUnchecked(request.getJavaMethod()).asMap(),
                  notEmpty).entrySet());
      OUTER: for (Entry<Integer, Set<Annotation>> entry : binderOrWrapWith) {
         boolean shouldBreak = false;
         Annotation annotation = get(entry.getValue(), 0);
         Binder binder;
         if (annotation instanceof BinderParam)
            binder = injector.getInstance(BinderParam.class.cast(annotation).value());
         else
            binder = injector.getInstance(BindToJsonPayloadWrappedWith.Factory.class).create(
                     WrapWith.class.cast(annotation).value());
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
         } else {
            // either arg is null, or request.getArgs().size() < entry.getKey() + 1
            // in either case, we require that null be allowed
            // (first, however, let's make sure we have enough args on the actual method)
            if (entry.getKey() >= request.getJavaMethod().getParameterAnnotations().length) {
               // not known whether this happens
               throw new IllegalArgumentException("Argument index " + (entry.getKey() + 1)
                        + " is out of bounds for method " + request.getJavaMethod());
            }

            if (request.getJavaMethod().isVarArgs()
                     && entry.getKey() + 1 == request.getJavaMethod().getParameterTypes().length)
               // allow null/missing for var args
               continue OUTER;

            Annotation[] annotations = request.getJavaMethod().getParameterAnnotations()[entry.getKey()];
            for (Annotation a : annotations) {
               if (NULLABLE.apply(a))
                  continue OUTER;
            }
            checkNotNull(null, request.getJavaMethod().getName() + " parameter " + (entry.getKey() + 1));
         }
      }

      return request;
   }

   public static Map<Integer, Set<Annotation>> indexWithOnlyOneAnnotation(Method method, String description,
         LoadingCache<Method, LoadingCache<Integer, Set<Annotation>>> toRefine) {
      Map<Integer, Set<Annotation>> indexToPayloadAnnotation = indexWithAtLeastOneAnnotation(method, toRefine);
      if (indexToPayloadAnnotation.size() > 1) {
         throw new IllegalStateException(String.format(
               "You must not specify more than one %s annotation on: %s; found %s", description, method.toString(),
               indexToPayloadAnnotation));
      }
      return indexToPayloadAnnotation;
   }

   private static Map<Integer, Set<Annotation>> indexWithAtLeastOneAnnotation(Method method,
         LoadingCache<Method, LoadingCache<Integer, Set<Annotation>>> toRefine) {
      Map<Integer, Set<Annotation>> indexToPayloadAnnotation = filterValues(toRefine.getUnchecked(method).asMap(),
            new Predicate<Set<Annotation>>() {
               public boolean apply(Set<Annotation> input) {
                  return input.size() == 1;
               }
            });
      return indexToPayloadAnnotation;
   }

  //TODO: change to LoadingCache<ClassMethodArgs, HttpRequestOptions and move this logic to the CacheLoader.
  private Set<HttpRequestOptions> findOptionsIn(Method method, Object... args) {
     ImmutableSet.Builder<HttpRequestOptions> result = ImmutableSet.builder();
     for (int index : methodToIndexesOfOptions.getUnchecked(method)) {
         if (args.length >= index + 1) {// accommodate varargs
            if (args[index] instanceof Object[]) {
               for (Object option : (Object[]) args[index]) {
                  if (option instanceof HttpRequestOptions) {
                     result.add((HttpRequestOptions) option);
                  }
               }
            } else {
               for (; index < args.length; index++) {
                  if (args[index] instanceof HttpRequestOptions) {
                     result.add((HttpRequestOptions) args[index]);
                  }
               }
            }
         }
      }
      return result.build();
   }

   public Multimap<String, String> buildHeaders(Multimap<String, ?> tokenValues, Method method, Object... args) {
      Multimap<String, String> headers = LinkedHashMultimap.create();
      addHeaderIfAnnotationPresentOnMethod(headers, method, tokenValues);
      LoadingCache<Integer, Set<Annotation>> indexToHeaderParam = methodToIndexOfParamToHeaderParamAnnotations
            .getUnchecked(method);
      for (Entry<Integer, Set<Annotation>> entry : indexToHeaderParam.asMap().entrySet()) {
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
      List<String> accept = getAcceptHeadersOrNull(method);
      if (accept.size() > 0)
         headers.replaceValues(ACCEPT, accept);
   }

   private static List<String> getAcceptHeadersOrNull(Method method) {
      List<String> accept = ImmutableList.of();
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
         Multimap<String, ?> tokenValues) {
      if (declaring.isAnnotationPresent(Headers.class)) {
         Headers header = declaring.getAnnotation(Headers.class);
         addHeader(headers, header, tokenValues);
      }
      if (method.isAnnotationPresent(Headers.class)) {
         Headers header = method.getAnnotation(Headers.class);
         addHeader(headers, header, tokenValues);
      }
   }

   private void addHeader(Multimap<String, String> headers, Headers header, Multimap<String, ?> tokenValues) {
      for (int i = 0; i < header.keys().length; i++) {
         String value = header.values()[i];
         value = replaceTokens(value, tokenValues);
         headers.put(header.keys()[i], value);
      }

   }

   private List<Part> getParts(Method method, Object[] args, Multimap<String, ?> tokenValues) {
      ImmutableList.Builder<Part> parts = ImmutableList.<Part> builder();
      LoadingCache<Integer, Set<Annotation>> indexToPartParam = methodToIndexOfParamToPartParamAnnotations
            .getUnchecked(method);
      for (Entry<Integer, Set<Annotation>> entry : indexToPartParam.asMap().entrySet()) {
         for (Annotation key : entry.getValue()) {
            PartParam param = (PartParam) key;
            PartOptions options = new PartOptions();
            if (!PartParam.NO_CONTENT_TYPE.equals(param.contentType()))
               options.contentType(param.contentType());
            if (!PartParam.NO_FILENAME.equals(param.filename()))
               options.filename(replaceTokens(param.filename(), tokenValues));
            Object arg = args[entry.getKey()];
            checkNotNull(arg, param.name());
            Part part = Part.create(param.name(), newPayload(arg), options);
            parts.add(part);
         }
      }
      return parts.build();
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

   //TODO: change to LoadingCache<ClassMethodArgs, Multimap<String,String> and move this logic to the CacheLoader.
   private Multimap<String, Object> getPathParamKeyValues(Method method, Object... args) {
      Multimap<String, Object> pathParamValues = LinkedHashMultimap.create();
      LoadingCache<Integer, Set<Annotation>> indexToPathParam = methodToIndexOfParamToPathParamAnnotations.getUnchecked(method);

      LoadingCache<Integer, Set<Annotation>> indexToParamExtractor = methodToIndexOfParamToParamParserAnnotations.getUnchecked(method);
      for (Entry<Integer, Set<Annotation>> entry : indexToPathParam.asMap().entrySet()) {
         for (Annotation key : entry.getValue()) {
            Set<Annotation> extractors = indexToParamExtractor.getUnchecked(entry.getKey());
            String paramKey = ((PathParam) key).value();
            Optional<?> paramValue = getParamValue(method, args, extractors, entry, paramKey);
            if (paramValue.isPresent())
               pathParamValues.put(paramKey, paramValue.get().toString());
         }
      }
      if (method.isAnnotationPresent(PathParam.class) && method.isAnnotationPresent(ParamParser.class)) {
         String paramKey = method.getAnnotation(PathParam.class).value();
         String paramValue = injector.getInstance(method.getAnnotation(ParamParser.class).value()).apply(args);
         pathParamValues.put(paramKey, paramValue);

      }
      return pathParamValues;
   }

   protected Optional<?> getParamValue(Method method, Object[] args, Set<Annotation> extractors,
            Entry<Integer, Set<Annotation>> entry, String paramKey) {
      Integer argIndex = entry.getKey();
      Object arg = args[argIndex];
      if (extractors != null && extractors.size() > 0 && checkPresentOrNullable(method, paramKey, argIndex, arg)) {
         ParamParser extractor = (ParamParser) extractors.iterator().next();
         // ParamParsers can deal with nullable parameters
         arg = injector.getInstance(extractor.value()).apply(arg);
      }
      checkPresentOrNullable(method, paramKey, argIndex, arg);
      return Optional.fromNullable(arg);
   }

   private static boolean checkPresentOrNullable(Method method, String paramKey, Integer argIndex, Object arg) {
      if (arg == null && !argNullable(method, argIndex))
         throw new NullPointerException(String.format("param{%s} for method %s.%s", paramKey, method
                  .getDeclaringClass().getSimpleName(), method.getName()));
      return true;
   }

   private static boolean argNullable(Method method, Integer argIndex) {
      return containsNullable(method.getParameterAnnotations()[argIndex]);
   }

   private static final Predicate<Annotation> NULLABLE = new Predicate<Annotation>() {

      @Override
      public boolean apply(Annotation in) {
         return Nullable.class.isAssignableFrom(in.annotationType());
      }
   };

   private static boolean containsNullable(Annotation[] annotations) {
      return any(ImmutableSet.copyOf(annotations), NULLABLE);
   }


   //TODO: change to LoadingCache<ClassMethodArgs, Multimap<String,String> and move this logic to the CacheLoader.
   //take care to manage size of this cache
   private Multimap<String, Object> getFormParamKeyValues(Method method, Object... args) {
      Multimap<String, Object> formParamValues = LinkedHashMultimap.create();
      LoadingCache<Integer, Set<Annotation>> indexToFormParam = methodToIndexOfParamToFormParamAnnotations
            .getUnchecked(method);

      LoadingCache<Integer, Set<Annotation>> indexToParamExtractor = methodToIndexOfParamToParamParserAnnotations
            .getUnchecked(method);
      for (Entry<Integer, Set<Annotation>> entry : indexToFormParam.asMap().entrySet()) {
         for (Annotation key : entry.getValue()) {
            Set<Annotation> extractors = indexToParamExtractor.getUnchecked(entry.getKey());
            String paramKey = ((FormParam) key).value();
            Optional<?> paramValue = getParamValue(method, args, extractors, entry, paramKey);
            if (paramValue.isPresent())
               formParamValues.put(paramKey, paramValue.get().toString());
         }
      }

      if (method.isAnnotationPresent(FormParam.class) && method.isAnnotationPresent(ParamParser.class)) {
         String paramKey = method.getAnnotation(FormParam.class).value();
         String paramValue = injector.getInstance(method.getAnnotation(ParamParser.class).value()).apply(args);
         formParamValues.put(paramKey, paramValue);

      }
      return formParamValues;
   }

   //TODO: change to LoadingCache<ClassMethodArgs, Multimap<String,String> and move this logic to the CacheLoader.
   private Multimap<String, Object> getQueryParamKeyValues(Method method, Object... args) {
      Multimap<String, Object> queryParamValues = LinkedHashMultimap.create();
      LoadingCache<Integer, Set<Annotation>> indexToQueryParam = methodToIndexOfParamToQueryParamAnnotations
            .getUnchecked(method);

      LoadingCache<Integer, Set<Annotation>> indexToParamExtractor = methodToIndexOfParamToParamParserAnnotations
            .getUnchecked(method);
      for (Entry<Integer, Set<Annotation>> entry : indexToQueryParam.asMap().entrySet()) {
         for (Annotation key : entry.getValue()) {
            Set<Annotation> extractors = indexToParamExtractor.getUnchecked(entry.getKey());
            String paramKey = ((QueryParam) key).value();
            Optional<?> paramValue = getParamValue(method, args, extractors, entry, paramKey);
            if (paramValue.isPresent()) {
               if (paramValue.get() instanceof Iterable) {                  
                  Iterable<String> iterableStrings = transform(Iterable.class.cast(paramValue.get()), toStringFunction());
                  queryParamValues.putAll(paramKey, iterableStrings);
               }
               else {
                  queryParamValues.put(paramKey, paramValue.get().toString());
               }
            }
         }
      }

      if (method.isAnnotationPresent(QueryParam.class) && method.isAnnotationPresent(ParamParser.class)) {
         String paramKey = method.getAnnotation(QueryParam.class).value();
         String paramValue = injector.getInstance(method.getAnnotation(ParamParser.class).value()).apply(args);
         queryParamValues.put(paramKey, paramValue);

      }
      return queryParamValues;
   }

   //TODO: change to LoadingCache<ClassMethodArgs, Map<String,Object> and move this logic to the CacheLoader.
   //take care to manage size of this cache
   private Map<String, Object> buildPostParams(Method method, Object... args) {
      Map<String, Object> postParams = newHashMap();
      LoadingCache<Integer, Set<Annotation>> indexToPathParam = methodToIndexOfParamToPostParamAnnotations.getUnchecked(method);
      LoadingCache<Integer, Set<Annotation>> indexToParamExtractor = methodToIndexOfParamToParamParserAnnotations.getUnchecked(method);
      for (Entry<Integer, Set<Annotation>> entry : indexToPathParam.asMap().entrySet()) {
         for (Annotation key : entry.getValue()) {
            Set<Annotation> extractors = indexToParamExtractor.getUnchecked(entry.getKey());
            String paramKey = ((PayloadParam) key).value();
            Optional<?> paramValue = getParamValue(method, args, extractors, entry, paramKey);
            if (paramValue.isPresent())
               postParams.put(paramKey, paramValue.get());
         }
      }
      return postParams;
   }

   /**
    * the class that is being processed
    */
   public Class<T> getDeclaring(){
      return declaring;
   }
}
