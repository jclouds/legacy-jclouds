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

package org.jclouds.openstack.nova;

import java.net.URI;
import java.net.URL;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.PathParam;

import org.jclouds.concurrent.Timeout;
import org.jclouds.openstack.nova.domain.Addresses;
import org.jclouds.openstack.nova.domain.Flavor;
import org.jclouds.openstack.nova.domain.Image;
import org.jclouds.openstack.nova.domain.RebootType;
import org.jclouds.openstack.nova.domain.Server;
import org.jclouds.openstack.nova.options.CreateServerOptions;
import org.jclouds.openstack.nova.options.CreateSharedIpGroupOptions;
import org.jclouds.openstack.nova.options.ListOptions;
import org.jclouds.openstack.nova.options.RebuildServerOptions;
import org.jclouds.rest.ResourceNotFoundException;

import java.util.concurrent.Future;

/**
 * Provides access to OpenStack Nova via their REST API.
 * <p/>
 * All commands return a Future of the result from OpenStack Nova. Any exceptions incurred
 * during processing will be wrapped in an {@link ExecutionException} as documented in
 * {@link Future#get()}.
 * 
 * @see NovaAsyncClient
 * @see <a href="http://wiki.openstack.org/OpenStackAPI_1-1" />
 * @author Adrian Cole
 */
@Timeout(duration = 60, timeUnit = TimeUnit.SECONDS)
public interface NovaClient {

   /**
    * 
    * List all servers (IDs and names only)
    * 
    * This operation provides a list of servers associated with your identity. Servers that have been
    * deleted are not included in this list.
    * <p/>
    * in order to retrieve all details, pass the option {@link ListOptions#withDetails()
    * withDetails()}
    */
   Set<Server> listServers(ListOptions... options);

   /**
    * 
    * This operation returns details of the specified server.
    * 
    * @return null, if the server is not found
    * @see Server
    */
   Server getServer(@PathParam("id") int id);

   /**
    * 
    * This operation deletes a cloud server instance from the system.
    * <p/>
    * Note: When a server is deleted, all images created from that server are also removed.
    * 
    * @return false if the server is not found
    * @see Server
    */
   boolean deleteServer(@PathParam("id") int id);

   /**
    * The reboot function allows for either a soft or hard reboot of a server.
    * <p/>
    * Status Transition:
    * <p/>
    * ACTIVE - REBOOT - ACTIVE (soft reboot)
    * <p/>
    * ACTIVE - HARD_REBOOT - ACTIVE (hard reboot)
    * 
    * @param rebootType
    *           With a soft reboot, the operating system is signaled to restart, which allows for a
    *           graceful shutdown of all processes. A hard reboot is the equivalent of power cycling
    *           the server.
    */
   void rebootServer(int id, RebootType rebootType);

   /**
    * The resize function converts an existing server to a different flavor, in essence, scaling the
    * server up or down. The original server is saved for a period of time to allow rollback if
    * there is a problem. All resizes should be tested and explicitly confirmed, at which time the
    * original server is removed. All resizes are automatically confirmed after 24 hours if they are
    * not confirmed or reverted.
    * <p/>
    * Status Transition:
    * <p/>
    * ACTIVE - QUEUE_RESIZE - PREP_RESIZE - VERIFY_RESIZE
    * <p/>
    * ACTIVE - QUEUE_RESIZE - ACTIVE (on error)
    */
   void resizeServer(int id, int flavorId);

   /**
    * The resize function converts an existing server to a different flavor, in essence, scaling the
    * server up or down. The original server is saved for a period of time to allow rollback if
    * there is a problem. All resizes should be tested and explicitly confirmed, at which time the
    * original server is removed. All resizes are automatically confirmed after 24 hours if they are
    * not confirmed or reverted.
    * <p/>
    * Status Transition:
    * <p/>
    * VERIFY_RESIZE - ACTIVE
    */
   void confirmResizeServer(int id);

   /**
    * The resize function converts an existing server to a different flavor, in essence, scaling the
    * server up or down. The original server is saved for a period of time to allow rollback if
    * there is a problem. All resizes should be tested and explicitly reverted, at which time the
    * original server is removed. All resizes are automatically reverted after 24 hours if they are
    * not reverted or reverted.
    * <p/>
    * Status Transition:
    * <p/>
    * VERIFY_RESIZE - ACTIVE
    */
   void revertResizeServer(int id);

   /**
    * This operation asynchronously provisions a new server. The progress of this operation depends
    * on several factors including location of the requested image, network i/o, host load, and the
    * selected flavor. The progress of the request can be checked by performing a GET on /server/id,
    * which will return a progress attribute (0-100% completion). A password will be randomly
    * generated for you and returned in the response object. For security reasons, it will not be
    * returned in subsequent GET calls against a given server ID.
    * 
    * @param options
    *           - used to specify extra files, metadata, or ip parameters during server creation.
    */
   Server createServer(String name, String imageRef, String flavorRef, CreateServerOptions... options);

   /**
    * The rebuild function removes all data on the server and replaces it with the specified image.
    * Server ID and IP addresses remain the same.
    * <p/>
    * Status Transition:
    * <p/>
    * ACTIVE - REBUILD - ACTIVE
    * <p/>
    * ACTIVE - REBUILD - ERROR (on error)
    * <p/>
    * 
    * @param options
    *           - imageId is an optional argument. If it is not specified, the server is rebuilt
    *           with the original imageId.
    */
   void rebuildServer(int id, RebuildServerOptions... options);

   /**
    * This operation allows you to change the administrative password.
    * <p/>
    * Status Transition: ACTIVE - PASSWORD - ACTIVE
    * 
    */
   void changeAdminPass(int id, String adminPass);

   /**
    * This operation allows you to update the name of the server. This operation changes the name of
    * the server in the OpenStack Nova system and does not change the server host name itself.
    * <p/>
    * Status Transition: ACTIVE - PASSWORD - ACTIVE
    * 
    */
   void renameServer(int id, String newName);

   /**
    * 
    * List available flavors (IDs and names only)
    * 
    * in order to retrieve all details, pass the option {@link ListOptions#withDetails()
    * withDetails()}
    */
   Set<Flavor> listFlavors(ListOptions... options);

   /**
    * 
    * This operation returns details of the specified flavor.
    * 
    * @return null, if the flavor is not found
    * @see Flavor
    */
   Flavor getFlavor(int id);

   /**
    * 
    * List available images (IDs and names only)
    * 
    * in order to retrieve all details, pass the option {@link ListOptions#withDetails()
    * withDetails()}
    */
   Set<Image> listImages(ListOptions... options);

   /**
    * 
    * This operation returns details of the specified image.
    * 
    * @return null, if the image is not found
    * 
    * @see Image
    */
   Image getImage(int id);

   /**
    * 
    * This operation deletes an image from the system.
    * <p/>
    * Note: Images are immediately removed. Currently, there are no state transitions to track the
    * delete operation.
    * 
    * @return false if the image is not found
    * @see Image
    */
   boolean deleteImage(int id);

   /**
    * 
    * This operation creates a new image for the given server ID. Once complete, a new image will be
    * available that can be used to rebuild or create servers. Specifying the same image name as an
    * existing custom image replaces the image. The image creation status can be queried by
    * performing a GET on /images/id and examining the status and progress attributes.
    * 
    * Status Transition:
    * <p/>
    * QUEUED - PREPARING - SAVING - ACTIVE
    * <p/>
    * QUEUED - PREPARING - SAVING - FAILED (on error)
    * <p/>
    * Note: At present, image creation is an asynchronous operation, so coordinating the creation
    * with data quiescence, etc. is currently not possible.
    * 
    * @throws ResourceNotFoundException
    *            if the server is not found
    * @see Image
    */
   Image createImageFromServer(String imageName, int serverId);

   /**
    * List all server addresses
    * 
    * returns empty set if the server doesn't exist
    */
   Addresses getAddresses(int serverId);

   /**
    * List all public server addresses
    * 
    * returns empty set if the server doesn't exist
    */
   Set<String> listPublicAddresses(int serverId);

   /**
    * List all private server addresses
    * 
    * returns empty set if the server doesn't exist
    */
   Set<String> listPrivateAddresses(int serverId);

}
