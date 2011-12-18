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
package org.jclouds.ec2.compute.functions;

import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.aws.util.AWSUtils;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.ec2.EC2Client;
import org.jclouds.ec2.domain.PublicIpInstanceIdPair;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * This class searches for elastic ip addresses that are associated with the node, and adds them to
 * the publicIpAddress collection if present.
 * 
 * @author Adrian Cole
 */
@Singleton
public class AddElasticIpsToNodemetadata implements Function<NodeMetadata, NodeMetadata> {

   private final EC2Client client;

   @Inject
   AddElasticIpsToNodemetadata(EC2Client client) {
      this.client = client;
   }

   @Override
   public NodeMetadata apply(NodeMetadata arg0) {
      String[] parts = AWSUtils.parseHandle(arg0.getId());
      String region = parts[0];
      String instanceId = parts[1];

      Iterable<PublicIpInstanceIdPair> elasticIpsAssociatedWithNode = ipAddressPairsAssignedToInstance(region,
            instanceId);

      Set<String> publicIps = extractIpAddressFromPairs(elasticIpsAssociatedWithNode);

      return addPublicIpsToNode(publicIps, arg0);
   }

   @VisibleForTesting
   NodeMetadata addPublicIpsToNode(Set<String> publicIps, NodeMetadata arg0) {
      if (publicIps.size() > 0)
         arg0 = NodeMetadataBuilder.fromNodeMetadata(arg0).publicAddresses(
                  Iterables.concat(publicIps, arg0.getPublicAddresses())).build();
      return arg0;
   }

   @VisibleForTesting
   Set<String> extractIpAddressFromPairs(Iterable<PublicIpInstanceIdPair> elasticIpsAssociatedWithNode) {
      Set<String> publicIps = ImmutableSet.copyOf(Iterables.transform(elasticIpsAssociatedWithNode,
               new Function<PublicIpInstanceIdPair, String>() {

                  @Override
                  public String apply(PublicIpInstanceIdPair arg0) {
                     return arg0.getPublicIp();
                  }

               }));
      return publicIps;
   }

   @VisibleForTesting
   Iterable<PublicIpInstanceIdPair> ipAddressPairsAssignedToInstance(String region, final String instanceId) {
      Iterable<PublicIpInstanceIdPair> elasticIpsAssociatedWithNode = Iterables.filter(client
               .getElasticIPAddressServices().describeAddressesInRegion(region),
               new Predicate<PublicIpInstanceIdPair>() {

                  @Override
                  public boolean apply(PublicIpInstanceIdPair in) {
                     return instanceId.equals(in.getInstanceId());
                  }
               });
      return elasticIpsAssociatedWithNode;
   }

}