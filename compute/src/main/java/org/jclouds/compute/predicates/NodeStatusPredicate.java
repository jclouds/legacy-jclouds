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
package org.jclouds.compute.predicates;

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.logging.Logger;

import com.google.common.base.Predicate;

/**
 * Tests to see if node has reached status.
 * 
 * @author Everett Toews
 */
public class NodeStatusPredicate implements Predicate<String> {
   private final ComputeService computeService;
   private final NodeMetadata.Status status;    

   @javax.annotation.Resource
   protected Logger logger = Logger.NULL;

   public NodeStatusPredicate(ComputeService computeService, NodeMetadata.Status status) {
      this.computeService = computeService;
      this.status = status;
   }

   /**
    * @param node Works with a node identified by ZoneAndId
    * @return boolean Return true when the server reaches status, false otherwise
    */
   public boolean apply(String id) {
      NodeMetadata nodeUpdated = computeService.getNodeMetadata(id);
         
      logger.trace("looking for server: %s status: %s current: %s",
                   id, status, nodeUpdated.getStatus());
         
      return status.equals(nodeUpdated.getStatus());
   }
}
