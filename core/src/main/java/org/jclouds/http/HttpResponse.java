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

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Nullable;

import org.jclouds.io.Payload;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

/**
 * Represents a response produced from {@link HttpCommandExecutorService}
 * 
 * @author Adrian Cole
 */
public class HttpResponse extends HttpMessage {

   private final int statusCode;
   private final String message;

   public HttpResponse(int statusCode, String message, @Nullable Payload payload) {
      this(statusCode, message, payload, ImmutableMultimap.<String, String> of());
   }

   public HttpResponse(int statusCode, String message, @Nullable Payload payload, Multimap<String, String> headers) {
      super(payload);
      this.statusCode = statusCode;
      this.message = message;
      this.headers.putAll(checkNotNull(headers));
   }

   public int getStatusCode() {
      return statusCode;
   }

   public String getMessage() {
      return message;
   }

   @Override
   public String toString() {
      return "[message=" + message + ", statusCode=" + statusCode + ", headers=" + headers + ", payload=" + payload
            + "]";
   }

   public String getStatusLine() {
      return String.format("HTTP/1.1 %d %s", getStatusCode(), getMessage());
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((payload == null) ? 0 : payload.hashCode());
      result = prime * result + ((headers == null) ? 0 : headers.hashCode());
      result = prime * result + ((message == null) ? 0 : message.hashCode());
      result = prime * result + statusCode;
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
      HttpResponse other = (HttpResponse) obj;
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
      if (message == null) {
         if (other.message != null)
            return false;
      } else if (!message.equals(other.message))
         return false;
      if (statusCode != other.statusCode)
         return false;
      return true;
   }

}