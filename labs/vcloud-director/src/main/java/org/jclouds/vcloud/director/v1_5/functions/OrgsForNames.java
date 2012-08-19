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
package org.jclouds.vcloud.director.v1_5.functions;

import static com.google.common.collect.FluentIterable.from;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.vcloud.director.v1_5.domain.Reference;
import org.jclouds.vcloud.director.v1_5.domain.org.Org;
import org.jclouds.vcloud.director.v1_5.user.VCloudDirectorApi;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

/**
 * @author danikov
 */
@Singleton
public class OrgsForNames implements Function<Iterable<String>, Iterable<? extends Org>> {
   private final VCloudDirectorApi api;

   @Inject
   OrgsForNames(VCloudDirectorApi api) {
      this.api = api;
   }

   @Override
   public Iterable<? extends Org> apply(final Iterable<String> from) {
      return from(api.getOrgApi().list()).filter(new Predicate<Reference>() {
         @Override
         public boolean apply(Reference in) {
            return Iterables.contains(from, in.getName());
         }
      }).transform(new Function<Reference, Org>() {
         
         @Override
         public Org apply(Reference in) {
            return api.getOrgApi().get(in.getHref());
         }

      });
   }

}
