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
package org.jclouds.vcloud.director.v1_5.loaders;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.vcloud.director.v1_5.domain.Entity;
import org.jclouds.vcloud.director.v1_5.user.VCloudDirectorApi;

import com.google.common.cache.CacheLoader;

@Singleton
public class ResolveEntity extends CacheLoader<String, Entity> {
   private final VCloudDirectorApi api;

   @Inject
   public ResolveEntity(VCloudDirectorApi api) {
      this.api = checkNotNull(api, "api");
   }

   @Override
   public Entity load(String input) {
      return api.resolveEntity(checkNotNull(input, "urn"));
   }

   @Override
   public String toString() {
      return "resolveEntity()";
   }
}
