/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.aws.ec2.compute.config;

import javax.inject.Singleton;

import org.jclouds.aws.ec2.EC2AsyncClient;
import org.jclouds.aws.ec2.EC2Client;
import org.jclouds.aws.ec2.compute.EC2ComputeService;
import org.jclouds.aws.ec2.config.EC2ContextModule;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.internal.ComputeServiceContextImpl;
import org.jclouds.rest.RestContext;

import com.google.inject.Provides;

/**
 * Configures the {@link EC2ComputeServiceContext}; requires {@link EC2ComputeService} bound.
 * 
 * @author Adrian Cole
 */
public class EC2ComputeServiceContextModule extends EC2ContextModule {

   @Override
   protected void configure() {
      super.configure();
      bind(ComputeService.class).to(EC2ComputeService.class).asEagerSingleton();
   }

   @Provides
   @Singleton
   ComputeServiceContext provideContext(ComputeService computeService,
            RestContext<EC2AsyncClient, EC2Client> context) {
      return new ComputeServiceContextImpl<EC2AsyncClient, EC2Client>(computeService, context);
   }

}
