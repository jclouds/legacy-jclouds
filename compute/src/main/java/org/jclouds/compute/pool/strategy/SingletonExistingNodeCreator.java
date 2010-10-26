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

package org.jclouds.compute.pool.strategy;

import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.pool.ConnectedNodeCreator;

/**
 * Inspired by work by Aslak Knutsen in the Arquillian project
 * 
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 */
public class SingletonExistingNodeCreator extends ConnectedNodeCreator {
   private String nodeId;

   public SingletonExistingNodeCreator(ComputeServiceContext context, String nodeId) {
      super(context);
      this.nodeId = nodeId;
   }

   @Override
   public NodeMetadata createNode() {
      return getComputeContext().getComputeService().getNodeMetadata(nodeId);
   }

   @Override
   public void destroyNode(NodeMetadata nodeMetadata) {
      // no op, don't destroy something we did not create.
   }
}
