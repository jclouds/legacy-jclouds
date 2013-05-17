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
package org.jclouds.location.suppliers.derived;

import java.lang.reflect.UndeclaredThrowableException;
import java.net.URI;
import java.util.Map;
import java.util.Set;

import javax.inject.Singleton;

import org.jclouds.location.Region;
import org.jclouds.location.suppliers.RegionIdsSupplier;

import com.google.common.base.Supplier;
import com.google.common.base.Throwables;
import com.google.inject.Inject;

/**
 * as opposed to via properties, lets look up regions via api, as they are more likely to change
 */
@Singleton
public class RegionIdsFromRegionIdToURIKeySet implements RegionIdsSupplier {

   private final Supplier<Map<String, Supplier<URI>>> regionIdToURISupplier;

   @Inject
   protected RegionIdsFromRegionIdToURIKeySet(@Region Supplier<Map<String, Supplier<URI>>> regionIdToURISupplier) {
      this.regionIdToURISupplier = regionIdToURISupplier;
   }

   @Override
   public Set<String> get() {
      try {
         return regionIdToURISupplier.get().keySet();
      } catch (UndeclaredThrowableException e) {
         throw Throwables.propagate(e.getCause());
      }
   }
}
