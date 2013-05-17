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
package org.jclouds.compute.config;


import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.domain.Location;
import org.jclouds.functions.IdentityFunction;

import com.google.common.base.Function;
import com.google.inject.TypeLiteral;

/**
 * 
 * @author Adrian Cole
 */
public class JCloudsNativeComputeServiceAdapterContextModule extends
         ComputeServiceAdapterContextModule<NodeMetadata, Hardware, Image, Location> {
   protected final Class<? extends ComputeServiceAdapter<NodeMetadata, Hardware, Image, Location>> adapter;

   public JCloudsNativeComputeServiceAdapterContextModule(
            Class<? extends ComputeServiceAdapter<NodeMetadata, Hardware, Image, Location>> adapter) {
      this.adapter = adapter;
   }

   /**
    * This binds the converters to {@link IdentityFunction} as that ensure the same value is
    * returned.
    */
   @SuppressWarnings("unchecked")
   @Override
   protected void configure() {
      bind(new TypeLiteral<ComputeServiceAdapter<NodeMetadata, Hardware, Image, Location>>() {
      }).to(adapter);
      bind(new TypeLiteral<Function<NodeMetadata, NodeMetadata>>() {
      }).to(Class.class.cast(IdentityFunction.class));
      bind(new TypeLiteral<Function<Image, Image>>() {
      }).to(Class.class.cast(IdentityFunction.class));
      bind(new TypeLiteral<Function<Hardware, Hardware>>() {
      }).to(Class.class.cast(IdentityFunction.class));
      bind(new TypeLiteral<Function<Location, Location>>() {
      }).to(Class.class.cast(IdentityFunction.class));
      super.configure();
   }

}
