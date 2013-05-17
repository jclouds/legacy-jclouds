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
package org.jclouds.trmk.vcloud_0_8.compute.strategy;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.strategy.GetNodeMetadataStrategy;
import org.jclouds.compute.strategy.RebootNodeStrategy;
import org.jclouds.compute.strategy.ResumeNodeStrategy;
import org.jclouds.compute.strategy.SuspendNodeStrategy;
import org.jclouds.logging.Logger;
import org.jclouds.trmk.vcloud_0_8.TerremarkVCloudClient;
import org.jclouds.trmk.vcloud_0_8.domain.Task;

import com.google.common.base.Predicate;

/**
 * @author Adrian Cole
 */
@Singleton
public class TerremarkVCloudLifeCycleStrategy implements RebootNodeStrategy, ResumeNodeStrategy, SuspendNodeStrategy {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final TerremarkVCloudClient client;
   protected final Predicate<URI> taskTester;
   protected final GetNodeMetadataStrategy getNode;

   @Inject
   protected TerremarkVCloudLifeCycleStrategy(TerremarkVCloudClient client, Predicate<URI> taskTester,
            GetNodeMetadataStrategy getNode) {
      this.client = client;
      this.taskTester = taskTester;
      this.getNode = getNode;
   }

   @Override
   public NodeMetadata rebootNode(String in) {
      Task task = client.resetVApp(URI.create(checkNotNull(in, "node.id")));
      return returnWhenTaskCompletes(in, task);
   }

   private NodeMetadata returnWhenTaskCompletes(String in, Task task) {
      taskTester.apply(task.getHref());
      return getNode.getNode(in);
   }

   @Override
   public NodeMetadata resumeNode(String in) {
      Task task = client.powerOnVApp(URI.create(checkNotNull(in, "node.id")));
      return returnWhenTaskCompletes(in, task);
   }

   @Override
   public NodeMetadata suspendNode(String in) {
      Task task = client.powerOffVApp(URI.create(checkNotNull(in, "node.id")));
      return returnWhenTaskCompletes(in, task);
   }

}
