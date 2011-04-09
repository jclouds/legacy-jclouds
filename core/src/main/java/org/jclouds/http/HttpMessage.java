/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;

import javax.annotation.Nullable;

import org.jclouds.http.internal.PayloadEnclosingImpl;
import org.jclouds.io.Payload;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

/**
 * Represents a request that can be executed within {@link HttpCommandExecutorService}
 * 
 * @author Adrian Cole
 */
public class HttpMessage extends PayloadEnclosingImpl {
   public static Builder<? extends HttpMessage> builder() {
      return new Builder<HttpMessage>();
   }

   public static class Builder<T extends HttpMessage> {
      protected Payload payload;
      protected Multimap<String, String> headers = ImmutableMultimap.of();

      public Builder<T> payload(Payload payload) {
         this.payload = payload;
         return this;
      }

      public Builder<T> headers(Multimap<String, String> headers) {
         this.headers = ImmutableMultimap.copyOf(checkNotNull(headers, "headers"));
         return this;
      }

      @SuppressWarnings("unchecked")
      public T build() {
         return (T) new HttpMessage(payload, headers);
      }

      public static <X extends HttpMessage> Builder<X> from(X input) {
         return new Builder<X>().payload(input.getPayload()).headers(input.getHeaders());
      }
   }

   protected final Multimap<String, String> headers;

   public HttpMessage(@Nullable Payload payload, Multimap<String, String> headers) {
      super(payload);
      this.headers = ImmutableMultimap.copyOf(checkNotNull(headers, "headers"));
   }

   public Multimap<String, String> getHeaders() {
      return headers;
   }

   /**
    * try to get the value, then try as lowercase.
    */
   public String getFirstHeaderOrNull(String string) {
      Collection<String> values = headers.get(string);
      if (values.size() == 0)
         values = headers.get(string.toLowerCase());
      return (values.size() >= 1) ? values.iterator().next() : null;
   }

   public Builder<? extends HttpMessage> toBuilder() {
      return Builder.from(this);
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((headers == null) ? 0 : headers.hashCode());
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
      HttpMessage other = (HttpMessage) obj;
      if (headers == null) {
         if (other.headers != null)
            return false;
      } else if (!headers.equals(other.headers))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[headers=" + headers + ", payload=" + payload + "]";
   }

}
