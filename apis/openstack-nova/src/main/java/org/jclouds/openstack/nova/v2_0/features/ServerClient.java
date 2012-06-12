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
package org.jclouds.openstack.nova.v2_0.features;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;
import org.jclouds.openstack.nova.v2_0.domain.RebootType;
import org.jclouds.openstack.nova.v2_0.domain.Server;
import org.jclouds.openstack.nova.v2_0.domain.ServerCreated;
import org.jclouds.openstack.nova.v2_0.options.CreateServerOptions;
import org.jclouds.openstack.nova.v2_0.options.RebuildServerOptions;
import org.jclouds.openstack.v2_0.domain.Resource;

/**
 * Provides synchronous access to Server.
 * <p/>
 * 
 * @see ServerAsyncClient
 * @see <a href=
 *      "http://docs.openstack.org/api/openstack-compute/1.1/content/Servers-d1e2073.html"
 *      />
 * @author Adrian Cole
 */
@Timeout(duration = 180, timeUnit = TimeUnit.SECONDS)
public interface ServerClient {

   /**
    * List all servers (IDs, names, links)
    * 
    * @return all servers (IDs, names, links)
    */
   Set<Resource> listServers();

   /**
    * List all servers (all details)
    * 
    * @return all servers (all details)
    */
   Set<Server> listServersInDetail();

   /**
    * List details of the specified server
    * 
    * @param id
    *           id of the server
    * @return server or null if not found
    */
   Server getServer(String id);

   /**
    * Create a new server
    * 
    * @param name
    *           name of the server to create
    * @param imageRef
    *           reference to the image for the server to use
    * @param flavorRef
    *           reference to the flavor to use when creating the server
    * @param options
    *           optional parameters to be passed into the server creation
    *           request
    * @return the newly created server
    */
   // blocking call
   @Timeout(duration = 10, timeUnit = TimeUnit.MINUTES)
   ServerCreated createServer(String name, String imageRef, String flavorRef, CreateServerOptions... options);

   /**
    * Terminate and delete a server.
    * 
    * @param id
    *           id of the server
    * @return True if successful, False otherwise
    */
   Boolean deleteServer(String id);

   /**
    * Reboot a server.
    * 
    * @param id
    *           id of the server
    * @param rebootType
    *           The type of reboot to perform (Hard/Soft)
    */
   void rebootServer(String id, RebootType rebootType);

   /**
    * Resize a server to a new flavor size.
    * 
    * @param id
    *           id of the server
    * @param flavorId
    *           id of the new flavor to use
    */
   void resizeServer(String id, String flavorId);

   /**
    * Confirm a resize operation.
    * 
    * @param id
    *           id of the server
    */
   void confirmResizeServer(String id);

   /**
    * Revert a resize operation.
    * 
    * @param id
    *           id of the server
    */
   void revertResizeServer(String id);

   /**
    * Rebuild a server.
    * 
    * @param id
    *           id of the server
    * @param options
    *           Optional paramaters to the rebuilding operation.
    */
   void rebuildServer(String id, RebuildServerOptions... options);

   /**
    * Change the administrative password to a server.
    * 
    * @param id
    *           id of the server
    * @param adminPass
    *           The new administrative password to use
    */
   void changeAdminPass(String id, String adminPass);

   /**
    * Rename a server.
    * 
    * @param id
    *           id of the server
    * @param newName
    *           The new name for the server
    */
   void renameServer(String id, String newName);

   /**
    * Create an image from a server.
    *
    * @param name
    *           The name of the new image
    * @param id
    *           id of the server
    *
    * @return ID of the new / updated image
    */
   String createImageFromServer(String name, String id);

}
