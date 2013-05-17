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
package org.jclouds.openstack.nova.v2_0.features;

import com.google.common.base.Optional;
import java.util.Map;
import org.jclouds.collect.PagedIterable;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.openstack.keystone.v2_0.domain.PaginatedCollection;
import org.jclouds.openstack.nova.v2_0.domain.RebootType;
import org.jclouds.openstack.nova.v2_0.domain.Server;
import org.jclouds.openstack.nova.v2_0.domain.ServerCreated;
import org.jclouds.openstack.nova.v2_0.options.CreateServerOptions;
import org.jclouds.openstack.nova.v2_0.options.RebuildServerOptions;
import org.jclouds.openstack.v2_0.domain.Resource;
import org.jclouds.openstack.v2_0.options.PaginationOptions;

/**
 * Provides synchronous access to Server.
 * <p/>
 * 
 * @see ServerAsyncApi
 * @see <a href=
 *      "http://docs.openstack.org/api/openstack-compute/1.1/content/Servers-d1e2073.html"
 *      />
 * @author Adrian Cole
 */
public interface ServerApi {

   /**
    * List all servers (IDs, names, links)
    * 
    * @return all servers (IDs, names, links)
    */
   PagedIterable<? extends Resource> list();

   PaginatedCollection<? extends Resource> list(PaginationOptions options);

   /**
    * List all servers (all details)
    * 
    * @return all servers (all details)
    */
   PagedIterable<? extends Server> listInDetail();

   PaginatedCollection<? extends Server> listInDetail(PaginationOptions options);

   /**
    * List details of the specified server
    * 
    * @param id
    *           id of the server
    * @return server or null if not found
    */
   Server get(String id);

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
   ServerCreated create(String name, String imageRef, String flavorRef, CreateServerOptions... options);

   /**
    * Terminate and delete a server.
    * 
    * @param id
    *           id of the server
    * @return True if successful, False otherwise
    */
   boolean delete(String id);
  
   /**
    * Start a server
    * 
    * @param id
    *           id of the server
    */
   void start(String id);

   /**
    * Stop a server
    * 
    * @param id
    *           id of the server
    */
   void stop(String id);
   
   /**
    * Reboot a server.
    * 
    * @param id
    *           id of the server
    * @param rebootType
    *           The type of reboot to perform (Hard/Soft)
    */
   void reboot(String id, RebootType rebootType);

   /**
    * Resize a server to a new flavor size.
    * 
    * @param id
    *           id of the server
    * @param flavorId
    *           id of the new flavor to use
    */
   void resize(String id, String flavorId);

   /**
    * Confirm a resize operation.
    * 
    * @param id
    *           id of the server
    */
   void confirmResize(String id);

   /**
    * Revert a resize operation.
    * 
    * @param id
    *           id of the server
    */
   void revertResize(String id);

   /**
    * Rebuild a server.
    * 
    * @param id
    *           id of the server
    * @param options
    *           Optional parameters to the rebuilding operation.
    */
   void rebuild(String id, RebuildServerOptions... options);

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
   void rename(String id, String newName);

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
   
   /**
    * List all metadata for a server.
    * 
    * @param id
    *           id of the server
    *                      
    * @return the metadata as a Map<String, String> 
    */
   Map<String, String> getMetadata(String id);

   /**
    * Set the metadata for a server.
    * 
    * @param id
    *           id of the server
    * @param metadata
    *           a Map containing the metadata
    * @return the metadata as a Map<String, String> 
    */
   Map<String, String> setMetadata(String id, Map<String, String> metadata);
   
   /**
    * Update the metadata for a server.
    * 
    * @param id
    *           id of the server
    * @param metadata
    *           a Map containing the metadata
    * @return the metadata as a Map<String, String> 
    */
   Map<String, String> updateMetadata(String id, Map<String, String> metadata);
   
   /**
    * Update the metadata for a server.
    * 
    * @param id
    *           id of the image
    * @param metadata
    *           a Map containing the metadata
    * @return the value or null if not present
    */
   @Nullable
   String getMetadata(String id, String key);

   /**
    * Set a metadata item for a server.
    * 
    * @param id
    *           id of the image
    * @param key
    *           the name of the metadata item
    * @param value
    *           the value of the metadata item
    * @return the value you updated
    */
   String updateMetadata(String id, String key, String value);

   /**
    * Delete a metadata item from a server.
    * 
    * @param id
    *           id of the image
    * @param key
    *           the name of the metadata item
    */
   void deleteMetadata(String id, String key);
   
   
   /**
    * Get usage information about the server such as CPU usage, Memory and IO.
    * The information returned by this method is dependent on the hypervisor
    * in use by the OpenStack installation and whether that hypervisor supports
    * this method. More information can be found in the 
    * <a href="http://api.openstack.org/api-ref.html"> OpenStack API 
    * reference</a>. <br/>
    * At the moment the returned response is a generic map. In future versions 
    * of OpenStack this might be subject to change.
    * 
    * @param id
    *           id of the server
    * @return A Map containing the collected values organized by key - value.
    * @Beta
    */
    Optional<Map<String, String>> getDiagnostics(String id);


}
