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
package org.jclouds.openstack.keystone.v2_0.functions.internal;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.openstack.v2_0.options.PaginationOptions.Builder.marker;

import java.beans.ConstructorProperties;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.internal.CallerArg0ToPagedIterable;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.json.Json;
import org.jclouds.openstack.keystone.v2_0.KeystoneApi;
import org.jclouds.openstack.keystone.v2_0.domain.PaginatedCollection;
import org.jclouds.openstack.keystone.v2_0.domain.Tenant;
import org.jclouds.openstack.keystone.v2_0.features.TenantApi;
import org.jclouds.openstack.keystone.v2_0.functions.internal.ParseTenants.Tenants;
import org.jclouds.openstack.v2_0.domain.Link;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.inject.TypeLiteral;

/**
 * boiler plate until we determine a better way
 * 
 * @author Adrian Cole
 */
@Beta
@Singleton
public class ParseTenants extends ParseJson<Tenants> {
   static class Tenants extends PaginatedCollection<Tenant> {

      @ConstructorProperties({ "tenants", "tenants_links" })
      protected Tenants(Iterable<Tenant> tenants, Iterable<Link> tenants_links) {
         super(tenants, tenants_links);
      }

   }

   @Inject
   public ParseTenants(Json json) {
      super(json, TypeLiteral.get(Tenants.class));
   }

   public static class ToPagedIterable extends CallerArg0ToPagedIterable<Tenant, ToPagedIterable> {

      private final KeystoneApi api;

      @Inject
      protected ToPagedIterable(KeystoneApi api) {
         this.api = checkNotNull(api, "api");
      }

      @Override
      protected Function<Object, IterableWithMarker<Tenant>> markerToNextForCallingArg0(final String ignored) {
         final TenantApi tenantApi = api.getTenantApi().get();
         return new Function<Object, IterableWithMarker<Tenant>>() {

            @SuppressWarnings("unchecked")
            @Override
            public IterableWithMarker<Tenant> apply(Object input) {
               return IterableWithMarker.class.cast(tenantApi.list(marker(input.toString())));
            }

            @Override
            public String toString() {
               return "listTenants()";
            }
         };
      }

   }

}
