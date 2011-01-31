/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.compute.stub.config;

import java.util.concurrent.ConcurrentMap;

import javax.inject.Singleton;

import org.jclouds.compute.config.JCloudsNativeComputeServiceAdapterContextModule;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.concurrent.SingleThreaded;
import org.jclouds.domain.Location;
import org.jclouds.location.suppliers.OnlyLocationOrFirstZone;

import com.google.common.base.Supplier;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

/**
 * 
 * @author Adrian Cole
 */
@SuppressWarnings("rawtypes")
@SingleThreaded
public class StubComputeServiceContextModule extends
         JCloudsNativeComputeServiceAdapterContextModule<ConcurrentMap, ConcurrentMap> {

   public StubComputeServiceContextModule() {
      super(ConcurrentMap.class, ConcurrentMap.class, StubComputeServiceAdapter.class);
   }

   @Provides
   @Singleton
   ConcurrentMap provideApi(ConcurrentMap<String, NodeMetadata> in) {
      return in;
   }

   @Override
   protected void configure() {
      install(new StubComputeServiceDependenciesModule());
      bind(new TypeLiteral<Supplier<Location>>() {
      }).to(OnlyLocationOrFirstZone.class);
      super.configure();
   }
}
