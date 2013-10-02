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

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;

import org.jclouds.functions.ToLowerCase;
import org.jclouds.http.internal.PayloadEnclosingImpl;
import org.jclouds.io.Payload;
import org.jclouds.io.Payloads;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.util.Multimaps2;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

/**
 * Represents a request that can be executed within {@link HttpCommandExecutorService}
 * 
 * @author Adrian Cole
 */
public class HttpMessage extends PayloadEnclosingImpl {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromHttpMessage(this);
   }

   public abstract static class Builder<T extends Builder<T>>  {
      protected abstract T self();

      protected ImmutableMultimap.Builder<String, String> headers = ImmutableMultimap.<String, String>builder();
      protected Payload payload;

      /**
       * @see HttpMessage#getPayload()
       */
      public T payload(Payload payload) {
         this.payload = payload;
         return self();
      }
      
      /**
       * @see HttpMessage#getPayload()
       */
      public T payload(byte [] payload) {
         this.payload = Payloads.newByteArrayPayload(checkNotNull(payload, "payload"));
         return self();
      }
      
      /**
       * @see HttpMessage#getPayload()
       */
      public T payload(File payload) {
         this.payload = Payloads.newFilePayload(checkNotNull(payload, "payload"));
         return self();
      }
      
      /**
       * @see HttpMessage#getPayload()
       */
      public T payload(InputStream payload) {
         this.payload = Payloads.newInputStreamPayload(checkNotNull(payload, "payload"));
         return self();
      }
      
      /**
       * @see HttpMessage#getPayload()
       */
      public T payload(String payload) {
         this.payload = Payloads.newStringPayload(checkNotNull(payload, "payload"));
         return self();
      }

      /**
       * replaces all headers with the the supplied multimap.
       * 
       * @see HttpMessage#getHeaders()
       */
      public T headers(Multimap<String, String> headers) {
         this.headers = ImmutableMultimap.<String, String> builder();
         this.headers.putAll(checkNotNull(headers, "headers"));
         return self();
      }
      
      /**
       * replace all headers that have the same keys as the input multimap
       * 
       * @see HttpMessage#getHeaders()
       */
      public T replaceHeaders(Multimap<String, String> headers) {
         checkNotNull(headers, "headers");
         Multimap<String, String> oldHeaders = this.headers.build();
         this.headers = ImmutableMultimap.<String, String> builder();
         this.headers.putAll(Multimaps2.replaceEntries(oldHeaders, headers));
         return self();
      }
      
      /**
       * replace all headers that have the same keys as the input multimap
       * 
       * @see HttpMessage#getHeaders()
       */
      public T removeHeader(String name) {
         checkNotNull(name, "name");
         Multimap<String, String> oldHeaders = this.headers.build();
         this.headers = ImmutableMultimap.<String, String> builder();
         this.headers.putAll(Multimaps2.withoutKey(oldHeaders, name));
         return self();
      }
      
      /**
       * Note that if there's an existing header of the same name, this will only add the new value,
       * not replace it.
       * 
       * @see HttpMessage#getHeaders()
       */
      public T addHeader(String name, String ... values) {
         this.headers.putAll(checkNotNull(name, "name"), checkNotNull(values, "values of %s", name));
         return self();
      }

      /**
       * Replace header.
       * 
       * @see HttpMessage#getHeaders()
       */
      public T replaceHeader(String name, String ... values) {
         checkNotNull(name, "name");
         checkNotNull(values, "values of %s", name);
         return replaceHeaders(ImmutableMultimap.<String, String> builder().putAll(name, values).build());
      }
      
      public HttpMessage build() {
         return new HttpMessage(headers.build(), payload);
      }

      public T fromHttpMessage(HttpMessage in) {
         return this
               .headers(in.getHeaders())
               .payload(in.getPayload());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   protected final Multimap<String, String> headers;

   protected HttpMessage(Multimap<String, String> headers, @Nullable Payload payload) {
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
      if (values.size() == 0) {
         Multimap<String, String> lowerCaseHeaders = Multimaps2.transformKeys(getHeaders(), new ToLowerCase()); 
         values = lowerCaseHeaders.get(string.toLowerCase());
      }
      return (values.size() >= 1) ? values.iterator().next() : null;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(headers, payload);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      // testing equals by value, not by type
      if (!(obj instanceof HttpMessage)) return false;
      HttpMessage that = HttpMessage.class.cast(obj);
      return Objects.equal(this.headers, that.headers)
            && Objects.equal(this.payload, that.payload);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper("").omitNullValues()
                    .add("headers", headers)
                    .add("payload", payload);
   }

   @Override
   public String toString() {
      return string().toString();
   }
}
