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
package org.jclouds.location.suppliers.implicit;

import java.net.URI;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.location.Provider;
import org.jclouds.location.Region;
import org.jclouds.location.suppliers.ImplicitRegionIdSupplier;
import org.jclouds.suppliers.SupplyKeyMatchingValueOrNull;

import com.google.common.base.Supplier;

@Singleton
public class GetRegionIdMatchingProviderURIOrNull extends SupplyKeyMatchingValueOrNull<String, URI> implements
         ImplicitRegionIdSupplier {

   @Inject
   public GetRegionIdMatchingProviderURIOrNull(@Region Supplier<Map<String, Supplier<URI>>> supplier,
            @Provider Supplier<URI> providerUri) {
      super(supplier, providerUri);
   }

}
