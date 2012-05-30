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
package org.jclouds.aws.ec2.compute.functions;

import static org.jclouds.compute.util.ComputeServiceUtils.addMetadataAndParseTagsFromValuesOfEmptyString;

import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.aws.ec2.domain.AWSRunningInstance;
import org.jclouds.collect.Memoized;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.domain.NodeMetadata.Status;
import org.jclouds.compute.functions.GroupNamingConvention;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.ec2.compute.domain.RegionAndName;
import org.jclouds.ec2.compute.functions.RunningInstanceToNodeMetadata;
import org.jclouds.ec2.domain.InstanceState;
import org.jclouds.ec2.domain.RunningInstance;

import com.google.common.base.Supplier;
import com.google.common.cache.LoadingCache;

/**
 * @author Adrian Cole
 */
@Singleton
public class AWSRunningInstanceToNodeMetadata extends RunningInstanceToNodeMetadata {

   @Inject
   protected AWSRunningInstanceToNodeMetadata(Map<InstanceState, Status> instanceToNodeStatus,
         Map<String, Credentials> credentialStore, Supplier<LoadingCache<RegionAndName, ? extends Image>> imageMap,
         @Memoized Supplier<Set<? extends Location>> locations, @Memoized Supplier<Set<? extends Hardware>> hardware,
         GroupNamingConvention.Factory namingConvention) {
      super(instanceToNodeStatus, credentialStore, imageMap, locations, hardware, namingConvention);
   }

   @Override
   protected void addCredentialsForInstance(NodeMetadataBuilder builder, RunningInstance instance) {
      LoginCredentials creds = LoginCredentials.fromCredentials(credentialStore.get("node#" + instance.getRegion()
            + "/" + instance.getId()));
      String spotRequestId = AWSRunningInstance.class.cast(instance).getSpotInstanceRequestId();
      if (creds == null && spotRequestId != null) {
         creds = LoginCredentials.fromCredentials(credentialStore.get("node#" + instance.getRegion() + "/"
               + spotRequestId));
         if (creds != null)
            credentialStore.put("node#" + instance.getRegion() + "/" + instance.getId(), creds);
      }
      if (creds != null)
         builder.credentials(creds);
   }
   
   protected Hardware parseHardware(RunningInstance instance) {
      Hardware in = super.parseHardware(instance);
      if (in == null)
         return null;
      AWSRunningInstance awsInstance = AWSRunningInstance.class.cast(instance);
      return HardwareBuilder.fromHardware(in).hypervisor(awsInstance.getHypervisor().toString()).build();
   }

   @Override
   protected NodeMetadataBuilder buildInstance(RunningInstance instance, NodeMetadataBuilder builder) {
      AWSRunningInstance awsInstance = AWSRunningInstance.class.cast(instance);
      builder.name(awsInstance.getTags().get("Name"));
      addMetadataAndParseTagsFromValuesOfEmptyString(builder, awsInstance.getTags());
      return super.buildInstance(instance, builder);
   }
}
