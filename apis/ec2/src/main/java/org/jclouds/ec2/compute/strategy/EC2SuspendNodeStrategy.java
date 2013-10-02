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

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.aws.util.AWSUtils;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.strategy.GetNodeMetadataStrategy;
import org.jclouds.compute.strategy.SuspendNodeStrategy;
import org.jclouds.ec2.EC2Api;
import org.jclouds.ec2.features.InstanceApi;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class EC2SuspendNodeStrategy implements SuspendNodeStrategy {
   private final InstanceApi client;
   private final GetNodeMetadataStrategy getNode;

   @Inject
   protected EC2SuspendNodeStrategy(EC2Api client, GetNodeMetadataStrategy getNode) {
      this.client = client.getInstanceApi().get();
      this.getNode = getNode;
   }

   @Override
   public NodeMetadata suspendNode(String id) {
      String[] parts = AWSUtils.parseHandle(id);
      String region = parts[0];
      String instanceId = parts[1];
      client.stopInstancesInRegion(region, true, instanceId);
      return getNode.getNode(id);
   }

}
