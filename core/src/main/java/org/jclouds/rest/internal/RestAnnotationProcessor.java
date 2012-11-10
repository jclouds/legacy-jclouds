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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Collections2.filter;
import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.get;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Lists.newLinkedList;
import static com.google.common.collect.Maps.filterValues;
import static com.google.common.collect.Maps.newHashMap;
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
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.ExecutionException;

import javax.annotation.Resource;
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
import org.jclouds.functions.OnlyElementOrNull;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpUtils;
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
import org.jclouds.http.utils.Queries;
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
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.FormParams;
import org.jclouds.rest.annotations.Headers;
import org.jclouds.rest.annotations.JAXBResponseParser;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.MatrixParams;
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
import org.jclouds.rest.functions.MapHttp4xxCodesToExceptions;
import org.jclouds.util.Maps2;
import org.jclouds.util.Strings2;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Supplier;
import com.google.common.base.Throwables;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.collect.Iterables;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
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
   static final LoadingCache<Method, LoadingCache<Integer, Set<Annotation>>> methodToIndexOfParamToMatrixParamAnnotations = createMethodToIndexOfParamToAnnotation(MatrixParam.class);
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

   private static final Function<? super Entry<String, String>, ? extends Part> ENTRY_TO_PART = new Function<Entry<String, String>, Part>() {

      @Override
      public Part apply(Entry<String, String> from) {
         return Part.create(from.getKey(), from.getValue());
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
   private final Provider<UriBuilder> uriBuilderProvider;
   private final LoadingCache<Class<?>, Boolean> seedAnnotationCache;
   private final String apiVersion;
   private final String buildVersion;
   private char[] skips;

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
         transformer = Functions.compose(Function.class.cast(wrappingTransformer), transformer);
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
            transformer = Functions.compose(new OnlyElementOrNull(), transformer);
      } else {
         transformer = injector.getInstance(getParserOrThrowException(method));
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
   public RestAnnotationProcessor(Injector injector, LoadingCache<Class<?>, Boolean> seedAnnotationCache, Cache<MethodKey, Method> delegationMap,
            @ApiVersion String apiVersion, @BuildVersion String buildVersion, ParseSax.Factory parserFactory,
            HttpUtils utils, ContentMetadataCodec contentMetadataCodec, TypeLiteral<T> typeLiteral) throws ExecutionException {
      this.declaring = (Class<T>) typeLiteral.getRawType();
      this.injector = injector;
      this.parserFactory = parserFactory;
      this.utils = utils;
      this.contentMetadataCodec = contentMetadataCodec;
      this.uriBuilderProvider = injector.getProvider(UriBuilder.class);
      this.seedAnnotationCache = seedAnnotationCache;
      seedAnnotationCache.get(declaring);
      this.delegationMap = delegationMap;
      if (declaring.isAnnotationPresent(SkipEncoding.class)) {
         skips = declaring.getAnnotation(SkipEncoding.class).value();
      } else {
         skips = new char[] {};
      }
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
         return Objects.equal(this.declaringClass, that.declaringClass)
               && Objects.equal(this.name, that.name)
               && Objects.equal(this.parametersTypeHashCode, that.parametersTypeHashCode);
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
   private URI callerEndpoint;

   public void setCaller(ClassMethodArgs caller) {
      try {
         seedAnnotationCache.get(caller.getMethod().getDeclaringClass());
      } catch (ExecutionException e) {
         Throwables.propagate(e);
      }
      this.caller = caller;
      try {
         UriBuilder builder = uriBuilderProvider.get().uri(getEndpointFor(caller.getMethod(), caller.getArgs(), injector));
         Multimap<String, String> tokenValues = addPathAndGetTokens(caller.getMethod().getDeclaringClass(), caller.getMethod(), caller.getArgs(), builder);
         callerEndpoint = builder.buildFromEncodedMap(Maps2.convertUnsafe(tokenValues));
      } catch (IllegalStateException e) {
      } catch (ExecutionException e) {
         Throwables.propagate(e);
      }
   }

   public GeneratedHttpRequest createRequest(Method method, Object... args) {
      try {
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
            endpoint = injector.getInstance(Key.get(uriSupplierLiteral, org.jclouds.location.Provider.class)).get();
            logger.trace("using default endpoint %s for %s", endpoint, cma);
         }
         GeneratedHttpRequest.Builder<?> requestBuilder;
         HttpRequest r = RestAnnotationProcessor.findHttpRequestInArgs(args);
         if (r != null) {
            requestBuilder = GeneratedHttpRequest.builder().fromHttpRequest(r);
            endpoint = r.getEndpoint();
         } else {
            requestBuilder = GeneratedHttpRequest.builder();
            requestBuilder.method(getHttpMethodOrConstantOrThrowException(method));
         }

         if (endpoint == null) {
            throw new NoSuchElementException(String.format("no endpoint found for %s",
                     new ClassMethodArgs(method.getDeclaringClass(), method, args)));
         }

         requestBuilder.declaring(declaring)
                       .javaMethod(method)
                       .args(args)
                       .caller(caller)
                       .skips(skips)
                       .filters(getFiltersIfAnnotated(method));

         UriBuilder builder = uriBuilderProvider.get().uri(endpoint);

         Multimap<String, String> tokenValues = LinkedHashMultimap.create();

         tokenValues.put(Constants.PROPERTY_API_VERSION, apiVersion);
         tokenValues.put(Constants.PROPERTY_BUILD_VERSION, buildVersion);

         tokenValues.putAll(addPathAndGetTokens(declaring, method, args, builder));

         Multimap<String, String> formParams = addFormParams(tokenValues.entries(), method, args);
         Multimap<String, String> queryParams = addQueryParams(tokenValues.entries(), method, args);
         Multimap<String, String> matrixParams = addMatrixParams(tokenValues.entries(), method, args);
         Multimap<String, String> headers = buildHeaders(tokenValues.entries(), method, args);
         if (r != null)
            headers.putAll(r.getHeaders());

         if (shouldAddHostHeader(method)) {
            StringBuilder hostHeader = new StringBuilder(endpoint.getHost());
            if (endpoint.getPort() != -1)
               hostHeader.append(":").append(endpoint.getPort());
            headers.put(HOST, hostHeader.toString());
         }

         Payload payload = null;
         for(HttpRequestOptions options : findOptionsIn(method, args)) {
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
            builder.replaceQuery(Queries.makeQueryLine(queryParams, null, skips));
         }

         requestBuilder.headers(filterOutContentHeaders(headers));

         try {
            requestBuilder.endpoint(builder.buildFromEncodedMap(Maps2.convertUnsafe(tokenValues)));
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
         GeneratedHttpRequest request = requestBuilder.build();

         org.jclouds.rest.MapBinder mapBinder = getMapPayloadBinderOrNull(method, args);
         if (mapBinder != null) {
            Map<String, Object> mapParams = buildPostParams(method, args);
            if (method.isAnnotationPresent(PayloadParams.class)) {
               PayloadParams params = method.getAnnotation(PayloadParams.class);
               addMapPayload(mapParams, params, headers.entries());
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
      } catch (ExecutionException e) {
         throw Throwables.propagate(e);
      }
   }

   public static Multimap<String, String> filterOutContentHeaders(Multimap<String, String> headers) {
      // http message usually comes in as a null key header, let's filter it
      // out.
      return ImmutableMultimap.copyOf(Multimaps.filterKeys(headers,
         Predicates.and(
            Predicates.notNull(),
            Predicates.not(Predicates.in(ContentMetadata.HTTP_HEADERS)))));
   }

   public static final String BOUNDARY = "--JCLOUDS--";

   private Multimap<String, String> addPathAndGetTokens(Class<?> clazz, Method method, Object[] args, UriBuilder builder) throws ExecutionException {
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
      builder.replaceQuery(Queries.makeQueryLine(Queries.parseQueryToMap(newQuery), sorter, skips));
      return builder.build();
   }

   private Multimap<String, String> addMatrixParams(Collection<Entry<String, String>> tokenValues, Method method,
         Object... args) throws ExecutionException {
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
         Object... args) throws ExecutionException {
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
         Object... args) throws ExecutionException {
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

   private void addMapPayload(Map<String, Object> postParams, PayloadParams mapDefaults,
         Collection<Entry<String, String>> tokenValues) {
      for (int i = 0; i < mapDefaults.keys().length; i++) {
         if (mapDefaults.values()[i].equals(PayloadParams.NULL)) {
            postParams.put(mapDefaults.keys()[i], null);
         } else {
            postParams.put(mapDefaults.keys()[i], Strings2.replaceTokens(mapDefaults.values()[i], tokenValues));
         }
      }
   }

   //TODO: change to LoadingCache<Method, List<HttpRequestFilter>> and move this logic to the CacheLoader.
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

   //TODO: change to LoadingCache<ClassMethodArgs, Optional<URI>> and move this logic to the CacheLoader.
   @VisibleForTesting
   public static URI getEndpointInParametersOrNull(Method method, final Object[] args, Injector injector)
         throws ExecutionException {
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
   public static URI getEndpointFor(Method method, Object[] args, Injector injector) throws ExecutionException {
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
         endpoint = injector.getInstance(Key.get(uriSupplierLiteral, annotation.value())).get();
      }
      URI providerEndpoint = injector.getInstance(Key.get(uriSupplierLiteral, org.jclouds.location.Provider.class))
               .get();
      return addHostIfMissing(endpoint, providerEndpoint);
   }

   public static URI addHostIfMissing(URI original, URI withHost) {
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
         } else if (getAcceptHeadersOrNull(method).contains(MediaType.APPLICATION_JSON)) {
            return getJsonParserKeyForMethod(method);
         } else if (getAcceptHeadersOrNull(method).contains(MediaType.APPLICATION_XML)
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

   public GeneratedHttpRequest decorateRequest(GeneratedHttpRequest request) throws NegativeArraySizeException,
            ExecutionException {
      OUTER: for (Entry<Integer, Set<Annotation>> entry : concat(//
               filterValues(methodToIndexOfParamToBinderParamAnnotation.get(request.getJavaMethod()).asMap(), notEmpty)
                        .entrySet(), //
               filterValues(methodToIndexOfParamToWrapWithAnnotation.get(request.getJavaMethod()).asMap(), notEmpty)
                        .entrySet())) {
         boolean shouldBreak = false;
         Annotation annotation = Iterables.get(entry.getValue(), 0);
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
            Preconditions.checkNotNull(null, request.getJavaMethod().getName() + " parameter " + (entry.getKey() + 1));
         }
      }

      return request;
   }

   public static Map<Integer, Set<Annotation>> indexWithOnlyOneAnnotation(Method method, String description,
         LoadingCache<Method, LoadingCache<Integer, Set<Annotation>>> toRefine) throws ExecutionException {
      Map<Integer, Set<Annotation>> indexToPayloadAnnotation = indexWithAtLeastOneAnnotation(method, toRefine);
      if (indexToPayloadAnnotation.size() > 1) {
         throw new IllegalStateException(String.format(
               "You must not specify more than one %s annotation on: %s; found %s", description, method.toString(),
               indexToPayloadAnnotation));
      }
      return indexToPayloadAnnotation;
   }

   private static Map<Integer, Set<Annotation>> indexWithAtLeastOneAnnotation(Method method,
         LoadingCache<Method, LoadingCache<Integer, Set<Annotation>>> toRefine) throws ExecutionException {
      Map<Integer, Set<Annotation>> indexToPayloadAnnotation = filterValues(toRefine.get(method).asMap(),
            new Predicate<Set<Annotation>>() {
               public boolean apply(Set<Annotation> input) {
                  return input.size() == 1;
               }
            });
      return indexToPayloadAnnotation;
   }

  //TODO: change to LoadingCache<ClassMethodArgs, HttpRequestOptions and move this logic to the CacheLoader.
  private Set<HttpRequestOptions> findOptionsIn(Method method, Object... args) throws ExecutionException {
     ImmutableSet.Builder<HttpRequestOptions> result = ImmutableSet.builder();
     for (int index : methodToIndexesOfOptions.get(method)) {
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

   public Multimap<String, String> buildHeaders(Collection<Entry<String, String>> tokenValues, Method method,
         final Object... args) throws ExecutionException {
      Multimap<String, String> headers = LinkedHashMultimap.create();
      addHeaderIfAnnotationPresentOnMethod(headers, method, tokenValues);
      LoadingCache<Integer, Set<Annotation>> indexToHeaderParam = methodToIndexOfParamToHeaderParamAnnotations.get(method);
      for (Entry<Integer, Set<Annotation>> entry : indexToHeaderParam.asMap().entrySet()) {
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

   List<? extends Part> getParts(Method method, Object[] args, Iterable<Entry<String, String>> iterable) throws ExecutionException {
      List<Part> parts = newLinkedList();
      LoadingCache<Integer, Set<Annotation>> indexToPartParam = methodToIndexOfParamToPartParamAnnotations.get(method);
      for (Entry<Integer, Set<Annotation>> entry : indexToPartParam.asMap().entrySet()) {
         for (Annotation key : entry.getValue()) {
            PartParam param = (PartParam) key;
            PartOptions options = new PartOptions();
            if (!PartParam.NO_CONTENT_TYPE.equals(param.contentType()))
               options.contentType(param.contentType());
            if (!PartParam.NO_FILENAME.equals(param.filename()))
               options.filename(Strings2.replaceTokens(param.filename(), iterable));
            Object arg = args[entry.getKey()];
            Preconditions.checkNotNull(arg, param.name());
            Part part = Part.create(param.name(), newPayload(arg), options);
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

   //TODO: change to LoadingCache<ClassMethodArgs, Multimap<String,String> and move this logic to the CacheLoader.
   private Multimap<String, String> getPathParamKeyValues(Method method, Object... args) throws ExecutionException {
      Multimap<String, String> pathParamValues = LinkedHashMultimap.create();
      LoadingCache<Integer, Set<Annotation>> indexToPathParam = methodToIndexOfParamToPathParamAnnotations.get(method);

      LoadingCache<Integer, Set<Annotation>> indexToParamExtractor = methodToIndexOfParamToParamParserAnnotations.get(method);
      for (Entry<Integer, Set<Annotation>> entry : indexToPathParam.asMap().entrySet()) {
         for (Annotation key : entry.getValue()) {
            Set<Annotation> extractors = indexToParamExtractor.get(entry.getKey());
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
      return Iterables.any(ImmutableSet.copyOf(annotations), NULLABLE);
   }

   private Multimap<String, String> encodeValues(Multimap<String, String> unencoded, char... skips) {
      Multimap<String, String> encoded = LinkedHashMultimap.create();
      for (Entry<String, String> entry : unencoded.entries()) {
         encoded.put(entry.getKey(), Strings2.urlEncode(entry.getValue(), skips));
      }
      return encoded;
   }

   //TODO: change to LoadingCache<ClassMethodArgs, Multimap<String,String> and move this logic to the CacheLoader.
   private Multimap<String, String> getMatrixParamKeyValues(Method method, Object... args) throws ExecutionException {
      Multimap<String, String> matrixParamValues = LinkedHashMultimap.create();
      LoadingCache<Integer, Set<Annotation>> indexToMatrixParam = methodToIndexOfParamToMatrixParamAnnotations.get(method);

      LoadingCache<Integer, Set<Annotation>> indexToParamExtractor = methodToIndexOfParamToParamParserAnnotations.get(method);
      for (Entry<Integer, Set<Annotation>> entry : indexToMatrixParam.asMap().entrySet()) {
         for (Annotation key : entry.getValue()) {
            Set<Annotation> extractors = indexToParamExtractor.get(entry.getKey());
            String paramKey = ((MatrixParam) key).value();
            Optional<?> paramValue = getParamValue(method, args, extractors, entry, paramKey);
            if (paramValue.isPresent())
               matrixParamValues.put(paramKey, paramValue.get().toString());
         }
      }

      if (method.isAnnotationPresent(MatrixParam.class) && method.isAnnotationPresent(ParamParser.class)) {
         String paramKey = method.getAnnotation(MatrixParam.class).value();
         String paramValue = injector.getInstance(method.getAnnotation(ParamParser.class).value()).apply(args);
         matrixParamValues.put(paramKey, paramValue);

      }
      return matrixParamValues;
   }

   //TODO: change to LoadingCache<ClassMethodArgs, Multimap<String,String> and move this logic to the CacheLoader.
   //take care to manage size of this cache
   private Multimap<String, String> getFormParamKeyValues(Method method, Object... args) throws ExecutionException {
      Multimap<String, String> formParamValues = LinkedHashMultimap.create();
      LoadingCache<Integer, Set<Annotation>> indexToFormParam = methodToIndexOfParamToFormParamAnnotations.get(method);

      LoadingCache<Integer, Set<Annotation>> indexToParamExtractor = methodToIndexOfParamToParamParserAnnotations.get(method);
      for (Entry<Integer, Set<Annotation>> entry : indexToFormParam.asMap().entrySet()) {
         for (Annotation key : entry.getValue()) {
            Set<Annotation> extractors = indexToParamExtractor.get(entry.getKey());
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
   private Multimap<String, String> getQueryParamKeyValues(Method method, Object... args) throws ExecutionException {
      Multimap<String, String> queryParamValues = LinkedHashMultimap.create();
      LoadingCache<Integer, Set<Annotation>> indexToQueryParam = methodToIndexOfParamToQueryParamAnnotations.get(method);

      LoadingCache<Integer, Set<Annotation>> indexToParamExtractor = methodToIndexOfParamToParamParserAnnotations.get(method);
      for (Entry<Integer, Set<Annotation>> entry : indexToQueryParam.asMap().entrySet()) {
         for (Annotation key : entry.getValue()) {
            Set<Annotation> extractors = indexToParamExtractor.get(entry.getKey());
            String paramKey = ((QueryParam) key).value();
            Optional<?> paramValue = getParamValue(method, args, extractors, entry, paramKey);
            if (paramValue.isPresent())
               queryParamValues.put(paramKey, paramValue.get().toString());
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
   private Map<String, Object> buildPostParams(Method method, Object... args) throws ExecutionException {
      Map<String, Object> postParams = newHashMap();
      LoadingCache<Integer, Set<Annotation>> indexToPathParam = methodToIndexOfParamToPostParamAnnotations.get(method);
      LoadingCache<Integer, Set<Annotation>> indexToParamExtractor = methodToIndexOfParamToParamParserAnnotations.get(method);
      for (Entry<Integer, Set<Annotation>> entry : indexToPathParam.asMap().entrySet()) {
         for (Annotation key : entry.getValue()) {
            Set<Annotation> extractors = indexToParamExtractor.get(entry.getKey());
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
