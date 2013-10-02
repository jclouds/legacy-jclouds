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
package org.jclouds.byon.config;

import java.io.InputStream;
import java.net.URI;

import org.jclouds.byon.internal.BYONComputeServiceAdapter;
import org.jclouds.byon.suppliers.SupplyFromProviderURIOrNodesProperty;
import org.jclouds.compute.config.JCloudsNativeComputeServiceAdapterContextModule;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.concurrent.SingleThreaded;
import org.jclouds.domain.Location;
import org.jclouds.location.Provider;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.inject.TypeLiteral;

/**
 * 
 * @author Adrian Cole
 */
@SingleThreaded
public class BYONComputeServiceContextModule extends JCloudsNativeComputeServiceAdapterContextModule {

   public BYONComputeServiceContextModule() {
      super(BYONComputeServiceAdapter.class);
   }

   @Override
   protected void configure() {
      super.configure();
      bind(new TypeLiteral<Function<URI, InputStream>>() {
      }).to(SupplyFromProviderURIOrNodesProperty.class);
      bind(new TypeLiteral<Supplier<InputStream>>() {
      }).annotatedWith(Provider.class).to(SupplyFromProviderURIOrNodesProperty.class);
      bind(new TypeLiteral<Function<URI, InputStream>>() {
      }).to(SupplyFromProviderURIOrNodesProperty.class);
      install(new LocationsFromComputeServiceAdapterModule<NodeMetadata, Hardware, Image, Location>() {
      });
   }
}
