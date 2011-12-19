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

import java.util.concurrent.ExecutionException;

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
import org.jclouds.ec2.compute.domain.RegionAndName;
import org.jclouds.ec2.reference.EC2Constants;
import org.jclouds.logging.Logger;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

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
   protected final LoadingCache<RegionAndName, String> elasticIpCache;

   @Inject
   @Named(EC2Constants.PROPERTY_EC2_AUTO_ALLOCATE_ELASTIC_IPS)
   @VisibleForTesting
   boolean autoAllocateElasticIps = false;

   @Inject
   protected EC2DestroyNodeStrategy(EC2Client client, GetNodeMetadataStrategy getNode,
            @Named("ELASTICIP") LoadingCache<RegionAndName, String> elasticIpCache) {
      this.client = checkNotNull(client, "client");
      this.getNode = checkNotNull(getNode, "getNode");
      this.elasticIpCache = checkNotNull(elasticIpCache, "elasticIpCache");
   }

   @Override
   public NodeMetadata destroyNode(String id) {
      String[] parts = AWSUtils.parseHandle(id);
      String region = parts[0];
      String instanceId = parts[1];

      // TODO: can there be multiple?
      releaseAnyPublicIpForInstanceInRegion(instanceId, region);
      destroyInstanceInRegion(instanceId, region);
      return getNode.getNode(id);
   }

   protected void releaseAnyPublicIpForInstanceInRegion(String instanceId, String region) {
      if (!autoAllocateElasticIps)
         return;
      try {
         String ip = elasticIpCache.get(new RegionAndName(region, instanceId));
         logger.debug(">> disassociating elastic IP %s", ip);
         client.getElasticIPAddressServices().disassociateAddressInRegion(region, ip);
         logger.trace("<< disassociated elastic IP %s", ip);
         elasticIpCache.invalidate(new RegionAndName(region, instanceId));
         logger.debug(">> releasing elastic IP %s", ip);
         client.getElasticIPAddressServices().releaseAddressInRegion(region, ip);
         logger.trace("<< released elastic IP %s", ip);
      } catch (CacheLoader.InvalidCacheLoadException e) {
         // no ip was found
         return;
      } catch (ExecutionException e) {
         // don't propagate as we need to clean up the node regardless
         logger.warn(e, "error cleaning up elastic ip for instance %s/%s", region, instanceId);
      }

   }

   protected void destroyInstanceInRegion(String instanceId, String region) {
      client.getInstanceServices().terminateInstancesInRegion(region, instanceId);
   }
}
