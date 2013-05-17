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
package org.jclouds.ultradns.ws;

import static com.google.common.base.Preconditions.checkState;
import static java.lang.String.format;

import javax.inject.Inject;

import org.jclouds.http.HttpException;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;
import org.jclouds.rest.annotations.Transform;

import com.google.common.base.Function;
import com.google.inject.AbstractModule;

/**
 * Adds support for implicit transactions.
 * 
 * @author Adrian Cole
 */
public class ScopedTransaction {

   /**
    * Sets a scoped transaction. Add to rest calls that start a transaction.
    * Make sure that these note a possible IllegalStateException when a
    * transaction is already in progress.
    * 
    * <p/>
    * 
    * ex.
    * 
    * <pre>
    * ...
    * @Transform(ScopedTransaction.Set.class)
    * String start() throws IllegalStateException;
    * </pre>
    * 
    * @see Transform
    */
   public static class Set implements Function<String, String> {
      private final ScopedTransaction scope;

      @Inject
      private Set(ScopedTransaction scope) {
         this.scope = scope;
      }

      @Override
      public String apply(String in) {
         checkState(scope.transactionId.get() == null, "A transaction is already in progress");
         scope.transactionId.set(in);
         return in;
      }
   }

   /**
    * Removes a scoped transaction, if present. Add to rest calls that commit or
    * rollback a transaction.
    * 
    * <p/>
    * 
    * ex.
    * 
    * <pre>
    * ...
    * @Transform(ScopedTransaction.Invalidate.class)
    * void rollback(String txId);
    * </pre>
    * 
    * @see Transform
    */
   public static class Remove implements Function<Void, Void> {
      private final ScopedTransaction scope;

      @Inject
      private Remove(ScopedTransaction scope) {
         this.scope = scope;
      }

      @Override
      public Void apply(Void in) {
         scope.transactionId.remove();
         return in;
      }
   }

   public static class Filter implements HttpRequestFilter {
      private final ScopedTransaction scope;

      @Inject
      private Filter(ScopedTransaction scope) {
         this.scope = scope;
      }

      @Override
      public HttpRequest filter(HttpRequest request) throws HttpException {
         String transactionId = scope.transactionId.get();
         if (transactionId == null)
            return request;
         String body = request.getPayload().getRawContent().toString();
         body = body.replace("<transactionID />", format("<transactionID>%s</transactionID>", transactionId));
         return request.toBuilder().payload(body).build();
      }
   }

   /**
    * In order to support implicit transactions, this module must be installed.
    */
   public static class Module extends AbstractModule {
      public void configure() {
         ScopedTransaction scope = new ScopedTransaction();
         bind(ScopedTransaction.class).toInstance(scope);
      }
   }

   private final ThreadLocal<String> transactionId = new ThreadLocal<String>();
}
