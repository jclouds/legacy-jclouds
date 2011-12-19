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

import java.util.Map;
import java.util.NoSuchElementException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.aws.ec2.AWSEC2Client;
import org.jclouds.compute.strategy.GetNodeMetadataStrategy;
import org.jclouds.domain.Credentials;
import org.jclouds.ec2.compute.domain.RegionAndName;
import org.jclouds.ec2.compute.strategy.EC2DestroyNodeStrategy;

import com.google.common.cache.LoadingCache;
import com.google.common.collect.Iterables;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class AWSEC2DestroyNodeStrategy extends EC2DestroyNodeStrategy {

   protected final AWSEC2Client client;
   protected final Map<String, Credentials> credentialStore;

   @Inject
   protected AWSEC2DestroyNodeStrategy(AWSEC2Client client, GetNodeMetadataStrategy getNode,
            @Named("ELASTICIP") LoadingCache<RegionAndName, String> elasticIpCache,
            Map<String, Credentials> credentialStore) {
      super(client, getNode, elasticIpCache);
      this.client = checkNotNull(client, "client");
      this.credentialStore = checkNotNull(credentialStore, "credentialStore");
   }

   @Override
   protected void destroyInstanceInRegion(String id, String region) {
      String spotId = id;
      if (id.indexOf("sir-") != 0) {
         try {
            spotId = getOnlyElement(
                     Iterables.concat(client.getInstanceServices().describeInstancesInRegion(region, id)))
                     .getSpotInstanceRequestId();
            credentialStore.remove("node#" + region + "/" + spotId);
         } catch (NoSuchElementException e) {
         }
         super.destroyInstanceInRegion(id, region);
      } else {
         client.getSpotInstanceServices().cancelSpotInstanceRequestsInRegion(region, spotId);
         credentialStore.remove("node#" + region + "/" + id);
      }

   }
}
