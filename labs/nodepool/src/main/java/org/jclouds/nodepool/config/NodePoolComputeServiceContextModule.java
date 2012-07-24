/*
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

package org.jclouds.nodepool.config;

import javax.annotation.Nullable;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.config.JCloudsNativeComputeServiceAdapterContextModule;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.domain.Location;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.nodepool.NodePoolApiMetadata;
import org.jclouds.nodepool.NodePoolComputeServiceAdapter;
import org.jclouds.nodepool.NodePoolComputeServiceContext;
import org.jclouds.nodepool.internal.JsonNodeMetadataStore;
import org.jclouds.nodepool.internal.NodeMetadataStore;

import com.google.common.base.Function;
import com.google.inject.TypeLiteral;

public class NodePoolComputeServiceContextModule extends JCloudsNativeComputeServiceAdapterContextModule {

   private static class NullCredentialsOverrider implements Function<Template, LoginCredentials> {

      @Override
      @Nullable
      public LoginCredentials apply(@Nullable Template input) {
         return null;
      }

   }

   public NodePoolComputeServiceContextModule() {
      super(NodePoolComputeServiceAdapter.class);
   }

   @Override
   protected void configure() {
      super.configure();
      bind(NodeMetadataStore.class).to(JsonNodeMetadataStore.class);
      bind(ApiMetadata.class).to(NodePoolApiMetadata.class);
      bind(ComputeServiceContext.class).to(NodePoolComputeServiceContext.class);
      install(new LocationsFromComputeServiceAdapterModule<NodeMetadata, Hardware, Image, Location>() {
      });

   }

   @Override
   protected void bindCredentialsOverriderFunction() {
      bind(new TypeLiteral<Function<Template, LoginCredentials>>() {
      }).to(NullCredentialsOverrider.class);
   }

}
