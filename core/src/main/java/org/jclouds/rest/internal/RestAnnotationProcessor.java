/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.rest.internal;

import static com.google.common.base.Functions.toStringFunction;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Predicates.instanceOf;
import static com.google.common.collect.Collections2.filter;
import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.get;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Iterables.tryFind;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.newLinkedList;
import static com.google.common.collect.Multimaps.transformValues;
import static com.google.common.net.HttpHeaders.ACCEPT;
import static com.google.common.net.HttpHeaders.CONTENT_TYPE;
import static com.google.common.net.HttpHeaders.HOST;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.jclouds.http.HttpUtils.filterOutContentHeaders;
import static org.jclouds.http.HttpUtils.tryFindHttpMethod;
import static org.jclouds.http.Uris.uriBuilder;
import static org.jclouds.io.Payloads.newPayload;
import static org.jclouds.util.Strings2.replaceTokens;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.net.URI;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.annotation.Resource;
import javax.inject.Named;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.jclouds.Constants;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;
import org.jclouds.http.HttpUtils;
import org.jclouds.http.Uris.UriBuilder;
import org.jclouds.http.filters.StripExpectHeader;
import org.jclouds.http.options.HttpRequestOptions;
import org.jclouds.io.ContentMetadataCodec;
import org.jclouds.io.Payload;
import org.jclouds.io.PayloadEnclosing;
import org.jclouds.io.Payloads;
import org.jclouds.io.payloads.MultipartForm;
import org.jclouds.io.payloads.Part;
import org.jclouds.io.payloads.Part.PartOptions;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.logging.Logger;
import org.jclouds.reflect.Invocation;
import org.jclouds.rest.Binder;
import org.jclouds.rest.InputParamValidator;
import org.jclouds.rest.annotations.ApiVersion;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.BuildVersion;
import org.jclouds.rest.annotations.Endpoint;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.FormParams;
import org.jclouds.rest.annotations.Headers;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.OverrideRequestFilters;
import org.jclouds.rest.annotations.ParamParser;
import org.jclouds.rest.annotations.PartParam;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.PayloadParams;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.rest.annotations.VirtualHost;
import org.jclouds.rest.annotations.WrapWith;
import org.jclouds.rest.binders.BindMapToStringPayload;
import org.jclouds.rest.binders.BindToJsonPayloadWrappedWith;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.primitives.Chars;
import com.google.common.reflect.Invokable;
import com.google.common.reflect.Parameter;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

/**
 * 
 * @author Adrian Cole
 */
public class RestAnnotationProcessor implements Function<Invocation, HttpRequest> {

   @Resource
   protected Logger logger = Logger.NULL;

   private static final Function<? super Entry<String, Object>, ? extends Part> ENTRY_TO_PART = new Function<Entry<String, Object>, Part>() {
      @Override
      public Part apply(Entry<String, Object> from) {
         return Part.create(from.getKey(), from.getValue().toString());
      }
   };

   private final Injector injector;
   private final HttpUtils utils;
   private final ContentMetadataCodec contentMetadataCodec;
   private final String apiVersion;
   private final String buildVersion;
   private final InputParamValidator inputParamValidator;
   private final GetAcceptHeaders getAcceptHeaders;
   private final Invocation caller;
   private final boolean stripExpectHeader;

   @Inject
   private RestAnnotationProcessor(Injector injector, @ApiVersion String apiVersion, @BuildVersion String buildVersion,
         HttpUtils utils, ContentMetadataCodec contentMetadataCodec, InputParamValidator inputParamValidator,
         GetAcceptHeaders getAcceptHeaders, @Nullable @Named("caller") Invocation caller,
         @Named(Constants.PROPERTY_STRIP_EXPECT_HEADER) boolean stripExpectHeader) {
      this.injector = injector;
      this.utils = utils;
      this.contentMetadataCodec = contentMetadataCodec;
      this.apiVersion = apiVersion;
      this.buildVersion = buildVersion;
      this.inputParamValidator = inputParamValidator;
      this.getAcceptHeaders = getAcceptHeaders;
      this.caller = caller;
      this.stripExpectHeader = stripExpectHeader;
   }

   /**
    * Note this is dangerous as it cannot pass the inheriting class! Using this
    * when subclassing interfaces may result in lost data.
    */
   @Deprecated
   public GeneratedHttpRequest createRequest(Invokable<?, ?> invokable, List<Object> args) {
      return apply(Invocation.create(invokable, args));
   }

   @Override
   public GeneratedHttpRequest apply(Invocation invocation) {
      checkNotNull(invocation, "invocation");
      inputParamValidator.validateMethodParametersOrThrow(invocation);

      Optional<URI> endpoint = Optional.absent();
      HttpRequest r = findOrNull(invocation.getArgs(), HttpRequest.class);
      if (r != null) {
         endpoint = Optional.fromNullable(r.getEndpoint());
         if (endpoint.isPresent())
            logger.trace("using endpoint %s from invocation.getArgs() for %s", endpoint, invocation);
      } else if (caller != null) {
         endpoint = getEndpointFor(caller);
         if (endpoint.isPresent())
            logger.trace("using endpoint %s from caller %s for %s", endpoint, caller, invocation);
         else
            endpoint = findEndpoint(invocation);
      } else {
         endpoint = findEndpoint(invocation);
      }

      if (!endpoint.isPresent())
         throw new NoSuchElementException(format("no endpoint found for %s", invocation));
      GeneratedHttpRequest.Builder requestBuilder = GeneratedHttpRequest.builder().invocation(invocation)
            .caller(caller);
      String requestMethod = null;
      if (r != null) {
         requestMethod = r.getMethod();
         requestBuilder.fromHttpRequest(r);
      } else {
         requestMethod = tryFindHttpMethod(invocation.getInvokable()).get();
         requestBuilder.method(requestMethod);
      }

      requestBuilder.filters(getFiltersIfAnnotated(invocation));
      if (stripExpectHeader) {
         requestBuilder.filter(new StripExpectHeader());
      }

      Multimap<String, Object> tokenValues = LinkedHashMultimap.create();

      tokenValues.put(Constants.PROPERTY_API_VERSION, apiVersion);
      tokenValues.put(Constants.PROPERTY_BUILD_VERSION, buildVersion);
      // URI template in rfc6570 form
      UriBuilder uriBuilder = uriBuilder(endpoint.get().toString());

      overridePathEncoding(uriBuilder, invocation);

      if (caller != null)
         tokenValues.putAll(addPathAndGetTokens(caller, uriBuilder));
      tokenValues.putAll(addPathAndGetTokens(invocation, uriBuilder));
      Multimap<String, Object> formParams;
      if (caller != null) {
         formParams = addFormParams(tokenValues, caller);
         formParams.putAll(addFormParams(tokenValues, invocation));
      } else {
         formParams = addFormParams(tokenValues, invocation);
      }      
      Multimap<String, Object> queryParams = addQueryParams(tokenValues, invocation);
      Multimap<String, String> headers = buildHeaders(tokenValues, invocation);

      if (r != null)
         headers.putAll(r.getHeaders());

      if (shouldAddHostHeader(invocation)) {
         StringBuilder hostHeader = new StringBuilder(endpoint.get().getHost());
         if (endpoint.get().getPort() != -1)
            hostHeader.append(":").append(endpoint.get().getPort());
         headers.put(HOST, hostHeader.toString());
      }

      Payload payload = null;
      for (HttpRequestOptions options : findOptionsIn(invocation)) {
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

      if (payload == null) {
         PayloadEnclosing payloadEnclosing = findOrNull(invocation.getArgs(), PayloadEnclosing.class);
         payload = (payloadEnclosing != null) ? payloadEnclosing.getPayload() : findOrNull(invocation.getArgs(),
               Payload.class);
      }

      List<? extends Part> parts = getParts(invocation, ImmutableMultimap.<String, Object> builder()
            .putAll(tokenValues).putAll(formParams).build());

      if (parts.size() > 0) {
         if (formParams.size() > 0) {
            parts = newLinkedList(concat(transform(formParams.entries(), ENTRY_TO_PART), parts));
         }
         payload = new MultipartForm(MultipartForm.BOUNDARY, parts);
      } else if (formParams.size() > 0) {
         payload = Payloads.newUrlEncodedFormPayload(transformValues(formParams, NullableToStringFunction.INSTANCE));
      } else if (headers.containsKey(CONTENT_TYPE) && !HttpRequest.NON_PAYLOAD_METHODS.contains(requestMethod)) {
         if (payload == null)
            payload = Payloads.newByteArrayPayload(new byte[] {});
         payload.getContentMetadata().setContentType(get(headers.get(CONTENT_TYPE), 0));
      }
      if (payload != null) {
         requestBuilder.payload(payload);
      }
      GeneratedHttpRequest request = requestBuilder.build();

      org.jclouds.rest.MapBinder mapBinder = getMapPayloadBinderOrNull(invocation);
      if (mapBinder != null) {
         Map<String, Object> mapParams;
         if (caller != null) {
            mapParams = buildPayloadParams(caller);
            mapParams.putAll(buildPayloadParams(invocation));
         } else {
            mapParams = buildPayloadParams(invocation);
         }
         if (invocation.getInvokable().isAnnotationPresent(PayloadParams.class)) {
            PayloadParams params = invocation.getInvokable().getAnnotation(PayloadParams.class);
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

   private static <T> T findOrNull(Iterable<Object> args, Class<T> clazz) {
      return clazz.cast(tryFind(args, instanceOf(clazz)).orNull());
   }

   private static <K, V> Map<K, V> convertUnsafe(Multimap<K, V> in) {
      LinkedHashMap<K, V> out = Maps.newLinkedHashMap();
      for (Entry<K, V> entry : in.entries()) {
         out.put(entry.getKey(), entry.getValue());
      }
      return ImmutableMap.copyOf(out);
   }

   private void overridePathEncoding(UriBuilder uriBuilder, Invocation invocation) {
      if (invocation.getInvokable().getOwnerType().getRawType().isAnnotationPresent(SkipEncoding.class)) {
         uriBuilder.skipPathEncoding(Chars.asList(invocation.getInvokable().getOwnerType().getRawType()
               .getAnnotation(SkipEncoding.class).value()));
      }
      if (invocation.getInvokable().isAnnotationPresent(SkipEncoding.class)) {
         uriBuilder.skipPathEncoding(Chars.asList(invocation.getInvokable().getAnnotation(SkipEncoding.class).value()));
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
   }

   protected Optional<URI> findEndpoint(Invocation invocation) {
      Optional<URI> endpoint = getEndpointFor(invocation);
      if (endpoint.isPresent())
         logger.trace("using endpoint %s for %s", endpoint, invocation);
      if (!endpoint.isPresent()) {
         logger.trace("looking up default endpoint for %s", invocation);
         endpoint = Optional.fromNullable(injector.getInstance(
               Key.get(uriSupplierLiteral, org.jclouds.location.Provider.class)).get());
         if (endpoint.isPresent())
            logger.trace("using default endpoint %s for %s", endpoint, invocation);
      }
      return endpoint;
   }

   private Multimap<String, Object> addPathAndGetTokens(Invocation invocation, UriBuilder uriBuilder) {
      if (invocation.getInvokable().getOwnerType().getRawType().isAnnotationPresent(Path.class))
         uriBuilder.appendPath(invocation.getInvokable().getOwnerType().getRawType().getAnnotation(Path.class).value());
      if (invocation.getInvokable().isAnnotationPresent(Path.class))
         uriBuilder.appendPath(invocation.getInvokable().getAnnotation(Path.class).value());
      return getPathParamKeyValues(invocation);
   }

   private Multimap<String, Object> addFormParams(Multimap<String, ?> tokenValues, Invocation invocation) {
      Multimap<String, Object> formMap = LinkedListMultimap.create();
      if (invocation.getInvokable().getOwnerType().getRawType().isAnnotationPresent(FormParams.class)) {
         FormParams form = invocation.getInvokable().getOwnerType().getRawType().getAnnotation(FormParams.class);
         addForm(formMap, form, tokenValues);
      }

      if (invocation.getInvokable().isAnnotationPresent(FormParams.class)) {
         FormParams form = invocation.getInvokable().getAnnotation(FormParams.class);
         addForm(formMap, form, tokenValues);
      }

      for (Entry<String, Object> form : getFormParamKeyValues(invocation).entries()) {
         formMap.put(form.getKey(), replaceTokens(form.getValue().toString(), tokenValues));
      }
      return formMap;
   }

   private Multimap<String, Object> addQueryParams(Multimap<String, ?> tokenValues, Invocation invocation) {
      Multimap<String, Object> queryMap = LinkedListMultimap.create();
      if (invocation.getInvokable().getOwnerType().getRawType().isAnnotationPresent(QueryParams.class)) {
         QueryParams query = invocation.getInvokable().getOwnerType().getRawType().getAnnotation(QueryParams.class);
         addQuery(queryMap, query, tokenValues);
      }

      if (invocation.getInvokable().isAnnotationPresent(QueryParams.class)) {
         QueryParams query = invocation.getInvokable().getAnnotation(QueryParams.class);
         addQuery(queryMap, query, tokenValues);
      }

      for (Entry<String, Object> query : getQueryParamKeyValues(invocation).entries()) {
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

   private List<HttpRequestFilter> getFiltersIfAnnotated(Invocation invocation) {
      List<HttpRequestFilter> filters = newArrayList();
      if (invocation.getInvokable().getOwnerType().getRawType().isAnnotationPresent(RequestFilters.class)) {
         for (Class<? extends HttpRequestFilter> clazz : invocation.getInvokable().getOwnerType().getRawType()
               .getAnnotation(RequestFilters.class).value()) {
            HttpRequestFilter instance = injector.getInstance(clazz);
            filters.add(instance);
            logger.trace("adding filter %s from annotation on %s", instance, invocation.getInvokable().getOwnerType()
                  .getRawType().getName());
         }
      }
      if (invocation.getInvokable().isAnnotationPresent(RequestFilters.class)) {
         if (invocation.getInvokable().isAnnotationPresent(OverrideRequestFilters.class))
            filters.clear();
         for (Class<? extends HttpRequestFilter> clazz : invocation.getInvokable().getAnnotation(RequestFilters.class)
               .value()) {
            HttpRequestFilter instance = injector.getInstance(clazz);
            filters.add(instance);
            logger.trace("adding filter %s from annotation on %s", instance, invocation.getInvokable().getName());
         }
      }
      return filters;
   }

   @VisibleForTesting
   static URI getEndpointInParametersOrNull(Invocation invocation, Injector injector) {
      Collection<Parameter> endpointParams = parametersWithAnnotation(invocation.getInvokable(), EndpointParam.class);
      if (endpointParams.isEmpty())
         return null;
      checkState(endpointParams.size() == 1, "invocation.getInvoked() %s has too many EndpointParam annotations",
            invocation.getInvokable());
      Parameter endpointParam = get(endpointParams, 0);
      Function<Object, URI> parser = injector.getInstance(endpointParam.getAnnotation(EndpointParam.class).parser());
      int position = endpointParam.hashCode();// guava issue 1243
      try {
         URI returnVal = parser.apply(invocation.getArgs().get(position));
         checkArgument(returnVal != null,
               format("endpoint for [%s] not configured for %s", position, invocation.getInvokable()));
         return returnVal;
      } catch (NullPointerException e) {
         throw new IllegalArgumentException(format("argument at index %d on invocation.getInvoked() %s was null",
               position, invocation.getInvokable()), e);
      }
   }

   private static Collection<Parameter> parametersWithAnnotation(Invokable<?, ?> invokable,
         final Class<? extends Annotation> annotationType) {
      return filter(invokable.getParameters(), new Predicate<Parameter>() {
         public boolean apply(Parameter in) {
            return in.isAnnotationPresent(annotationType);
         }
      });
   }

   private static final TypeLiteral<Supplier<URI>> uriSupplierLiteral = new TypeLiteral<Supplier<URI>>() {
   };

   protected Optional<URI> getEndpointFor(Invocation invocation) {
      URI endpoint = getEndpointInParametersOrNull(invocation, injector);
      if (endpoint == null) {
         Endpoint annotation;
         if (invocation.getInvokable().isAnnotationPresent(Endpoint.class)) {
            annotation = invocation.getInvokable().getAnnotation(Endpoint.class);
         } else if (invocation.getInvokable().getOwnerType().getRawType().isAnnotationPresent(Endpoint.class)) {
            annotation = invocation.getInvokable().getOwnerType().getRawType().getAnnotation(Endpoint.class);
         } else {
            logger.trace("no annotations on class or invocation.getInvoked(): %s", invocation.getInvokable());
            return Optional.absent();
         }
         endpoint = injector.getInstance(Key.get(uriSupplierLiteral, annotation.value())).get();
      }
      URI provider = injector.getInstance(Key.get(uriSupplierLiteral, org.jclouds.location.Provider.class)).get();
      return Optional.fromNullable(addHostIfMissing(endpoint, provider));
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

   private org.jclouds.rest.MapBinder getMapPayloadBinderOrNull(Invocation invocation) {
      if (invocation.getArgs() != null) {
         for (Object arg : invocation.getArgs()) {
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
                           "we currently do not support multiple varinvocation.getArgs() postBinders in: "
                                 + invocation.getInvokable().getName());
                  }
               }
            } else if (arg instanceof org.jclouds.rest.MapBinder) {
               org.jclouds.rest.MapBinder binder = (org.jclouds.rest.MapBinder) arg;
               injector.injectMembers(binder);
               return binder;
            }
         }
      }
      if (invocation.getInvokable().isAnnotationPresent(MapBinder.class)) {
         return injector.getInstance(invocation.getInvokable().getAnnotation(MapBinder.class).value());
      } else if (invocation.getInvokable().isAnnotationPresent(org.jclouds.rest.annotations.Payload.class)) {
         return injector.getInstance(BindMapToStringPayload.class);
      } else if (invocation.getInvokable().isAnnotationPresent(WrapWith.class)) {
         return injector.getInstance(BindToJsonPayloadWrappedWith.Factory.class).create(
               invocation.getInvokable().getAnnotation(WrapWith.class).value());
      }
      return null;
   }

   private boolean shouldAddHostHeader(Invocation invocation) {
      return invocation.getInvokable().getOwnerType().getRawType().isAnnotationPresent(VirtualHost.class) || invocation
            .getInvokable().isAnnotationPresent(VirtualHost.class);
   }

   private GeneratedHttpRequest decorateRequest(GeneratedHttpRequest request) throws NegativeArraySizeException {
      Invocation invocation = request.getInvocation();
      List<Object> args = request.getInvocation().getArgs();
      Set<Parameter> binderOrWrapWith = ImmutableSet.copyOf(concat(
            parametersWithAnnotation(invocation.getInvokable(), BinderParam.class),
            parametersWithAnnotation(invocation.getInvokable(), WrapWith.class)));
      OUTER: for (Parameter entry : binderOrWrapWith) {
         int position = entry.hashCode();
         boolean shouldBreak = false;
         Binder binder;
         if (entry.isAnnotationPresent(BinderParam.class))
            binder = injector.getInstance(entry.getAnnotation(BinderParam.class).value());
         else
            binder = injector.getInstance(BindToJsonPayloadWrappedWith.Factory.class).create(
                  entry.getAnnotation(WrapWith.class).value());
         Object arg = args.size() >= position + 1 ? args.get(position) : null;
         if (args.size() >= position + 1 && arg != null) {
            Class<?> parameterType = entry.getType().getRawType();
            Class<? extends Object> argType = arg.getClass();
            if (!argType.isArray() && parameterType.isArray()) {// TODO: &&
                                                                // invocation.getInvokable().isVarArgs())
                                                                // {
               int arrayLength = args.size() - invocation.getInvokable().getParameters().size() + 1;
               if (arrayLength == 0)
                  break OUTER;
               arg = (Object[]) Array.newInstance(arg.getClass(), arrayLength);
               System.arraycopy(args.toArray(), position, arg, 0, arrayLength);
               shouldBreak = true;
            } else if (argType.isArray() && parameterType.isArray()) {// TODO:
                                                                      // &&
                                                                      // invocation.getInvokable().isVarArgs())
                                                                      // {
            } else {
               if (arg.getClass().isArray()) {
                  Object[] payloadArray = (Object[]) arg;
                  arg = payloadArray.length > 0 ? payloadArray[0] : null;
               }
            }
            if (arg != null) {
               request = binder.bindToRequest(request, arg);
            }
            if (shouldBreak)
               break OUTER;
         } else {
            if (position + 1 == invocation.getInvokable().getParameters().size() && entry.getType().isArray())// TODO:
                                                                                                              // &&
                                                                                                              // invocation.getInvokable().isVarArgs())
               continue OUTER;

            if (entry.isAnnotationPresent(Nullable.class)) {
               continue OUTER;
            }
            checkNotNull(arg, invocation.getInvokable().getName() + " parameter " + (position + 1));
         }
      }
      return request;
   }

   private static final LoadingCache<Invokable<?, ?>, Set<Integer>> invokableToIndexesOfOptions = CacheBuilder
         .newBuilder().build(new CacheLoader<Invokable<?, ?>, Set<Integer>>() {
            @Override
            public Set<Integer> load(Invokable<?, ?> invokable) {
               Builder<Integer> toReturn = ImmutableSet.builder();
               for (Parameter param : invokable.getParameters()) {
                  Class<?> type = param.getType().getRawType();
                  if (HttpRequestOptions.class.isAssignableFrom(type)
                        || HttpRequestOptions[].class.isAssignableFrom(type))
                     toReturn.add(param.hashCode());
               }
               return toReturn.build();
            }
         });

   private Set<HttpRequestOptions> findOptionsIn(Invocation invocation) {
      ImmutableSet.Builder<HttpRequestOptions> result = ImmutableSet.builder();
      for (int index : invokableToIndexesOfOptions.getUnchecked(invocation.getInvokable())) {
         if (invocation.getArgs().size() >= index + 1) {// accommodate
                                                        // varinvocation.getArgs()
            if (invocation.getArgs().get(index) instanceof Object[]) {
               for (Object option : (Object[]) invocation.getArgs().get(index)) {
                  if (option instanceof HttpRequestOptions) {
                     result.add((HttpRequestOptions) option);
                  }
               }
            } else {
               for (; index < invocation.getArgs().size(); index++) {
                  if (invocation.getArgs().get(index) instanceof HttpRequestOptions) {
                     result.add((HttpRequestOptions) invocation.getArgs().get(index));
                  }
               }
            }
         }
      }
      return result.build();
   }

   private Multimap<String, String> buildHeaders(Multimap<String, ?> tokenValues, Invocation invocation) {
      Multimap<String, String> headers = LinkedHashMultimap.create();
      addHeaderIfAnnotationPresentOnMethod(headers, invocation, tokenValues);
      for (Parameter headerParam : parametersWithAnnotation(invocation.getInvokable(), HeaderParam.class)) {
         Annotation key = headerParam.getAnnotation(HeaderParam.class);
         String value = invocation.getArgs().get(headerParam.hashCode()).toString();
         value = replaceTokens(value, tokenValues);
         headers.put(((HeaderParam) key).value(), value);
      }
      addProducesIfPresentOnTypeOrMethod(headers, invocation);
      addConsumesIfPresentOnTypeOrMethod(headers, invocation);
      return headers;
   }

   private void addConsumesIfPresentOnTypeOrMethod(Multimap<String, String> headers, Invocation invocation) {
      Set<String> accept = getAcceptHeaders.apply(invocation);
      if (!accept.isEmpty())
         headers.replaceValues(ACCEPT, accept);
   }

   private void addProducesIfPresentOnTypeOrMethod(Multimap<String, String> headers, Invocation invocation) {
      if (invocation.getInvokable().getOwnerType().getRawType().isAnnotationPresent(Produces.class)) {
         Produces header = invocation.getInvokable().getOwnerType().getRawType().getAnnotation(Produces.class);
         headers.replaceValues(CONTENT_TYPE, asList(header.value()));
      }
      if (invocation.getInvokable().isAnnotationPresent(Produces.class)) {
         Produces header = invocation.getInvokable().getAnnotation(Produces.class);
         headers.replaceValues(CONTENT_TYPE, asList(header.value()));
      }
   }

   private void addHeaderIfAnnotationPresentOnMethod(Multimap<String, String> headers, Invocation invocation,
         Multimap<String, ?> tokenValues) {
      if (invocation.getInvokable().getOwnerType().getRawType().isAnnotationPresent(Headers.class)) {
         Headers header = invocation.getInvokable().getOwnerType().getRawType().getAnnotation(Headers.class);
         addHeader(headers, header, tokenValues);
      }
      if (invocation.getInvokable().isAnnotationPresent(Headers.class)) {
         Headers header = invocation.getInvokable().getAnnotation(Headers.class);
         addHeader(headers, header, tokenValues);
      }
   }

   private static void addHeader(Multimap<String, String> headers, Headers header, Multimap<String, ?> tokenValues) {
      for (int i = 0; i < header.keys().length; i++) {
         String value = header.values()[i];
         value = replaceTokens(value, tokenValues);
         headers.put(header.keys()[i], value);
      }
   }

   private static List<Part> getParts(Invocation invocation, Multimap<String, ?> tokenValues) {
      ImmutableList.Builder<Part> parts = ImmutableList.<Part> builder();
      for (Parameter param : parametersWithAnnotation(invocation.getInvokable(), PartParam.class)) {
         PartParam partParam = param.getAnnotation(PartParam.class);
         PartOptions options = new PartOptions();
         if (!PartParam.NO_CONTENT_TYPE.equals(partParam.contentType()))
            options.contentType(partParam.contentType());
         if (!PartParam.NO_FILENAME.equals(partParam.filename()))
            options.filename(replaceTokens(partParam.filename(), tokenValues));
         Object arg = invocation.getArgs().get(param.hashCode());
         checkNotNull(arg, partParam.name());
         Part part = Part.create(partParam.name(), newPayload(arg), options);
         parts.add(part);
      }
      return parts.build();
   }

   private Multimap<String, Object> getPathParamKeyValues(Invocation invocation) {
      Multimap<String, Object> pathParamValues = LinkedHashMultimap.create();
      for (Parameter param : parametersWithAnnotation(invocation.getInvokable(), PathParam.class)) {
         PathParam pathParam = param.getAnnotation(PathParam.class);
         String paramKey = pathParam.value();
         Optional<?> paramValue = getParamValue(invocation, param.getAnnotation(ParamParser.class), param.hashCode(),
               paramKey);
         if (paramValue.isPresent())
            pathParamValues.put(paramKey, paramValue.get().toString());
      }
      return pathParamValues;
   }

   private Optional<?> getParamValue(Invocation invocation, @Nullable ParamParser extractor, int argIndex,
         String paramKey) {
      Object arg = invocation.getArgs().get(argIndex);
      if (extractor != null && checkPresentOrNullable(invocation, paramKey, argIndex, arg)) {
         // ParamParsers can deal with nullable parameters
         arg = injector.getInstance(extractor.value()).apply(arg);
      }
      checkPresentOrNullable(invocation, paramKey, argIndex, arg);
      return Optional.fromNullable(arg);
   }

   private boolean checkPresentOrNullable(Invocation invocation, String paramKey, int argIndex, Object arg) {
      if (arg == null && !invocation.getInvokable().getParameters().get(argIndex).isAnnotationPresent(Nullable.class))
         throw new NullPointerException(format("param{%s} for invocation %s.%s", paramKey, invocation.getInvokable()
               .getOwnerType().getRawType().getSimpleName(), invocation.getInvokable().getName()));
      return true;
   }

   private Multimap<String, Object> getFormParamKeyValues(Invocation invocation) {
      Multimap<String, Object> formParamValues = LinkedHashMultimap.create();
      for (Parameter param : parametersWithAnnotation(invocation.getInvokable(), FormParam.class)) {
         FormParam formParam = param.getAnnotation(FormParam.class);
         String paramKey = formParam.value();
         Optional<?> paramValue = getParamValue(invocation, param.getAnnotation(ParamParser.class), param.hashCode(),
               paramKey);
         if (paramValue.isPresent())
            formParamValues.put(paramKey, paramValue.get().toString());
      }
      return formParamValues;
   }

   private Multimap<String, Object> getQueryParamKeyValues(Invocation invocation) {
      Multimap<String, Object> queryParamValues = LinkedHashMultimap.create();
      for (Parameter param : parametersWithAnnotation(invocation.getInvokable(), QueryParam.class)) {
         QueryParam queryParam = param.getAnnotation(QueryParam.class);
         String paramKey = queryParam.value();
         Optional<?> paramValue = getParamValue(invocation, param.getAnnotation(ParamParser.class), param.hashCode(),
               paramKey);
         if (paramValue.isPresent())
            if (paramValue.get() instanceof Iterable) {
               @SuppressWarnings("unchecked")
               Iterable<String> iterableStrings = transform(Iterable.class.cast(paramValue.get()), toStringFunction());
               queryParamValues.putAll(paramKey, iterableStrings);
            } else {
               queryParamValues.put(paramKey, paramValue.get().toString());
            }
      }
      return queryParamValues;
   }

   private Map<String, Object> buildPayloadParams(Invocation invocation) {
      Map<String, Object> payloadParamValues = Maps.newLinkedHashMap();
      for (Parameter param : parametersWithAnnotation(invocation.getInvokable(), PayloadParam.class)) {
         PayloadParam payloadParam = param.getAnnotation(PayloadParam.class);
         String paramKey = payloadParam.value();
         Optional<?> paramValue = getParamValue(invocation, param.getAnnotation(ParamParser.class), param.hashCode(),
               paramKey);
         if (paramValue.isPresent())
            payloadParamValues.put(paramKey, paramValue.get());
      }
      return payloadParamValues;
   }

   @Override
   public String toString() {
      String callerString = caller != null ? String.format("%s.%s%s", caller.getInvokable().getOwnerType().getRawType().getSimpleName(),
            caller.getInvokable().getName(), caller.getArgs()) : null;
      return Objects.toStringHelper("").omitNullValues().add("caller", callerString).toString();
   }
}
