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
package org.jclouds.openstack.nova.ec2;

import java.util.List;

import org.jclouds.ec2.EC2AsyncClient;
import org.jclouds.ec2.EC2Client;
import org.jclouds.ec2.EC2ContextBuilder;
import org.jclouds.ec2.compute.EC2ComputeServiceContext;
import org.jclouds.openstack.nova.ec2.config.NovaEC2ComputeServiceContextModule;
import org.jclouds.openstack.nova.ec2.config.NovaEC2RestClientModule;
import org.jclouds.providers.ProviderMetadata;

import com.google.inject.Module;

/**
 * 
 * @author Adrian Cole
 */
public class NovaEC2ContextBuilder extends EC2ContextBuilder<EC2Client, EC2AsyncClient, EC2ComputeServiceContext<EC2Client, EC2AsyncClient>, NovaEC2ApiMetadata> {

   public NovaEC2ContextBuilder(
         ProviderMetadata<EC2Client, EC2AsyncClient, EC2ComputeServiceContext<EC2Client, EC2AsyncClient>, NovaEC2ApiMetadata> providerMetadata) {
      super(providerMetadata);
   }

   public NovaEC2ContextBuilder(NovaEC2ApiMetadata apiMetadata) {
      super(apiMetadata);
   }

   @Override
   protected void addClientModule(List<Module> modules) {
      modules.add(new NovaEC2RestClientModule());
   }

   @Override
   protected void addContextModule(List<Module> modules) {
      modules.add(new NovaEC2ComputeServiceContextModule());
   }

}
