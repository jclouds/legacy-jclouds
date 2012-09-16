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
import org.jclouds.openstack.keystone.v2_0.domain.User;
import org.jclouds.openstack.keystone.v2_0.features.UserApi;
import org.jclouds.openstack.keystone.v2_0.functions.internal.ParseUsers.Users;
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
public class ParseUsers extends ParseJson<Users<? extends User>> {
   static class Users<T extends User> extends PaginatedCollection<T> {

      @ConstructorProperties({ "users", "users_links" })
      protected Users(Iterable<T> users, Iterable<Link> users_links) {
         super(users, users_links);
      }

   }

   @Inject
   public ParseUsers(Json json) {
      super(json, new TypeLiteral<Users<? extends User>>() {
      });
   }

   public static class ToPagedIterable extends CallerArg0ToPagedIterable<User, ToPagedIterable> {

      private final KeystoneApi api;

      @Inject
      protected ToPagedIterable(KeystoneApi api) {
         this.api = checkNotNull(api, "api");
      }

      @Override
      protected Function<Object, IterableWithMarker<User>> markerToNextForCallingArg0(final String ignored) {
         final UserApi userApi = api.getUserApi().get();
         return new Function<Object, IterableWithMarker<User>>() {

            @SuppressWarnings("unchecked")
            @Override
            public IterableWithMarker<User> apply(Object input) {
               return IterableWithMarker.class.cast(userApi.list(marker(input.toString())));
            }

            @Override
            public String toString() {
               return "listUsers()";
            }
         };
      }

   }

}
