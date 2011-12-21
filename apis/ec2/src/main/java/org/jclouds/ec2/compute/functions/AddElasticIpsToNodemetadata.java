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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.concurrent.ExecutionException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.aws.util.AWSUtils;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.ec2.compute.domain.RegionAndName;

import com.google.common.base.Function;
import com.google.common.base.Throwables;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableSet;

/**
 * This class searches for elastic ip addresses that are associated with the node, and adds them to
 * the publicIpAddress collection if present.
 * 
 * @author Adrian Cole
 */
@Singleton
public class AddElasticIpsToNodemetadata implements Function<NodeMetadata, NodeMetadata> {

   private final LoadingCache<RegionAndName, String> cache;

   @Inject
   protected AddElasticIpsToNodemetadata(@Named("ELASTICIP") LoadingCache<RegionAndName, String> cache) {
      this.cache = checkNotNull(cache, "cache");
   }

   // Note: Instances only have one Internet routable IP address. When an Elastic IP is associated to an
   // instance, the instance's existing Public IP address mapping is removed and is no longer valid for this instance
   // http://aws.amazon.com/articles/1346

   // TODO can there be multiple elastic ips on one instance?
   @Override
   public NodeMetadata apply(NodeMetadata arg0) {
      String[] parts = AWSUtils.parseHandle(arg0.getId());
      String region = parts[0];
      String instanceId = parts[1];
      try {
         String publicIp = cache.get(new RegionAndName(region, instanceId));
         // Replace existing public addresses with elastic IP (see note above)
         return NodeMetadataBuilder.fromNodeMetadata(arg0)
                 .publicAddresses(ImmutableSet.<String> builder().add(publicIp).build()).build();
      } catch (CacheLoader.InvalidCacheLoadException e) {
         // no ip was found
         return arg0;
      } catch (ExecutionException e) {
         throw Throwables.propagate(e);
      }
   }

}