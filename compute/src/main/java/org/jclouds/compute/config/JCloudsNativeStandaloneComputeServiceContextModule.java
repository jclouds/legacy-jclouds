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

package org.jclouds.compute.config;

import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.domain.Location;

import com.google.common.base.Function;
import com.google.inject.TypeLiteral;

/**
 * 
 * @author Adrian Cole
 */
public class JCloudsNativeStandaloneComputeServiceContextModule extends
      StandaloneComputeServiceContextModule<NodeMetadata, Hardware, Image, Location> {
   private final Class<? extends ComputeServiceAdapter<NodeMetadata, Hardware, Image, Location>> adapter;

   public JCloudsNativeStandaloneComputeServiceContextModule(
         Class<? extends ComputeServiceAdapter<NodeMetadata, Hardware, Image, Location>> adapter) {
      this.adapter = adapter;
   }

   /**
    * This binds the converters to {@link IdentityFunction} as that ensure the same value is
    * returned.
    */
   @SuppressWarnings({ "unchecked", "rawtypes" })
   @Override
   protected void configure() {
      bind(new TypeLiteral<ComputeServiceAdapter<NodeMetadata, Hardware, Image, Location>>() {
      }).to(adapter);
      bind(IdentityFunction.class).toInstance(IdentityFunction.INSTANCE);
      bind(new TypeLiteral<Function<NodeMetadata, NodeMetadata>>() {
      }).to((Class) StandaloneComputeServiceContextModule.IdentityFunction.class);
      bind(new TypeLiteral<Function<Image, Image>>() {
      }).to((Class) StandaloneComputeServiceContextModule.IdentityFunction.class);
      bind(new TypeLiteral<Function<Hardware, Hardware>>() {
      }).to((Class) StandaloneComputeServiceContextModule.IdentityFunction.class);
      bind(new TypeLiteral<Function<Location, Location>>() {
      }).to((Class) StandaloneComputeServiceContextModule.IdentityFunction.class);
      super.configure();
   }

}