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
package org.jclouds.azure.management.compute.config;

import org.jclouds.azure.management.compute.AzureManagementComputeServiceAdapter;
import org.jclouds.azure.management.compute.functions.DeploymentToNodeMetadata;
import org.jclouds.azure.management.compute.functions.OSImageToImage;
import org.jclouds.azure.management.compute.functions.RoleSizeToHardware;
import org.jclouds.azure.management.domain.Deployment;
import org.jclouds.azure.management.domain.OSImage;
import org.jclouds.azure.management.domain.RoleSize;
import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.config.ComputeServiceAdapterContextModule;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;

import com.google.common.base.Function;
import com.google.inject.TypeLiteral;

/**
 * 
 * @author Gérald Pereira
 */
public class AzureManagementComputeServiceContextModule extends
         ComputeServiceAdapterContextModule<Deployment, RoleSize, OSImage, String> {

   @Override
   protected void configure() {
      super.configure();
      bind(new TypeLiteral<ComputeServiceAdapter<Deployment, RoleSize, OSImage, String>>() {
      }).to(AzureManagementComputeServiceAdapter.class);
      bind(new TypeLiteral<Function<OSImage, Image>>() {
      }).to(OSImageToImage.class);
      bind(new TypeLiteral<Function<RoleSize, Hardware>>() {
      }).to(RoleSizeToHardware.class);
      bind(new TypeLiteral<Function<Deployment, NodeMetadata>>() {
      }).to(DeploymentToNodeMetadata.class);
      
   }

}
