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
package org.jclouds.ec2.compute.strategy;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Iterator;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.aws.util.AWSUtils;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.strategy.DestroyNodeStrategy;
import org.jclouds.compute.strategy.GetNodeMetadataStrategy;
import org.jclouds.ec2.EC2Client;
import org.jclouds.ec2.reference.EC2Constants;
import org.jclouds.logging.Logger;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class EC2DestroyNodeStrategy implements DestroyNodeStrategy {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;
   protected final EC2Client client;
   protected final GetNodeMetadataStrategy getNode;
   @Inject
   @Named(EC2Constants.PROPERTY_EC2_AUTO_ALLOCATE_ELASTIC_IPS)
   boolean autoAllocateElasticIps = false;

   @Inject
   protected EC2DestroyNodeStrategy(EC2Client client, GetNodeMetadataStrategy getNode) {
      this.client = checkNotNull(client, "client");
      this.getNode = checkNotNull(getNode, "getNode");
   }

   @Override
   public NodeMetadata destroyNode(String id) {
      String[] parts = AWSUtils.parseHandle(id);
      String region = parts[0];
      String instanceId = parts[1];
      Set<String> publicIps = getNode.getNode(id).getPublicAddresses();

      releaseElasticIpInRegion(region, instanceId, publicIps);
      destroyInstanceInRegion(region, instanceId);
      return getNode.getNode(id);
   }

   protected void releaseElasticIpInRegion(String region, String instanceId, Set<String> publicIps) {
       if (!autoAllocateElasticIps)
           return;

       Iterator<String> it = publicIps.iterator();
       while (it.hasNext()) {
          String publicIp = (String)it.next();
          client.getElasticIPAddressServices().disassociateAddressInRegion(region, publicIp);
          client.getElasticIPAddressServices().releaseAddressInRegion(region, publicIp);
       }
   }
   protected void destroyInstanceInRegion(String region, String instanceId) {
      client.getInstanceServices().terminateInstancesInRegion(region, instanceId);
   }
}
