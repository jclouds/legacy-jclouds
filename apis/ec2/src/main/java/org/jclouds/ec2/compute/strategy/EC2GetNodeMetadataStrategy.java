/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.ec2.compute.strategy;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.getOnlyElement;

import java.util.NoSuchElementException;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.aws.util.AWSUtils;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.strategy.GetNodeMetadataStrategy;
import org.jclouds.ec2.EC2Api;
import org.jclouds.ec2.domain.RunningInstance;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class EC2GetNodeMetadataStrategy implements GetNodeMetadataStrategy {

   private final EC2Api client;
   private final Function<RunningInstance, NodeMetadata> runningInstanceToNodeMetadata;

   @Inject
   protected EC2GetNodeMetadataStrategy(EC2Api client,
            Function<RunningInstance, NodeMetadata> runningInstanceToNodeMetadata) {
      this.client = checkNotNull(client, "client");
      this.runningInstanceToNodeMetadata = checkNotNull(runningInstanceToNodeMetadata, "runningInstanceToNodeMetadata");
   }

   @Override
   public NodeMetadata getNode(String id) {
      checkNotNull(id, "id");
      String[] parts = AWSUtils.parseHandle(id);
      String region = parts[0];
      String instanceId = parts[1];
      try {
         RunningInstance runningInstance = getRunningInstanceInRegion(region, instanceId);
         return runningInstanceToNodeMetadata.apply(runningInstance);
      } catch (NoSuchElementException e) {
         return null;
      }
   }

   public RunningInstance getRunningInstanceInRegion(String region, String id) {
      return getOnlyElement(Iterables.concat(client.getInstanceApi().get().describeInstancesInRegion(region, id)));
   }

}
