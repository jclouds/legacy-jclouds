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

import java.net.URI;
import java.util.List;

import org.jclouds.io.Payload;
import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

/**
 * Represents a request that can be executed within {@link HttpCommandExecutorService}
 * 
 * @author Adrian Cole
 */
public class HttpRequest extends HttpMessage {
   public static Builder builder() {
      return new Builder();
   }

   public static class Builder extends HttpMessage.Builder {
      protected String method;
      protected URI endpoint;
      protected char[] skips = new char[] {};
      protected List<HttpRequestFilter> requestFilters = ImmutableList.of();

      public Builder filters(List<HttpRequestFilter> requestFilters) {
         this.requestFilters = ImmutableList.copyOf(checkNotNull(requestFilters, "requestFilters"));
         return this;
      }

      public Builder method(String method) {
         this.method = checkNotNull(method, "method");
         return this;
      }

      public Builder endpoint(URI endpoint) {
         this.endpoint = checkNotNull(endpoint, "endpoint");
         return this;
      }

      public Builder skips(char[] skips) {
         char[] retval = new char[checkNotNull(skips, "skips").length];
         System.arraycopy(skips, 0, retval, 0, skips.length);
         this.skips = retval;
         return this;
      }

      @Override
      public Builder payload(Payload payload) {
         return (Builder) super.payload(payload);
      }

      @Override
      public Builder headers(Multimap<String, String> headers) {
         return (Builder) super.headers(headers);
      }

      @Override
      public HttpRequest build() {
         return new HttpRequest(method, endpoint, skips, requestFilters, payload, headers);
      }

      public static Builder from(HttpRequest input) {
         return new Builder().method(input.getMethod()).endpoint(input.getEndpoint()).skips(input.getSkips())
                  .filters(input.getFilters()).payload(input.getPayload()).headers(input.getHeaders());
      }

   }

   private final List<HttpRequestFilter> requestFilters;
   private final String method;
   private final URI endpoint;
   private final char[] skips;

   /**
    * 
    * @param endpoint
    *           This may change over the life of the request due to redirects.
    * @param method
    *           If the request is HEAD, this may change to GET due to redirects
    */
   public HttpRequest(String method, URI endpoint) {
      this(method, endpoint, new char[] {});
   }

   public HttpRequest(String method, URI endpoint, char[] skips) {
      this(method, endpoint, skips, ImmutableList.<HttpRequestFilter> of());
   }

   public HttpRequest(String method, URI endpoint, char[] skips, List<HttpRequestFilter> requestFilters) {
      this(method, endpoint, skips, requestFilters, null);
   }

   public HttpRequest(String method, URI endpoint, char[] skips, List<HttpRequestFilter> requestFilters,
            @Nullable Payload payload) {
      this(method, endpoint, skips, requestFilters, payload, ImmutableMultimap.<String, String> of());
   }

   /**
    * 
    * @param endpoint
    *           This may change over the life of the request due to redirects.
    * @param method
    *           If the request is HEAD, this may change to GET due to redirects
    */
   public HttpRequest(String method, URI endpoint, Multimap<String, String> headers) {
      this(method, endpoint, new char[] {}, ImmutableList.<HttpRequestFilter> of(), null, headers);
   }

   public HttpRequest(String method, URI endpoint, char[] skips, List<HttpRequestFilter> requestFilters,
            @Nullable Payload payload, Multimap<String, String> headers) {
      super(payload, headers);
      this.method = checkNotNull(method, "method");
      this.endpoint = checkNotNull(endpoint, "endpoint");
      checkArgument(endpoint.getHost() != null, String.format("endpoint.getHost() is null for %s", endpoint));
      this.skips = checkNotNull(skips, "skips");
      this.requestFilters = ImmutableList.<HttpRequestFilter> copyOf(checkNotNull(requestFilters, "requestFilters"));
   }

   /**
    * 
    * @param endpoint
    *           This may change over the life of the request due to redirects.
    * @param method
    *           If the request is HEAD, this may change to GET due to redirects
    */
   protected HttpRequest(String method, URI endpoint, Multimap<String, String> headers, @Nullable Payload payload) {
      this(method, endpoint, new char[] {}, ImmutableList.<HttpRequestFilter> of(), payload, headers);
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

   /**
    * characters to skip encoding on.
    */
   public char[] getSkips() {
      return skips;
   }

   public URI getEndpoint() {
      return endpoint;
   }

   public void addFilter(HttpRequestFilter filter) {
      requestFilters.add(filter);
   }

   public List<HttpRequestFilter> getFilters() {
      return requestFilters;
   }

   @Override
   public Builder toBuilder() {
      return Builder.from(this);
   }
   
   @Override
   public int hashCode() {
      return Objects.hashCode(method, endpoint, headers, payload);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (!(obj instanceof HttpRequest))
         return false;
      HttpRequest other = (HttpRequest) obj;
      if (!Objects.equal(method, other.method))
         return false;
      if (!Objects.equal(endpoint, other.endpoint))
         return false;
      if (!Objects.equal(headers, other.headers))
         return false;
      if (!Objects.equal(payload, other.payload))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return String.format("[method=%s, endpoint=%s, headers=%s, payload=%s]", method, endpoint, headers, payload);
   }

}
