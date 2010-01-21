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

import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.aws.domain.Region;
import org.jclouds.aws.ec2.EC2AsyncClient;
import org.jclouds.aws.ec2.EC2Client;
import org.jclouds.aws.ec2.compute.EC2ComputeService;
import org.jclouds.aws.ec2.compute.EC2Size;
import org.jclouds.aws.ec2.compute.EC2Template;
import org.jclouds.aws.ec2.config.EC2ContextModule;
import org.jclouds.aws.ec2.util.EC2Utils;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.Architecture;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.internal.ComputeServiceContextImpl;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.rest.RestContext;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Sets;
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

   @Provides
   @Singleton
   SortedSet<EC2Size> provideSizes() {
      return ImmutableSortedSet.of(EC2Size.C1_MEDIUM, EC2Size.C1_XLARGE, EC2Size.M1_LARGE,
               EC2Size.M1_SMALL, EC2Size.M1_XLARGE, EC2Size.M2_2XLARGE, EC2Size.M2_4XLARGE);
   }

   private static class LogHolder {
      @Resource
      @Named(ComputeServiceConstants.COMPUTE_LOGGER)
      protected Logger logger = Logger.NULL;
   }

   @Provides
   @Singleton
   Set<EC2Template> provideTemplates(EC2Client client, SortedSet<EC2Size> sizes,
            Map<Architecture, Map<OperatingSystem, Map<Region, String>>> imageAmiIdMap,
            LogHolder holder) {
      Set<EC2Template> templates = Sets.newHashSet();
      holder.logger.debug(">> generating templates");
      for (EC2Size size : sizes) {
         for (Architecture architecture : imageAmiIdMap.keySet()) {
            if (size.supportsArchitecture(architecture)) {
               for (OperatingSystem operatingSystem : imageAmiIdMap.get(architecture).keySet()) {
                  for (Region region : imageAmiIdMap.get(architecture).get(operatingSystem)
                           .keySet()) {
                     String ami = imageAmiIdMap.get(architecture).get(operatingSystem).get(region);
                     templates.add(new EC2Template(client, imageAmiIdMap, size, operatingSystem,
                              region, EC2Utils.newImage(client, region, operatingSystem,
                                       architecture, ami)));
                  }
               }
            }
         }
      }
      holder.logger.debug("<< templates(%d)", templates.size());
      return templates;
   }

   @Provides
   @Singleton
   Map<Architecture, Map<OperatingSystem, Map<Region, String>>> provideimageAmiIdMap() {
      return ImmutableMap.<Architecture, Map<OperatingSystem, Map<Region, String>>> of(
               Architecture.X86_32,//
               ImmutableMap.<OperatingSystem, Map<Region, String>> builder().put(
                        OperatingSystem.UBUNTU,
                        ImmutableMap.of(Region.DEFAULT, "ami-1515f67c", Region.US_EAST_1,
                                 "ami-1515f67c", Region.US_WEST_1, "ami-7d3c6d38",
                                 Region.EU_WEST_1, "ami-a62a01d2")).put(
                        OperatingSystem.RHEL,
                        ImmutableMap.of(Region.DEFAULT, "ami-368b685f", Region.US_EAST_1,
                                 "ami-368b685f")).build(),//
               Architecture.X86_64,//
               ImmutableMap.<OperatingSystem, Map<Region, String>> builder().put(
                        OperatingSystem.UBUNTU,
                        ImmutableMap.of(Region.DEFAULT, "ami-ab15f6c2", Region.US_EAST_1,
                                 "ami-ab15f6c2", Region.US_WEST_1, "ami-7b3c6d3e",
                                 Region.EU_WEST_1, "ami-9a2a01ee")).build());// todo ami
   }
}
