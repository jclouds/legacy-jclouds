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

package org.jclouds.byon.config;

import java.net.URI;
import java.util.Map;

import javax.inject.Singleton;

import org.jclouds.byon.Node;
import org.jclouds.byon.internal.BYONComputeServiceAdapter;
import org.jclouds.compute.config.JCloudsNativeComputeServiceAdapterContextModule;
import org.jclouds.concurrent.SingleThreaded;
import org.jclouds.location.Provider;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Provides;

/**
 * 
 * @author Adrian Cole
 */
@SuppressWarnings("rawtypes")
@SingleThreaded
public class BYONComputeServiceContextModule extends
      JCloudsNativeComputeServiceAdapterContextModule<Supplier, Supplier> {

   public BYONComputeServiceContextModule() {
      super(Supplier.class, Supplier.class, BYONComputeServiceAdapter.class);
   }

   @Provides
   @Singleton
   Supplier<Map<String, Node>> provideNodeList(@Provider URI uri) {
      // TODO parse uri into list, using yaml or something
      return Suppliers.<Map<String, Node>> ofInstance(ImmutableMap.<String, Node> of());
   }

   @Provides
   @Singleton
   Supplier provideApi(Supplier<Map<String, Node>> in) {
      return in;
   }

}
