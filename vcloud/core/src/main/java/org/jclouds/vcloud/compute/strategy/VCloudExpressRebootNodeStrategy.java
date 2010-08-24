/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.vcloud.compute.strategy;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.strategy.GetNodeMetadataStrategy;
import org.jclouds.compute.strategy.RebootNodeStrategy;
import org.jclouds.vcloud.VCloudExpressClient;
import org.jclouds.vcloud.domain.Task;

import com.google.common.base.Predicate;

/**
 * @author Adrian Cole
 */
@Singleton
public class VCloudExpressRebootNodeStrategy implements RebootNodeStrategy {
   private final VCloudExpressClient client;
   protected final Predicate<URI> taskTester;
   protected final GetNodeMetadataStrategy getNode;

   @Inject
   protected VCloudExpressRebootNodeStrategy(VCloudExpressClient client, Predicate<URI> taskTester,
            GetNodeMetadataStrategy getNode) {
      this.client = client;
      this.taskTester = taskTester;
      this.getNode = getNode;
   }

   @Override
   public NodeMetadata execute(String in) {
      URI id = URI.create(checkNotNull(in, "node.id"));
      Task task = client.resetVApp(id);
      taskTester.apply(task.getHref());
      return getNode.execute(in);
   }
}