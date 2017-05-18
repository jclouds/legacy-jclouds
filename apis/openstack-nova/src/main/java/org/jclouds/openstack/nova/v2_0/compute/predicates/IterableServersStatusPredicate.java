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
package org.jclouds.openstack.nova.v2_0.compute.predicates;

import java.util.HashMap;
import java.util.Map;

import org.jclouds.logging.Logger;
import org.jclouds.openstack.nova.v2_0.domain.Server;
import org.jclouds.openstack.nova.v2_0.features.ServerApi;
import org.jclouds.openstack.v2_0.domain.Resource;
import org.jclouds.rest.ResourceNotFoundException;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

/**
 * Tests to see if ALL servers have reached status.
 * 
 * @author Everett Toews
 */
public class IterableServersStatusPredicate implements Predicate<Iterable<Resource>> {
   private final ServerApi serverApi;
   private final Server.Status status; 

   @javax.annotation.Resource
   protected Logger logger = Logger.NULL;

   public IterableServersStatusPredicate(ServerApi serverApi, Server.Status status) {
      this.serverApi = serverApi;
      this.status = status;
   }

   /**
    * @param servers Works with an Iterable set of Server or ServerCreated
    * @return boolean Return true when ALL servers reach status, false otherwise
    */
   public boolean apply(Iterable<Resource> servers) {
      try {
         FluentIterable<? extends Server> serversUpdated = serverApi.listInDetail().concat();     
         Map<String, Server> serversUpdatedMap = new HashMap<String, Server>();
         
         for (Server serverUpdated: serversUpdated) {
            serversUpdatedMap.put(serverUpdated.getId(), serverUpdated);
         }
         
         for (Resource server: servers) {
            Server serverUpdated = serversUpdatedMap.get(server.getId());
            
	        logger.trace("looking for server: %s status: %s current: %s",
	                     server.getId(), status, serverUpdated.getStatus());

            if (!status.equals(serverUpdated.getStatus())) {
               return false;
            }
         }
         
         return true;
      } 
      catch (ResourceNotFoundException e) {
         return false;
      }
   }
}
