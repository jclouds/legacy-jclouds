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
package org.jclouds.location.predicates.fromconfig;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.location.predicates.RegionIdFilter;
import org.jclouds.location.suppliers.fromconfig.RegionIdsFromConfiguration;

/**
 * 
 * If there are regions configured in {@link RegionIdsFromConfiguration}, return
 * true if that set contains the input param. If there aren't, return true.
 * 
 * @author Adrian Cole
 */
@Singleton
public class AnyOrConfiguredRegionId implements RegionIdFilter {

   private RegionIdsFromConfiguration idsInConfigSupplier;

   @Inject
   protected AnyOrConfiguredRegionId(RegionIdsFromConfiguration idsInConfigSupplier) {
      this.idsInConfigSupplier = checkNotNull(idsInConfigSupplier, "idsInConfigSupplier");
   }

   @Override
   public boolean apply(String input) {
      Set<String> idsInConfig = idsInConfigSupplier.get();
      if (idsInConfig.size() == 0)
         return true;
      return idsInConfig.contains(input);
   }
   
   @Override
   public String toString() {
      return "anyOrConfiguredRegionId(" + idsInConfigSupplier + ")";
   }

}
