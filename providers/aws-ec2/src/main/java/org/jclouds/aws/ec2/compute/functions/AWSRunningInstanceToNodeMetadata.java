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

import static com.google.common.base.Predicates.equalTo;
import static com.google.common.base.Predicates.not;
import static com.google.common.collect.Maps.filterValues;

import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.aws.ec2.domain.AWSRunningInstance;
import org.jclouds.collect.Memoized;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.domain.NodeState;
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
   protected AWSRunningInstanceToNodeMetadata(Map<InstanceState, NodeState> instanceToNodeState,
         Map<String, Credentials> credentialStore, Supplier<LoadingCache<RegionAndName, ? extends Image>> imageMap,
         @Memoized Supplier<Set<? extends Location>> locations, @Memoized Supplier<Set<? extends Hardware>> hardware) {
      super(instanceToNodeState, credentialStore, imageMap, locations, hardware);
   }

   @Override
   protected void addCredentialsForInstance(NodeMetadataBuilder builder, RunningInstance instance) {
      LoginCredentials creds = LoginCredentials.builder(
            credentialStore.get("node#" + instance.getRegion() + "/" + instance.getId())).build();
      String spotRequestId = AWSRunningInstance.class.cast(instance).getSpotInstanceRequestId();
      if (creds == null && spotRequestId != null) {
         creds = LoginCredentials.builder(credentialStore.get("node#" + instance.getRegion() + "/" + spotRequestId))
               .build();
         if (creds != null)
            credentialStore.put("node#" + instance.getRegion() + "/" + instance.getId(), creds);
      }
      if (creds != null)
         builder.credentials(creds);
   }

   @Override
   protected NodeMetadataBuilder buildInstance(RunningInstance instance, NodeMetadataBuilder builder) {
      Map<String, String> tags = AWSRunningInstance.class.cast(instance).getTags();
      return super.buildInstance(instance, builder).name(tags.get("Name")).tags(
               filterValues(tags, equalTo("")).keySet()).userMetadata(filterValues(tags, not(equalTo(""))));
   }
}
