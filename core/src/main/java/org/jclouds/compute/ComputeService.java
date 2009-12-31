/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.compute;

import java.util.SortedSet;

import org.jclouds.compute.domain.CreateServerResponse;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.Profile;
import org.jclouds.compute.domain.ServerIdentity;
import org.jclouds.compute.domain.ServerMetadata;

/**
 * 
 * @author Ivan Meredith
 * @author Adrian Cole
 */
public interface ComputeService {

   /**
    * List all servers available to the current user
    */
   SortedSet<ServerIdentity> listServers();
   
   /**
    * Find all servers matching the specified name
    */
   SortedSet<ServerIdentity> getServerByName(String name);
   
   /**
    * Create a new server given the name, profile, and image.
    * 
    */
   CreateServerResponse createServer(String name, Profile profile, Image image);

   /**
    * destroy the server.
    */
   void destroyServer(String id);
   
   /**
    * Find a server by its id
    */
   ServerMetadata getServerMetadata(String id);

}
