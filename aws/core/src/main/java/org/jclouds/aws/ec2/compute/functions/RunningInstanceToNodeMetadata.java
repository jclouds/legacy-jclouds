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
package org.jclouds.aws.ec2.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.InetAddress;
import java.net.URI;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.aws.ec2.compute.domain.RegionTag;
import org.jclouds.aws.ec2.domain.Image;
import org.jclouds.aws.ec2.domain.InstanceState;
import org.jclouds.aws.ec2.domain.KeyPair;
import org.jclouds.aws.ec2.domain.RunningInstance;
import org.jclouds.aws.ec2.options.DescribeImagesOptions;
import org.jclouds.aws.ec2.services.AMIClient;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.domain.internal.NodeMetadataImpl;
import org.jclouds.compute.strategy.PopulateDefaultLoginCredentialsForImageStrategy;
import org.jclouds.domain.Credentials;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * @author Adrian Cole
 */
@Singleton
public class RunningInstanceToNodeMetadata implements Function<RunningInstance, NodeMetadata> {
   private static final Map<InstanceState, NodeState> instanceToNodeState = ImmutableMap
            .<InstanceState, NodeState> builder().put(InstanceState.PENDING, NodeState.PENDING)
            .put(InstanceState.RUNNING, NodeState.RUNNING).put(InstanceState.SHUTTING_DOWN,
                     NodeState.PENDING).put(InstanceState.TERMINATED, NodeState.TERMINATED).build();

   private final AMIClient amiClient;
   private final Map<RegionTag, KeyPair> credentialsMap;
   private final PopulateDefaultLoginCredentialsForImageStrategy credentialProvider;

   @Inject
   RunningInstanceToNodeMetadata(AMIClient amiClient, Map<RegionTag, KeyPair> credentialsMap,
            PopulateDefaultLoginCredentialsForImageStrategy credentialProvider) {
      this.amiClient = checkNotNull(amiClient, "amiClient");
      this.credentialsMap = checkNotNull(credentialsMap, "credentialsMap");
      this.credentialProvider = checkNotNull(credentialProvider, "credentialProvider");
   }

   @Override
   public NodeMetadata apply(RunningInstance instance) {
      String id = checkNotNull(instance, "instance").getId();
      String name = null; // user doesn't determine a node name;
      URI uri = null; // no uri to get rest access to host info
      Map<String, String> userMetadata = ImmutableMap.<String, String> of();
      String tag = instance.getKeyName().replaceAll("-[0-9]+", "");
      NodeState state = instanceToNodeState.get(instance.getInstanceState());

      Set<InetAddress> publicAddresses = nullSafeSet(instance.getIpAddress());
      Set<InetAddress> privateAddresses = nullSafeSet(instance.getPrivateIpAddress());

      Credentials credentials = new Credentials(getLoginAccountFor(instance), getPrivateKeyOrNull(
               instance, tag));

      String locationId = instance.getAvailabilityZone().toString();

      Map<String, String> extra = ImmutableMap.<String, String> of();

      return new NodeMetadataImpl(id, name, locationId, uri, userMetadata, tag, state,
               publicAddresses, privateAddresses, extra, credentials);
   }

   @VisibleForTesting
   String getPrivateKeyOrNull(RunningInstance instance, String tag) {
      KeyPair keyPair = credentialsMap.get(new RegionTag(instance.getRegion(), instance
               .getKeyName()));
      String privateKey = keyPair != null ? keyPair.getKeyMaterial() : null;
      return privateKey;
   }

   @VisibleForTesting
   String getLoginAccountFor(RunningInstance from) {
      Image image = Iterables.getOnlyElement(amiClient.describeImagesInRegion(from.getRegion(),
               DescribeImagesOptions.Builder.imageIds(from.getImageId())));
      return checkNotNull(credentialProvider.execute(image), "login from image: "
               + from.getImageId()).account;
   }

   Set<InetAddress> nullSafeSet(InetAddress in) {
      if (in == null) {
         return ImmutableSet.<InetAddress> of();
      }
      return ImmutableSet.<InetAddress> of(in);
   }

}