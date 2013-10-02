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
package org.jclouds.openstack.nova.v2_0.functions.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Singleton;

import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseFirstJsonValueNamed;
import org.jclouds.json.internal.GsonWrapper;
import org.jclouds.openstack.nova.v2_0.domain.KeyPair;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.FluentIterable;
import com.google.inject.Inject;
import com.google.inject.TypeLiteral;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class ParseKeyPairs implements Function<HttpResponse, FluentIterable<? extends KeyPair>> {
   private final ParseFirstJsonValueNamed<FluentIterable<Wrapper>> parser;

   private static class Wrapper implements Supplier<KeyPair> {
      private KeyPair keypair;

      @Override
      public KeyPair get() {
         return keypair;
      }

   }

   @Inject
   public ParseKeyPairs(GsonWrapper gsonView) {
      this.parser = new ParseFirstJsonValueNamed<FluentIterable<Wrapper>>(checkNotNull(gsonView, "gsonView"),
               new TypeLiteral<FluentIterable<Wrapper>>() {
               }, "keypairs");
   }

   public FluentIterable<? extends KeyPair> apply(HttpResponse response) {
      checkNotNull(response, "response");
      return parser.apply(response).transform(Suppliers.<KeyPair> supplierFunction());
   }
}
