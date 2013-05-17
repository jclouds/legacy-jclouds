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

import org.jclouds.io.Payload;
import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.Multimap;

/**
 * Represents a response produced from {@link HttpCommandExecutorService}
 * 
 * @author Adrian Cole
 */
public class HttpResponse extends HttpMessage {

   public static Builder<?> builder() { 
      return new ConcreteBuilder();
   }
   
   public Builder<?> toBuilder() { 
      return new ConcreteBuilder().fromHttpResponse(this);
   }

   public abstract static class Builder<T extends Builder<T>> extends HttpMessage.Builder<T>  {
      protected int statusCode;
      protected String message;

      /** 
       * @see HttpResponse#getStatusCode()
       */
      public T statusCode(int statusCode) {
         this.statusCode = statusCode;
         return self();
      }

      /** 
       * @see HttpResponse#getMessage()
       */
      public T message(@Nullable String message) {
         this.message = message;
         return self();
      }

      public HttpResponse build() {
         return new HttpResponse(statusCode, message, headers.build(), payload);
      }
      
      public T fromHttpResponse(HttpResponse in) {
         return super.fromHttpMessage(in)
                     .statusCode(in.getStatusCode())
                     .message(in.getMessage());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }
   
   private final int statusCode;
   private final String message;

   protected HttpResponse(int statusCode, @Nullable String message, Multimap<String, String> headers, @Nullable Payload payload) {
      super(headers, payload);
      this.statusCode = statusCode;
      this.message = message;
   }

   public int getStatusCode() {
      return statusCode;
   }

   @Nullable
   public String getMessage() {
      return message;
   }
   
   public String getStatusLine() {
      return String.format("HTTP/1.1 %d %s", getStatusCode(), getMessage());
   }
   
   @Override
   public int hashCode() {
      return Objects.hashCode(statusCode, message, super.hashCode());
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      // testing equals by value, not by type
      if (!(obj instanceof HttpResponse)) return false;
      HttpResponse that = HttpResponse.class.cast(obj);
      return super.equals(that) 
               && Objects.equal(this.statusCode, that.statusCode)
               && Objects.equal(this.message, that.message);
   }
   
   @Override
   protected ToStringHelper string() {
      return Objects.toStringHelper("").omitNullValues()
                    .add("statusCode", statusCode)
                    .add("message", message)
                    .add("headers", headers)
                    .add("payload", payload);
   }

}
