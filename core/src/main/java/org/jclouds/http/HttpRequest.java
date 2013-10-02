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
package org.jclouds.http;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.http.Uris.uriBuilder;
import static org.jclouds.http.utils.Queries.queryParser;
import static org.jclouds.io.Payloads.newUrlEncodedFormPayload;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jclouds.io.Payload;
import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

/**
 * Represents a request that can be executed within {@link HttpCommandExecutorService}
 * 
 * @author Adrian Cole
 */
public class HttpRequest extends HttpMessage {

   public static final Set<String> NON_PAYLOAD_METHODS = ImmutableSet
         .of("OPTIONS", "GET", "HEAD", "DELETE", "TRACE", "CONNECT");

   public static Builder<?> builder() { 
      return new ConcreteBuilder();
   }
   
   public Builder<?> toBuilder() { 
      return new ConcreteBuilder().fromHttpRequest(this);
   }

   public abstract static class Builder<T extends Builder<T>> extends HttpMessage.Builder<T>  {
      protected String method;
      protected URI endpoint;
      protected ImmutableList.Builder<HttpRequestFilter> filters = ImmutableList.<HttpRequestFilter>builder();
   
      /** 
       * @see HttpRequest#getMethod()
       */
      public T method(String method) {
         this.method = checkNotNull(method, "method");
         return self();
      }

      /** 
       * @see HttpRequest#getEndpoint()
       */
      public T endpoint(URI endpoint) {
         this.endpoint = checkNotNull(endpoint, "endpoint");
         return self();
      }

      /** 
       * @see HttpRequest#getEndpoint()
       */
      public T endpoint(String endpoint) {
         return endpoint(URI.create(checkNotNull(endpoint, "endpoint")));
      }
      
      /**
       * @see HttpRequest#getEndpoint()
       */
      public T addQueryParam(String name, Iterable<String> values) {
         endpoint = uriBuilder(endpoint).addQuery(name, values).build();
         return self();
      }
      
      /**
       * @see HttpRequest#getEndpoint()
       */
      public T addQueryParam(String name, String... values) {
         endpoint = uriBuilder(endpoint).addQuery(name, values).build();
         return self();
      }

      /**
       * @see HttpRequest#getEndpoint()
       */
      public T addQueryParams(Multimap<String, String> parameters) {
         endpoint = uriBuilder(endpoint).addQuery(parameters).build();
         return self();
      }
      
      /**
       * @see HttpRequest#getEndpoint()
       */
      public T replaceQueryParam(String name, Iterable<String> values) {
         endpoint = uriBuilder(endpoint).replaceQuery(name, values).build();
         return self();
      }
      
      /**
       * @see HttpRequest#getEndpoint()
       */
      public T replaceQueryParam(String name, String... values) {
         endpoint = uriBuilder(endpoint).replaceQuery(name, values).build();
         return self();
      }
      
      /**
       * @see HttpRequest#getEndpoint()
       */
      public T replaceQueryParams(Map<String, String> parameters) {
         return replaceQueryParams(Multimaps.forMap(parameters));
      }
      
      /**
       * @see HttpRequest#getEndpoint()
       */
      public T replaceQueryParams(Multimap<String, String> parameters) {
         endpoint = uriBuilder(endpoint).replaceQuery(parameters).build();
         return self();
      }

      /**
       * @see HttpRequest#getEndpoint()
       */
      public T replacePath(String path) {
         checkNotNull(endpoint, "endpoint");
         checkNotNull(path, "path");
         endpoint = uriBuilder(endpoint).path(path).build();
         return self();
      }
      
      /**
       * @see #addFormParams
       */
      public T addFormParam(String name, String... values) {
         return addFormParams(ImmutableMultimap.<String, String> builder()
                  .putAll(checkNotNull(name, "name"), checkNotNull(values, "values of %s", name)).build());
      }

      /**
       * Replaces the current payload with one that is a urlencoded payload including the following
       * parameters and any formerly set.
       * 
       * @see HttpRequest#getPayload()
       */
      public T addFormParams(Multimap<String, String> parameters) {
         checkNotNull(endpoint, "endpoint");
         Multimap<String, String> map = payload != null ? queryParser().apply(payload.getRawContent().toString())
               : LinkedHashMultimap.<String, String> create();
         map.putAll(parameters);
         payload = newUrlEncodedFormPayload(map);
         return self();
      }

      /**
       * @see #replaceFormParams
       */
      public T replaceFormParam(String name, String... values) {
         return replaceFormParams(ImmutableMultimap.<String, String> builder()
                  .putAll(checkNotNull(name, "name"), checkNotNull(values, "values of %s", name)).build());
      }

      /**
       * Replaces the current payload with one that is a urlencoded payload including the following
       * parameters and any formerly set.
       * 
       * @see HttpRequest#getPayload()
       */
      public T replaceFormParams(Multimap<String, String> parameters) {
         checkNotNull(endpoint, "endpoint");
         Multimap<String, String> map = payload != null ? queryParser().apply(payload.getRawContent().toString())
               : LinkedHashMultimap.<String, String> create();
         for (Map.Entry<String, Collection<String>> entry : parameters.asMap().entrySet()) {
            map.replaceValues(entry.getKey(), entry.getValue());
         }
         payload = newUrlEncodedFormPayload(map);
         return self();
      }

      /** 
       * @see HttpRequest#getFilters()
       */
      public T filters(Iterable<HttpRequestFilter> filters) {
         this.filters = ImmutableList.<HttpRequestFilter>builder();
         this.filters.addAll(checkNotNull(filters, "filters"));
         return self();
      }

      /** 
       * @see HttpRequest#getFilters()
       */
      public T filter(HttpRequestFilter filter) {
         this.filters.add(checkNotNull(filter, "filter"));
         return self();
      }

      public HttpRequest build() {
         return new HttpRequest(method, endpoint, headers.build(), payload, filters.build());
      }
      
      public T fromHttpRequest(HttpRequest in) {
         return super.fromHttpMessage(in)
                     .method(in.getMethod())
                     .endpoint(in.getEndpoint())
                     .filters(in.getFilters());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }
   
   private final String method;
   private final URI endpoint;
   private final List<HttpRequestFilter> filters;

   protected HttpRequest(String method, URI endpoint, Multimap<String, String> headers, @Nullable Payload payload,
         Iterable<HttpRequestFilter> filters) {
      super(headers, payload);
      this.method = checkNotNull(method, "method");
      this.endpoint = checkNotNull(endpoint, "endpoint");
      checkArgument(endpoint.getHost() != null, String.format("endpoint.getHost() is null for %s", endpoint));
      this.filters = ImmutableList.<HttpRequestFilter> copyOf(checkNotNull(filters, "filters"));
   }

   public String getRequestLine() {
      return String.format("%s %s HTTP/1.1", getMethod(), getEndpoint().toASCIIString());
   }

   /**
    * We cannot return an enum, as per specification custom methods are allowed. Enums are not
    * extensible.
    * 
    * @see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec5.html#sec5.1.1" >rfc2616</a>
    */
   public String getMethod() {
      return method;
   }

   public URI getEndpoint() {
      return endpoint;
   }

   public List<HttpRequestFilter> getFilters() {
      return filters;
   }
   
   @Override
   public int hashCode() {
      return Objects.hashCode(method, endpoint, super.hashCode());
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      // testing equals by value, not by type
      if (!(obj instanceof HttpRequest)) return false;
      HttpRequest that = HttpRequest.class.cast(obj);
      return super.equals(that) && Objects.equal(this.method, that.method)
               && Objects.equal(this.endpoint, that.endpoint);
   }
   
   @Override
   protected ToStringHelper string() {
      return Objects.toStringHelper("").omitNullValues()
                    .add("method", method)
                    .add("endpoint", endpoint)
                    .add("headers", headers)
                    .add("payload", payload);
   }
}
