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

package org.jclouds.http;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import org.jclouds.io.Payload;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

/**
 * Represents a request that can be executed within {@link HttpCommandExecutorService}
 * 
 * @author Adrian Cole
 */
public class HttpRequest extends HttpMessage {
   public static Builder<? extends HttpRequest> builder() {
      return new Builder<HttpRequest>();
   }

   public static class Builder<T extends HttpRequest> extends HttpMessage.Builder<T> {
      protected String method;
      protected URI endpoint;
      protected char[] skips = new char[] {};
      protected List<HttpRequestFilter> requestFilters = ImmutableList.of();

      public Builder<T> filters(List<HttpRequestFilter> requestFilters) {
         this.requestFilters = ImmutableList.copyOf(checkNotNull(requestFilters, "requestFilters"));
         return this;
      }

      public Builder<T> method(String method) {
         this.method = checkNotNull(method, "method");
         return this;
      }

      public Builder<T> endpoint(URI endpoint) {
         this.endpoint = checkNotNull(endpoint, "endpoint");
         return this;
      }

      public Builder<T> skips(char[] skips) {
         char[] retval = new char[checkNotNull(skips, "skips").length];
         System.arraycopy(skips, 0, retval, 0, skips.length);
         this.skips = retval;
         return this;
      }

      @Override
      public Builder<T> payload(Payload payload) {
         return (Builder<T>) super.payload(payload);
      }

      @Override
      public Builder<T> headers(Multimap<String, String> headers) {
         return (Builder<T>) super.headers(headers);
      }

      @Override
      @SuppressWarnings("unchecked")
      public T build() {
         return (T) new HttpRequest(method, endpoint, skips, requestFilters, payload, headers);
      }

      public static <X extends HttpRequest> Builder<X> from(X input) {
         return new Builder<X>().method(input.getMethod()).endpoint(input.getEndpoint()).skips(input.getSkips())
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
   public Builder<? extends HttpRequest> toBuilder() {
      return Builder.from(this);
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((endpoint == null) ? 0 : endpoint.hashCode());
      result = prime * result + ((method == null) ? 0 : method.hashCode());
      result = prime * result + ((payload == null) ? 0 : payload.hashCode());
      result = prime * result + ((headers == null) ? 0 : headers.hashCode());
      result = prime * result + ((requestFilters == null) ? 0 : requestFilters.hashCode());
      result = prime * result + Arrays.hashCode(skips);
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (!super.equals(obj))
         return false;
      if (getClass() != obj.getClass())
         return false;
      HttpRequest other = (HttpRequest) obj;
      if (endpoint == null) {
         if (other.endpoint != null)
            return false;
      } else if (!endpoint.equals(other.endpoint))
         return false;
      if (method == null) {
         if (other.method != null)
            return false;
      } else if (!method.equals(other.method))
         return false;
      if (payload == null) {
         if (other.payload != null)
            return false;
      } else if (!payload.equals(other.payload))
         return false;
      if (headers == null) {
         if (other.headers != null)
            return false;
      } else if (!headers.equals(other.headers))
         return false;
      if (requestFilters == null) {
         if (other.requestFilters != null)
            return false;
      } else if (!requestFilters.equals(other.requestFilters))
         return false;
      if (!Arrays.equals(skips, other.skips))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return String.format("[method=%s, endpoint=%s, headers=%s, payload=%s]", method, endpoint, headers, payload);
   }

}
