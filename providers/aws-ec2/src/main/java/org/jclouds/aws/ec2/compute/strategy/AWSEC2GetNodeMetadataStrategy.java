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
package org.jclouds.aws.ec2.compute.strategy;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.getOnlyElement;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.aws.ec2.AWSEC2Client;
import org.jclouds.aws.ec2.domain.SpotInstanceRequest;
import org.jclouds.aws.ec2.functions.SpotInstanceRequestToAWSRunningInstance;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.ec2.compute.strategy.EC2GetNodeMetadataStrategy;
import org.jclouds.ec2.domain.RunningInstance;

import com.google.common.base.Function;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class AWSEC2GetNodeMetadataStrategy extends EC2GetNodeMetadataStrategy {

   private final AWSEC2Client client;
   private final SpotInstanceRequestToAWSRunningInstance spotConverter;

   @Inject
   protected AWSEC2GetNodeMetadataStrategy(AWSEC2Client client,
            Function<RunningInstance, NodeMetadata> runningInstanceToNodeMetadata,
            SpotInstanceRequestToAWSRunningInstance spotConverter) {
      super(client, runningInstanceToNodeMetadata);
      this.client = checkNotNull(client, "client");
      this.spotConverter = checkNotNull(spotConverter, "spotConverter");
   }

   @Override
   public RunningInstance getRunningInstanceInRegion(String region, String id) {
      if (id.indexOf("sir-") != 0)
         return super.getRunningInstanceInRegion(region, id);
      SpotInstanceRequest spot = getOnlyElement(client.getSpotInstanceServices().describeSpotInstanceRequestsInRegion(
               region, id));
      if (spot.getState() == SpotInstanceRequest.State.ACTIVE)
         return super.getRunningInstanceInRegion(region, spot.getInstanceId());
      else
         return spotConverter.apply(spot);
   }

}
