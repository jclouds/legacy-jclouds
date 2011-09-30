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
package org.jclouds.byon.config;

import java.io.InputStream;
import java.net.URI;

import javax.inject.Singleton;

import org.jclouds.byon.Node;
import org.jclouds.byon.internal.BYONComputeServiceAdapter;
import org.jclouds.byon.suppliers.SupplyFromProviderURIOrNodesProperty;
import org.jclouds.compute.config.JCloudsNativeComputeServiceAdapterContextModule;
import org.jclouds.concurrent.SingleThreaded;
import org.jclouds.domain.Location;
import org.jclouds.location.Provider;
import org.jclouds.location.suppliers.OnlyLocationOrFirstZone;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.cache.Cache;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

/**
 * 
 * @author Adrian Cole
 */
@SuppressWarnings("unchecked")
@SingleThreaded
public class BYONComputeServiceContextModule extends
      JCloudsNativeComputeServiceAdapterContextModule<Supplier, Supplier> {

   public BYONComputeServiceContextModule() {
      super(Supplier.class, Supplier.class, BYONComputeServiceAdapter.class);
   }

   @Provides
   @Singleton
   Supplier provideApi(Supplier<Cache<String, Node>> in) {
      return in;
   }

   @Override
   protected void configure() {
      super.configure();
      bind(new TypeLiteral<Supplier<Location>>() {
      }).to(OnlyLocationOrFirstZone.class);
      bind(new TypeLiteral<Function<URI, InputStream>>() {
      }).to(SupplyFromProviderURIOrNodesProperty.class);
      bind(new TypeLiteral<Supplier<InputStream>>() {
      }).annotatedWith(Provider.class).to(SupplyFromProviderURIOrNodesProperty.class);
      bind(new TypeLiteral<Function<URI, InputStream>>() {
      }).to(SupplyFromProviderURIOrNodesProperty.class);
   }

}
