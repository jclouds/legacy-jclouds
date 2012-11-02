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
package org.jclouds.http;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.http.utils.Queries.makeQueryLine;
import static org.jclouds.http.utils.Queries.parseQueryToMap;
import static org.jclouds.io.Payloads.newUrlEncodedFormPayload;
import static org.jclouds.util.Multimaps2.replaceEntries;

import java.net.URI;
import java.util.List;

import org.jclouds.io.Payload;
import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.common.primitives.Chars;
import com.sun.jersey.api.uri.UriBuilderImpl;

/**
 * Represents a request that can be executed within {@link HttpCommandExecutorService}
 * 
 * @author Adrian Cole
 */
public class HttpRequest extends HttpMessage {

   public static Builder<?> builder() { 
      return new ConcreteBuilder();
   }
   
   public Builder<?> toBuilder() { 
      return new ConcreteBuilder().fromHttpRequest(this);
   }

   public abstract static class Builder<T extends Builder<T>> extends HttpMessage.Builder<T>  {
      protected String method;
      protected URI endpoint;
      protected ImmutableList.Builder<Character> skips = ImmutableList.<Character>builder();
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
         return addQueryParam(name, Iterables.toArray(checkNotNull(values, "values of %s", name), String.class));
      }
      
      /**
       * @see HttpRequest#getEndpoint()
       */
      public T addQueryParam(String name, String... values) {
         return addQueryParams(ImmutableMultimap.<String, String> builder()
                  .putAll(checkNotNull(name, "name"), checkNotNull(values, "values of %s", name)).build());
      }

      /**
       * @see HttpRequest#getEndpoint()
       */
      public T addQueryParams(Multimap<String, String> parameters) {
         checkNotNull(endpoint, "endpoint");
         Multimap<String, String> map = parseQueryToMap(endpoint.getQuery());
         map = ImmutableMultimap.<String, String>builder().putAll(map).putAll(parameters).build();
         return replaceQuery(map);
      }
      
      /**
       * @see HttpRequest#getEndpoint()
       */
      public T replaceQueryParam(String name, Iterable<String> values) {
         return replaceQueryParam(name, Iterables.toArray(checkNotNull(values, "values of %s", name), String.class));
      }
      
      /**
       * @see HttpRequest#getEndpoint()
       */
      public T replaceQueryParam(String name, String... values) {
         return replaceQueryParams(ImmutableMultimap.<String, String> builder()
                  .putAll(checkNotNull(name, "name"), checkNotNull(values, "values of %s", name)).build());
      }

      /**
       * @see HttpRequest#getEndpoint()
       */
      public T replaceQueryParams(Multimap<String, String> parameters) {
         checkNotNull(endpoint, "endpoint");
         Multimap<String, String> map = replaceEntries(parseQueryToMap(endpoint.getRawQuery()), parameters);
         return replaceQuery(map);
      }

      @SuppressWarnings("unchecked")
      private T replaceQuery(Multimap<String, String> map) {
         URI oldURI = endpoint;
         String query = makeQueryLine(map, null, Chars.toArray(skips.build()));
         endpoint = new UriBuilderImpl().uri(oldURI).replaceQuery(query).buildFromEncodedMap(ImmutableMap.<String, Object>of());
         return self();
      }

      /**
       * @see HttpRequest#getEndpoint()
       */
      @SuppressWarnings("unchecked")
      public T replacePath(String path) {
         checkNotNull(endpoint, "endpoint");
         checkNotNull(path, "path");
         URI oldURI = endpoint;
         endpoint = new UriBuilderImpl().uri(oldURI).replacePath(path).buildFromEncodedMap(ImmutableMap.<String, Object>of());
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
         Multimap<String, String> map = payload != null ? parseQueryToMap(payload.getRawContent()
                  .toString()) : ImmutableMultimap.<String, String> of();
         map = ImmutableMultimap.<String, String>builder().putAll(map).putAll(parameters).build();
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
         Multimap<String, String> map = replaceEntries(payload != null ? parseQueryToMap(payload.getRawContent()
                  .toString()) : ImmutableMultimap.<String, String> of(), parameters);
         payload = newUrlEncodedFormPayload(map);
         return self();
      }

      /** 
       * @see HttpRequest#getSkips()
       */
      public T skips(char[] skips) {
         this.skips = ImmutableList.<Character>builder();
         this.skips.addAll(Chars.asList(checkNotNull(skips, "skips")));
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
         return new HttpRequest(method, endpoint, headers.build(), payload, skips.build(), filters.build());
      }
      
      public T fromHttpRequest(HttpRequest in) {
         return super.fromHttpMessage(in)
                     .method(in.getMethod())
                     .endpoint(in.getEndpoint())
                     .skips(in.getSkips())
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
   private final List<Character> skips;
   private final List<HttpRequestFilter> filters;

   protected HttpRequest(String method, URI endpoint, Multimap<String, String> headers, @Nullable Payload payload,
            Iterable<Character> skips, Iterable<HttpRequestFilter> filters) {
      super(headers, payload);
      this.method = checkNotNull(method, "method");
      this.endpoint = checkNotNull(endpoint, "endpoint");
      checkArgument(endpoint.getHost() != null, String.format("endpoint.getHost() is null for %s", endpoint));
      this.skips = ImmutableList.<Character> copyOf(checkNotNull(skips, "skips"));
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

   /**
    * characters to skip encoding on.
    */
   public char[] getSkips() {
      return Chars.toArray(skips);
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
