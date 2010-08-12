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
import static com.google.common.collect.Lists.newArrayList;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import org.jclouds.io.Payload;

import com.google.common.collect.Multimap;

/**
 * Represents a request that can be executed within
 * {@link HttpCommandExecutorService}
 * 
 * @author Adrian Cole
 */
public class HttpRequest extends HttpMessage {

   private List<HttpRequestFilter> requestFilters = newArrayList();
   private String method;
   private URI endpoint;
   private char[] skips;

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
      this.setMethod(checkNotNull(method, "method"));
      this.setEndpoint(checkNotNull(endpoint, "endpoint"));
      checkArgument(endpoint.getHost() != null, String.format("endpoint.getHost() is null for %s", endpoint));
      this.skips = skips;
   }

   /**
    * 
    * @param endpoint
    *           This may change over the life of the request due to redirects.
    * @param method
    *           If the request is HEAD, this may change to GET due to redirects
    */
   public HttpRequest(String method, URI endpoint, Multimap<String, String> headers) {
      this(method, endpoint);
      getHeaders().putAll(checkNotNull(headers, "headers"));
   }

   /**
    * 
    * @param endpoint
    *           This may change over the life of the request due to redirects.
    * @param method
    *           If the request is HEAD, this may change to GET due to redirects
    */
   protected HttpRequest(String method, URI endpoint, Multimap<String, String> headers, @Nullable Payload payload) {
      this(method, endpoint);
      getHeaders().putAll(checkNotNull(headers, "headers"));
      setPayload(payload);
   }

   public String getRequestLine() {
      return String.format("%s %s HTTP/1.1", getMethod(), getEndpoint().toASCIIString());
   }

   /**
    * We cannot return an enum, as per specification custom methods are allowed.
    * Enums are not extensible.
    * 
    * @see <a
    *      href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec5.html#sec5.1.1"
    *      >rfc2616</a>
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

   public void setSkips(char[] skips) {
      this.skips = skips;
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

   public void setMethod(String method) {
      this.method = method;
   }

   public void setEndpoint(URI endpoint) {
      this.endpoint = endpoint;
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
      return "[method=" + method + ", endpoint=" + endpoint + ", headers=" + headers + ", payload=" + payload + "]";
   }

}
