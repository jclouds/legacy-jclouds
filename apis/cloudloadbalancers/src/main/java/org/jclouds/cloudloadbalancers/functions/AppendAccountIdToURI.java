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
package org.jclouds.cloudloadbalancers.functions;

import java.net.URI;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.ws.rs.core.UriBuilder;

import org.jclouds.cloudloadbalancers.reference.RackspaceConstants;

import com.google.common.base.Function;
import com.google.common.base.Supplier;

@Singleton
public final class AppendAccountIdToURI implements Function<Supplier<URI>, Supplier<URI>> {

   private final Supplier<String> accountID;
   private final javax.inject.Provider<UriBuilder> builders;

   @Inject
   public AppendAccountIdToURI(javax.inject.Provider<UriBuilder> builders,
            @Named(RackspaceConstants.PROPERTY_ACCOUNT_ID) Supplier<String> accountID) {
      this.accountID = accountID;
      this.builders = builders;
   }

   @Override
   public Supplier<URI> apply(final Supplier<URI> input) {
      return new Supplier<URI>() {

         @Override
         public URI get() {
            return builders.get().uri(input.get()).path(accountID.get()).build();
         }

         @Override
         public String toString() {
            return "appendAccountId()";
         }

      };
   }
}
