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

import java.net.URI;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.aws.ec2.EC2;
import org.jclouds.aws.ec2.EC2AsyncClient;
import org.jclouds.aws.ec2.EC2Client;
import org.jclouds.aws.ec2.compute.EC2ComputeService;
import org.jclouds.aws.reference.AWSConstants;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.internal.ComputeServiceContextImpl;
import org.jclouds.lifecycle.Closer;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

/**
 * Configures the {@link EC2ComputeServiceContext}; requires {@link EC2ComputeService} bound.
 * 
 * @author Adrian Cole
 */
public class EC2ComputeServiceContextModule extends AbstractModule {

   @Override
   protected void configure() {
      bind(ComputeService.class).to(EC2ComputeService.class).asEagerSingleton();
   }

   @Provides
   @Singleton
   ComputeServiceContext<EC2AsyncClient, EC2Client> provideContext(Closer closer,
            ComputeService computeService, EC2AsyncClient asynchApi, EC2Client defaultApi,
            @EC2 URI endPoint, @Named(AWSConstants.PROPERTY_AWS_ACCESSKEYID) String account) {
      return new ComputeServiceContextImpl<EC2AsyncClient, EC2Client>(closer, computeService,
               asynchApi, defaultApi, endPoint, account);
   }

}
