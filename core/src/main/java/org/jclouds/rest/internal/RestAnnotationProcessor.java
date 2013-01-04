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
import static com.google.common.base.Functions.toStringFunction;
import static com.google.common.base.Objects.equal;
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
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.annotation.Resource;
import javax.ws.rs.Consumes;
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
import org.jclouds.http.options.HttpRequestOptions;
import org.jclouds.internal.ClassInvokerArgs;
import org.jclouds.io.ContentMetadataCodec;
import org.jclouds.io.Payload;
import org.jclouds.io.PayloadEnclosing;
import org.jclouds.io.Payloads;
import org.jclouds.io.payloads.MultipartForm;
import org.jclouds.io.payloads.Part;
import org.jclouds.io.payloads.Part.PartOptions;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.logging.Logger;
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
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.primitives.Chars;
import com.google.common.reflect.Invokable;
import com.google.common.reflect.Parameter;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.Assisted;

/**
 * Creates http methods based on annotations on a class or interface.
 *
 * @author Adrian Cole
 */
public abstract class RestAnnotationProcessor {

   public static interface Factory {
      Declaring declaring(Class<?> declaring);
      Caller caller(ClassInvokerArgs caller);
   }

   public static final class Declaring extends RestAnnotationProcessor {
      @Inject
      private Declaring(Injector injector, @ApiVersion String apiVersion, @BuildVersion String buildVersion,
            HttpUtils utils, ContentMetadataCodec contentMetadataCodec, InputParamValidator inputParamValidator,
            @Assisted Class<?> declaring) {
         super(injector, apiVersion, buildVersion, utils, contentMetadataCodec, inputParamValidator, declaring);
      }
   }

   public static final class Caller extends RestAnnotationProcessor {
      private final ClassInvokerArgs caller;

      @Inject
      private Caller(Injector injector, @ApiVersion String apiVersion, @BuildVersion String buildVersion,
            HttpUtils utils, ContentMetadataCodec contentMetadataCodec, InputParamValidator inputParamValidator,
            @Assisted ClassInvokerArgs caller) {
         super(injector, apiVersion, buildVersion, utils, contentMetadataCodec, inputParamValidator, caller.getClazz());
         this.caller = caller;
      }

      @Override
      protected GeneratedHttpRequest.Builder requestBuilder() {
         return super.requestBuilder().caller(caller);
      }

      @Override
      protected Optional<URI> findEndpoint(Invokable<?,?> method, List<Object> args) {
         Optional<URI> endpoint = getEndpointFor(caller.getInvoker(), caller.getArgs());
         if (endpoint.isPresent())
            logger.trace("using endpoint %s from caller %s for %s", endpoint, caller, cma(method, args));
         else
            endpoint = super.findEndpoint(method, args);
         return endpoint;
      }

      @Override
      protected Multimap<String, Object> addPathAndGetTokens(Class<?> clazz, Invokable<?,?> method, List<Object> args,
            UriBuilder uriBuilder) {
         Class<?> callerClass = caller.getInvoker().getDeclaringClass();
         return ImmutableMultimap.<String, Object> builder()
               .putAll(super.addPathAndGetTokens(callerClass, caller.getInvoker(), caller.getArgs(), uriBuilder))
               .putAll(super.addPathAndGetTokens(clazz, method, args, uriBuilder)).build();
      }
   }

   protected ClassInvokerArgs cma(Invokable<?,?> method, List<Object> args) {
      return logger.isTraceEnabled() ? new ClassInvokerArgs(method.getDeclaringClass(), method, args) : null;
   }

   @Resource
   protected Logger logger = Logger.NULL;

   static final LoadingCache<Method, Invokable<?, ?>> methods = CacheBuilder.newBuilder().build(
         new CacheLoader<Method, Invokable<?, ?>>() {
            @Override
            public Invokable<?, ?> load(Method method) {
               return Invokable.from(method);
            }
         });
   

   private static final Function<? super Entry<String, Object>, ? extends Part> ENTRY_TO_PART = new Function<Entry<String, Object>, Part>() {
      @Override
      public Part apply(Entry<String, Object> from) {
         return Part.create(from.getKey(), from.getValue().toString());
      }
   };

   private final Class<?> declaring;
   private final Injector injector;
   private final HttpUtils utils;
   private final ContentMetadataCodec contentMetadataCodec;
   private final String apiVersion;
   private final String buildVersion;
   private final InputParamValidator inputParamValidator;
   
   private RestAnnotationProcessor(Injector injector, String apiVersion, String buildVersion,
          HttpUtils utils, ContentMetadataCodec contentMetadataCodec,
         InputParamValidator inputParamValidator, Class<?> declaring) {
      this.injector = injector;
      this.utils = utils;
      this.contentMetadataCodec = contentMetadataCodec;
      this.apiVersion = apiVersion;
      this.buildVersion = buildVersion;
      this.inputParamValidator = inputParamValidator;
      this.declaring = declaring;
   }
   
   public static class InvokerKey {
      private final String name;
      private final int parametersTypeHashCode;
      private final Class<?> declaringClass;

      public InvokerKey(Invokable<?, ?> invoker) {
         this.name = invoker.getName();
         this.declaringClass = invoker.getDeclaringClass();
         int parametersTypeHashCode = 0;
         for (Parameter param : invoker.getParameters())
            parametersTypeHashCode += param.getType().hashCode();
         this.parametersTypeHashCode = parametersTypeHashCode;
      }
      
      @Override
      public int hashCode() {
         return Objects.hashCode(declaringClass, name, parametersTypeHashCode);
      }

      @Override
      public boolean equals(Object obj) {
         if (this == obj) return true;
         if (obj == null || getClass() != obj.getClass()) return false;
         InvokerKey that = InvokerKey.class.cast(obj);
         return equal(this.declaringClass, that.declaringClass)
               && equal(this.name, that.name)
               && equal(this.parametersTypeHashCode, that.parametersTypeHashCode);
      }
   }
   
   @Deprecated
   public GeneratedHttpRequest createRequest(Method method, @Nullable Object... args) {
      List<Object> list = args == null ? Lists.newArrayList(new Object[] { null }) : Lists.newArrayList(args);
      return createRequest(method, methods.getUnchecked(method), list);
   }
   
   public GeneratedHttpRequest createRequest(Method method, Invokable<?, ?> invoker, List<Object> args) {
      checkNotNull(method, "method");
      checkNotNull(invoker, "invoker");
      checkNotNull(args, "args");
      inputParamValidator.validateMethodParametersOrThrow(invoker, args);

      Optional<URI> endpoint = Optional.absent();
      HttpRequest r = findOrNull(args, HttpRequest.class);
      if (r != null) {
         endpoint = Optional.fromNullable(r.getEndpoint());
         if (endpoint.isPresent())
            logger.trace("using endpoint %s from args for %s", endpoint, cma(invoker, args));
      } else {
         endpoint = findEndpoint(invoker, args);
      }

      if (!endpoint.isPresent())
         throw new NoSuchElementException(format("no endpoint found for %s", cma(invoker, args)));

      GeneratedHttpRequest.Builder requestBuilder = requestBuilder();
      if (r != null) {
         requestBuilder.fromHttpRequest(r);
      } else {
         requestBuilder.method(tryFindHttpMethod(invoker).get());
      }
      
      requestBuilder.declaring(declaring)
                    .javaMethod(method)
                    .invoker(invoker)
                    .args(args)
                    .filters(getFiltersIfAnnotated(invoker));
      
      Multimap<String, Object> tokenValues = LinkedHashMultimap.create();

      tokenValues.put(Constants.PROPERTY_API_VERSION, apiVersion);
      tokenValues.put(Constants.PROPERTY_BUILD_VERSION, buildVersion);
      
      UriBuilder uriBuilder = uriBuilder(endpoint.get().toString()); // URI template in rfc6570 form
      
      overridePathEncoding(uriBuilder, invoker);
      
      tokenValues.putAll(addPathAndGetTokens(declaring, invoker, args, uriBuilder));
      
      Multimap<String, Object> formParams = addFormParams(tokenValues, invoker, args);
      Multimap<String, Object> queryParams = addQueryParams(tokenValues, invoker, args);
      Multimap<String, String> headers = buildHeaders(tokenValues, invoker, args);

      if (r != null)
         headers.putAll(r.getHeaders());

      if (shouldAddHostHeader(invoker)) {
         StringBuilder hostHeader = new StringBuilder(endpoint.get().getHost());
         if (endpoint.get().getPort() != -1)
            hostHeader.append(":").append(endpoint.get().getPort());
         headers.put(HOST, hostHeader.toString());
      }

      Payload payload = null;
      for(HttpRequestOptions options : findOptionsIn(invoker, args)) {
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
         PayloadEnclosing payloadEnclosing = findOrNull(args, PayloadEnclosing.class);
         payload = (payloadEnclosing != null) ? payloadEnclosing.getPayload() : findOrNull(args, Payload.class);
      }

      List<? extends Part> parts = getParts(invoker, args, ImmutableMultimap.<String, Object> builder()
                                                                           .putAll(tokenValues)
                                                                           .putAll(formParams).build());
      
      if (parts.size() > 0) {
         if (formParams.size() > 0) {
            parts = newLinkedList(concat(transform(formParams.entries(), ENTRY_TO_PART), parts));
         }
         payload = new MultipartForm(MultipartForm.BOUNDARY, parts);
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

      org.jclouds.rest.MapBinder mapBinder = getMapPayloadBinderOrNull(invoker, args);
      if (mapBinder != null) {
         Map<String, Object> mapParams = buildPayloadParams(invoker, args);
         if (invoker.isAnnotationPresent(PayloadParams.class)) {
            PayloadParams params = invoker.getAnnotation(PayloadParams.class);
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

   private <T> T findOrNull(Iterable<Object> args, Class<T> clazz) {
      return clazz.cast(tryFind(args, instanceOf(clazz)).orNull());
   }
   
   private static <K, V> Map<K, V> convertUnsafe(Multimap<K, V> in) {
      LinkedHashMap<K, V> out = Maps.newLinkedHashMap();
      for (Entry<K, V> entry : in.entries()) {
         out.put(entry.getKey(), entry.getValue());
      }
      return ImmutableMap.copyOf(out);
   }
   
   protected org.jclouds.rest.internal.GeneratedHttpRequest.Builder requestBuilder() {
      return GeneratedHttpRequest.builder();
   }

   private void overridePathEncoding(UriBuilder uriBuilder, Invokable<?, ?> method) {
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
   }
   
   protected Optional<URI> findEndpoint(Invokable<?,?> method, List<Object> args) {
      ClassInvokerArgs cma = cma(method, args);
      Optional<URI> endpoint = getEndpointFor(method, args);
      if (endpoint.isPresent())
         logger.trace("using endpoint %s for %s", endpoint, cma);
      if (!endpoint.isPresent()) {
         logger.trace("looking up default endpoint for %s", cma);
         endpoint = Optional.fromNullable(injector.getInstance(
               Key.get(uriSupplierLiteral, org.jclouds.location.Provider.class)).get());
         if (endpoint.isPresent())
            logger.trace("using default endpoint %s for %s", endpoint, cma);
      }
      return endpoint;
   }

   protected Multimap<String, Object> addPathAndGetTokens(Class<?> clazz, Invokable<?, ?> method, List<Object> args,
         UriBuilder uriBuilder) {
      if (clazz.isAnnotationPresent(Path.class))
         uriBuilder.appendPath(clazz.getAnnotation(Path.class).value());
      if (method.isAnnotationPresent(Path.class))
         uriBuilder.appendPath(method.getAnnotation(Path.class).value());
      return getPathParamKeyValues(method, args);
   }

   private Multimap<String, Object> addFormParams(Multimap<String, ?> tokenValues, Invokable<?, ?> method,
         List<Object> args) {
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

   private Multimap<String, Object> addQueryParams(Multimap<String, ?> tokenValues, Invokable<?, ?> method,
         List<Object> args) {
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

   private List<HttpRequestFilter> getFiltersIfAnnotated(Invokable<?,?> method) {
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

   @Deprecated
   public static URI getEndpointInParametersOrNull(Method method, @Deprecated Object[] args, Injector injector) {
      return getEndpointInParametersOrNull(methods.getUnchecked(method), args != null ? Lists.newArrayList(args)
            : ImmutableList.of(), injector);
   }
   
   private static URI getEndpointInParametersOrNull(Invokable<?,?> method, List<Object> args, Injector injector) {
      Collection<Parameter> endpointParams = parametersWithAnnotation(method, EndpointParam.class);
      if (endpointParams.isEmpty())
         return null;
      checkState(endpointParams.size() == 1, "method %s has too many EndpointParam annotations", method);
      Parameter endpointParam = get(endpointParams, 0);
      Function<Object, URI> parser = injector.getInstance(endpointParam.getAnnotation(EndpointParam.class).parser());
      int position = endpointParam.hashCode();// guava issue 1243
      try {
         URI returnVal = parser.apply(args.get(position));
         checkArgument(returnVal != null, format("endpoint for [%s] not configured for %s", position, method));
         return returnVal;
      } catch (NullPointerException e) {
         throw new IllegalArgumentException(format("argument at index %d on method %s was null", position, method), e);
      }
   }

   private static Collection<Parameter> parametersWithAnnotation(Invokable<?, ?> method, final Class<? extends Annotation> annotationType) {
      return filter(method.getParameters(), new Predicate<Parameter>() {
         public boolean apply(Parameter in) {
            return in.isAnnotationPresent(annotationType);
         }
      });
   }

   private static final TypeLiteral<Supplier<URI>> uriSupplierLiteral = new TypeLiteral<Supplier<URI>>() {
   };

   protected Optional<URI> getEndpointFor(Invokable<?,?> method, List<Object> args) {
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
      URI provider = injector.getInstance(Key.get(uriSupplierLiteral, org.jclouds.location.Provider.class)).get();
      return Optional.fromNullable(addHostIfMissing(endpoint, provider));
   }

   @VisibleForTesting
   @Deprecated
   static URI addHostIfMissing(URI original, URI withHost) {
      checkNotNull(withHost, "URI withHost cannot be null");
      checkArgument(withHost.getHost() != null, "URI withHost must have host:" + withHost);
      if (original == null)
         return null;
      if (original.getHost() != null)
         return original;
      return withHost.resolve(original);
   }

   private org.jclouds.rest.MapBinder getMapPayloadBinderOrNull(Invokable<?,?> method, List<Object> args) {
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

   private boolean shouldAddHostHeader(Invokable<?, ?> method) {
      return (declaring.isAnnotationPresent(VirtualHost.class) || method.isAnnotationPresent(VirtualHost.class));
   }
   
   private GeneratedHttpRequest decorateRequest(GeneratedHttpRequest request) throws NegativeArraySizeException {
      Set<Parameter> binderOrWrapWith = ImmutableSet.copyOf(concat(parametersWithAnnotation(request.getInvoker(), BinderParam.class),
            parametersWithAnnotation(request.getInvoker(), WrapWith.class)));

      OUTER: for (Parameter entry : binderOrWrapWith) {
         int position = entry.hashCode();
         boolean shouldBreak = false;
         Binder binder;
         if (entry.isAnnotationPresent(BinderParam.class))
            binder = injector.getInstance(entry.getAnnotation(BinderParam.class).value());
         else
            binder = injector.getInstance(BindToJsonPayloadWrappedWith.Factory.class).create(
                  entry.getAnnotation(WrapWith.class).value());
         Object arg = request.getArgs().size() >= position + 1 ? request.getArgs().get(position) : null;
         if (request.getArgs().size() >= position + 1 && arg != null) {
            Class<?> parameterType = entry.getType().getRawType();
            Class<? extends Object> argType = arg.getClass();
            if (!argType.isArray() && parameterType.isArray()) { // TODO && varargs guava issue 1244
               int arrayLength = request.getArgs().size() - request.getInvoker().getParameters().size() + 1;
               if (arrayLength == 0)
                  break OUTER;
               arg = (Object[]) Array.newInstance(arg.getClass(), arrayLength);
               System.arraycopy(request.getArgs().toArray(), position, arg, 0, arrayLength);
               shouldBreak = true;
            } else if (argType.isArray() && parameterType.isArray()) { // TODO && varargs guava issue 1244
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
            if (position + 1 == request.getInvoker().getParameters().size() && entry.getType().isArray())
               continue OUTER;  // TODO should only skip on null when varargs: guava issue 1244

            if (entry.isAnnotationPresent(Nullable.class)) {
               continue OUTER;
            }
            checkNotNull(arg, request.getInvoker().getName() + " parameter " + (position + 1));
         }
      }
      return request;
   }

   private static final LoadingCache<Invokable<?, ?>, Set<Integer>> methodToIndexesOfOptions = CacheBuilder.newBuilder().build(
         new CacheLoader<Invokable<?, ?>, Set<Integer>>() {
            @Override
            public Set<Integer> load(Invokable<?, ?> method) {
               Builder<Integer> toReturn = ImmutableSet.builder();
               for (Parameter param : method.getParameters()) {
                  Class<?> type = param.getType().getRawType();
                  if (HttpRequestOptions.class.isAssignableFrom(type)
                        || HttpRequestOptions[].class.isAssignableFrom(type))
                     toReturn.add(param.hashCode()); // TODO position guava issue 1243
               }
               return toReturn.build();
            }
         });
   
  private Set<HttpRequestOptions> findOptionsIn(Invokable<?,?> method, List<Object> args) {
     ImmutableSet.Builder<HttpRequestOptions> result = ImmutableSet.builder();
     for (int index : methodToIndexesOfOptions.getUnchecked(method)) {
         if (args.size() >= index + 1) {// accommodate varargs
            if (args.get(index) instanceof Object[]) {
               for (Object option : (Object[]) args.get(index)) {
                  if (option instanceof HttpRequestOptions) {
                     result.add((HttpRequestOptions) option);
                  }
               }
            } else {
               for (; index < args.size(); index++) {
                  if (args.get(index) instanceof HttpRequestOptions) {
                     result.add((HttpRequestOptions) args.get(index));
                  }
               }
            }
         }
      }
      return result.build();
   }

   @Deprecated
   public Multimap<String, String> buildHeaders(Multimap<String, ?> tokenValues, Method method, Object... args) {
      return buildHeaders(tokenValues, methods.getUnchecked(method), args != null ? Lists.newArrayList(args)
            : ImmutableList.of());
   }

   private Multimap<String, String> buildHeaders(Multimap<String, ?> tokenValues, Invokable<?, ?> method,
         List<Object> args) {
      Multimap<String, String> headers = LinkedHashMultimap.create();
      addHeaderIfAnnotationPresentOnMethod(headers, method, tokenValues);
      for (Parameter headerParam : parametersWithAnnotation(method, HeaderParam.class)) {
         Annotation key = headerParam.getAnnotation(HeaderParam.class);
         String value = args.get(headerParam.hashCode()).toString(); // TODO position guava issue 1243
         value = replaceTokens(value, tokenValues);
         headers.put(((HeaderParam) key).value(), value);
      }
      addProducesIfPresentOnTypeOrMethod(headers, method);
      addConsumesIfPresentOnTypeOrMethod(headers, method);
      return headers;
   }

   private void addConsumesIfPresentOnTypeOrMethod(Multimap<String, String> headers, Invokable<?,?> method) {
      List<String> accept = getAcceptHeadersOrNull(method);
      if (accept.size() > 0)
         headers.replaceValues(ACCEPT, accept);
   }
   
   // TODO: refactor this out
   @VisibleForTesting
   static List<String> getAcceptHeadersOrNull(Invokable<?,?> method) {
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

   private void addProducesIfPresentOnTypeOrMethod(Multimap<String, String> headers, Invokable<?,?> method) {
      if (declaring.isAnnotationPresent(Produces.class)) {
         Produces header = declaring.getAnnotation(Produces.class);
         headers.replaceValues(CONTENT_TYPE, asList(header.value()));
      }
      if (method.isAnnotationPresent(Produces.class)) {
         Produces header = method.getAnnotation(Produces.class);
         headers.replaceValues(CONTENT_TYPE, asList(header.value()));
      }
   }

   private void addHeaderIfAnnotationPresentOnMethod(Multimap<String, String> headers, Invokable<?,?> method,
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

   private List<Part> getParts(Invokable<?, ?> method, List<Object> args, Multimap<String, ?> tokenValues) {
      ImmutableList.Builder<Part> parts = ImmutableList.<Part> builder();
      for (Parameter param : parametersWithAnnotation(method, PartParam.class)) {
         PartParam partParam = param.getAnnotation(PartParam.class);
         PartOptions options = new PartOptions();
         if (!PartParam.NO_CONTENT_TYPE.equals(partParam.contentType()))
            options.contentType(partParam.contentType());
         if (!PartParam.NO_FILENAME.equals(partParam.filename()))
            options.filename(replaceTokens(partParam.filename(), tokenValues));
         Object arg = args.get(param.hashCode()); // TODO position guava issue 1243
         checkNotNull(arg, partParam.name());
         Part part = Part.create(partParam.name(), newPayload(arg), options);
         parts.add(part);
      }
      return parts.build();
   }

   private Multimap<String, Object> getPathParamKeyValues(Invokable<?, ?> method, List<Object> args) {
      Multimap<String, Object> pathParamValues = LinkedHashMultimap.create();
      for (Parameter param : parametersWithAnnotation(method, PathParam.class)) {
         PathParam pathParam = param.getAnnotation(PathParam.class);
         String paramKey = pathParam.value();
         Optional<?> paramValue = getParamValue(method, args, param.getAnnotation(ParamParser.class), param.hashCode(),
               paramKey); // TODO position guava issue 1243
         if (paramValue.isPresent())
            pathParamValues.put(paramKey, paramValue.get().toString());
      }
      return pathParamValues;
   }

   private Optional<?> getParamValue(Invokable<?,?> method, List<Object> args, @Nullable ParamParser extractor,
            int argIndex, String paramKey) {
      Object arg = args.get(argIndex);
      if (extractor != null && checkPresentOrNullable(method, paramKey, argIndex, arg)) {
         arg = injector.getInstance(extractor.value()).apply(arg); // ParamParsers can deal with nullable parameters
      }
      checkPresentOrNullable(method, paramKey, argIndex, arg);
      return Optional.fromNullable(arg);
   }

   private static boolean checkPresentOrNullable(Invokable<?,?> method, String paramKey, int argIndex, Object arg) {
      if (arg == null && !method.getParameters().get(argIndex).isAnnotationPresent(Nullable.class))
         throw new NullPointerException(format("param{%s} for method %s.%s", paramKey, method
                  .getDeclaringClass().getSimpleName(), method.getName()));
      return true;
   }

   private Multimap<String, Object> getFormParamKeyValues(Invokable<?,?> method, List<Object> args) {
      Multimap<String, Object> formParamValues = LinkedHashMultimap.create();
      for (Parameter param : parametersWithAnnotation(method, FormParam.class)) {
         FormParam formParam = param.getAnnotation(FormParam.class);
         String paramKey = formParam.value();
         Optional<?> paramValue = getParamValue(method, args, param.getAnnotation(ParamParser.class), param.hashCode(),
               paramKey); // TODO position guava issue 1243
         if (paramValue.isPresent())
            formParamValues.put(paramKey, paramValue.get().toString());
      }
      return formParamValues;
   }

   private Multimap<String, Object> getQueryParamKeyValues(Invokable<?,?> method, List<Object> args) {
      Multimap<String, Object> queryParamValues = LinkedHashMultimap.create();
      for (Parameter param : parametersWithAnnotation(method, QueryParam.class)) {
         QueryParam queryParam = param.getAnnotation(QueryParam.class);
         String paramKey = queryParam.value();
         Optional<?> paramValue = getParamValue(method, args, param.getAnnotation(ParamParser.class), param.hashCode(),
               paramKey); // TODO position guava issue 1243
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

   private Map<String, Object> buildPayloadParams(Invokable<?,?> method, List<Object> args) {
      Map<String, Object> payloadParamValues = Maps.newLinkedHashMap();
      for (Parameter param : parametersWithAnnotation(method, PayloadParam.class)) {
         PayloadParam payloadParam = param.getAnnotation(PayloadParam.class);
         String paramKey = payloadParam.value();
         Optional<?> paramValue = getParamValue(method, args, param.getAnnotation(ParamParser.class), param.hashCode(),
               paramKey); // TODO position guava issue 1243
         if (paramValue.isPresent())
            payloadParamValues.put(paramKey, paramValue.get());
      }
      return payloadParamValues;
   }
}
